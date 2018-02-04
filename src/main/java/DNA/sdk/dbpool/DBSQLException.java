package DNA.sdk.dbpool;

public class DBSQLException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DBSQLException(String msg) {
		super(msg);
	}
	
	public DBSQLException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DBSQLException(Throwable t) {
		super(t);
	}

}