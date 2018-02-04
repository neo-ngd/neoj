package DNA.sdk.dbpool;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DBPool implements Closeable{
	public static final String url = DBConsts.dbUrl;
    public static final String driver = DBConsts.dbDriver;  
    public static final String user = DBConsts.dbUser;  
    public static final String password = DBConsts.dbPassword; 
    private static final int connCount = DBConsts.dbPoolCount;
	private Map<String, ConnectionManager> connMap = new ConcurrentHashMap<String, ConnectionManager>();
	
	public DBPool() {
		init();
	}
	
	private void init() {
		if (loadDriver()) {
            createPool();
            PrintHelper.printDebug("dbpool init finished");
            return;
        }
		PrintHelper.printError("Load database driver failed, please to check your db'driver...");
	}
	
	private boolean loadDriver() {
		try {
            DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
            return true;
        } catch (Exception e) {
        	PrintHelper.printError(" Load Database driver fail : " + e);
        }
        return false;
	}
	private void createPool() {
		for(int i=0; i<connCount; ++i) {
			ConnectionManager connMgr = createConn();
			if(connMgr == null) {
				sleep();
				--i; continue;
			}
			connMap.put(connMgr.getConn().toString(), connMgr);
		}
		
	}
	private ConnectionManager createConn() {
		Connection localConn = null;
		try {
			localConn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			PrintHelper.printError("CreatePool faild..., PooledConnection is null");
		}
		if(localConn != null) 
			return new ConnectionManager(this, localConn);
		else 
			return null;
		
	}
	
	public void reOpen(Connection conn) {
		if(conn != null) {
			ConnectionManager oconMgr = connMap.remove(conn.toString());
			if(oconMgr != null) {
				oconMgr.close();
			}
			ConnectionManager connMgr = null;
			while(true) {
				connMgr = createConn();
				if(connMgr != null) {
					break;
				}
//				PrintHelper.printError("reOpenConn failed, continue reOpen after 1(s).....count:"+(++count));
				sleep();
				
			} 
			connMap.put(connMgr.getConn().toString(), connMgr);
		}
	}
	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
	
	public Connection getConnection() {
		ConnectionManager connMgr = null;
		while(true){
			connMgr = connMap.values().stream().filter(p -> !p.isBusy).findAny().orElse(null);
			if(connMgr != null) {
				connMgr.isBusy = true;
				break;
			}
			PrintHelper.printError("Not find spare conn, please wait 1(s).....");
			sleep();
		} 
		return connMgr.getConn();
	}
	public void freeConnection(Connection conn) {
		if(connMap.containsKey(conn.toString())) {
			ConnectionManager connMgr = connMap.get(conn.toString());
			connMgr.isBusy = false;
			connMap.put(conn.toString(), connMgr);
		}
	}
	public void close() {
		connMap.values().stream().forEach(p -> p.close());
	}
	
}