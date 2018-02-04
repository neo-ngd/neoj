package DNA.sdk.dbpool;

import java.sql.Connection;

public class DBResource {
	private static DBResource _instance = null;
	public static DBResource getInstance() {
		if(_instance == null) {
			initInstance();
		}
		return _instance;
	}
	private static synchronized void initInstance() {
		if(_instance == null) {
			_instance = new DBResource();
		}
	}
	private DBResource() {
		if(dbpool == null) {
			dbpool = new DBPool();
		}
	}
	
	private DBPool dbpool = new DBPool();
	public Connection getConnection() {
		return dbpool.getConnection();
	}
	public void freeConnection(Connection conn) {
		dbpool.freeConnection(conn);
	}
}
