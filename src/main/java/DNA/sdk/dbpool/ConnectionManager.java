package DNA.sdk.dbpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接管理器
 * 
 * @author 12146
 *
 */
class ConnectionManager {
	String sql = "select count(*) from dual";
	private int interval = 1000;
	private static int count = 1;
	public boolean isBusy = false;
	private Connection conn;
	private DBPool pool;
	private boolean stop = false;
	public ConnectionManager(DBPool pool, Connection conn) {
		this.conn = conn;
		this.pool = pool;
		Thread tt = new Thread(this::run, "ConnPool_"+(++count));
		tt.setDaemon(true);
		tt.start();
	}
	
	private void run() {
		while(!stop) {
			try {
				ping();
				Thread.sleep(interval);
//				PrintHelper.printDebug(Thread.currentThread().getName() + " conn is running......");
			} catch (SQLException e) {
//				PrintHelper.printError(Thread.currentThread().getName() + "conn is disconnected, start to reOpen.");
				reOpenConn();
				break;
			} catch (InterruptedException e) {
			}
		}
	}
	private void ping() throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
				stmt = null;
			}
		}
	}
	
	private void reOpenConn() {
		pool.reOpen(conn);
	}
	
	public Connection getConn() {
		return conn;
	}
	
	public void close() {
		stop = true;
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
}