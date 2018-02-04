package DNA.sdk.dbpool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class DBParamInitailer {
	private static final String dbName = "db.name";
	private static String dbUrl = "db.${}.url";
	private static String dbDriver = "db.${}.driver";
	private static String dbUser = "db.${}.user";
	private static String dbPswd = "db.${}.password";
	private static String dbPool = "db.${}.dbpool";
	
	public static void init(String dbUrl, String dbDriver, String dbUser, String dbPswd, int dbPool) {
		DBConsts.dbUrl = dbUrl;
		DBConsts.dbDriver = dbDriver;
		DBConsts.dbUser = dbUser;
		DBConsts.dbPassword = dbPswd;
		DBConsts.dbPoolCount = dbPool;
	}
	public static void init(String config) {
		initCfg(config);
	}
	private static void initCfg(String cfg) {
		Properties prop=new Properties();     
		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(cfg))) {
			prop.load(reader);  
			initDbPool(prop);
		} catch (IOException e ) {
			e.printStackTrace();
		}
	}
	private static void initDbPool(Properties prop) {
		String rr = prop.getProperty(dbName);
		
		DBConsts.dbUrl = prop.getProperty(replace(dbUrl, rr));
		DBConsts.dbDriver = prop.getProperty(replace(dbDriver, rr));
		DBConsts.dbUser = prop.getProperty(replace(dbUser, rr));
		DBConsts.dbPassword = prop.getProperty(replace(dbPswd, rr));
		String dbPoolCount = prop.getProperty(replace(dbPool, rr));
		int numInt = 1;
		try {
			numInt = Integer.parseInt(dbPoolCount);
		} catch (Exception e) {
			numInt = 3;
		}
		DBConsts.dbPoolCount = numInt;
	}
	
	private static String replace(String ss, String rr) {
//		return ss.replace("${}", rr);
		String r = ss.replace("${}", rr);
		System.out.println(r);
		return r;
	}
	
	public static void main(String[] args) {
		init("./control/db.ini");
		DBResource.getInstance();
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
