package DNA.sdk.dbpool;

import java.text.SimpleDateFormat;
import java.util.Date;

class PrintHelper {
	static String sdf = "yyyy-MM-dd HH:mm:ss.SSS";
	
	static void printErrorConsole(String str) {
		print(str, false);
	}
	static void printDebugConsole(String str) {
		print(str, true);
	}
	static void print(String str, boolean isDebug) {
		if(isDebug) 
			System.out.println(getNow() + str);
		else
			System.err.println(getNow() + str);
	}
	static void print() {
		System.out.println();
	}
	static String getNow() {
		return new SimpleDateFormat(sdf).format(new Date()) + " ";
	}
	
	public static void printError(String str) {
		printErrorConsole(str);
	}
	public static void printDebug(String str) {
		printDebugConsole(str);
	}
	
}