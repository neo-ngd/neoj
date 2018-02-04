package DNA.sdk.helper;

import com.alibaba.fastjson.JSON;

public class DataFormat {
	public static String getCodeMsg(String code, String message) {
		return ExceptionConst.Json_CodeMessage.replace(ExceptionConst.Flag_Code, code).replace(ExceptionConst.Flag_Message, message);
	}
	
	public static String getErrorDesc4ComposeTrfTransaction(String message) {
		return getErrorDesc(ExceptionConst.Code_getErrorDesc4ComposeTrfTransaction, message);
	}
	public static String getErrorDesc4ComposeIssTransaction(String message) {
		return getErrorDesc(ExceptionConst.Code_getErrorDesc4ComposeIssTransaction, message);
	}
	public static String getErrorDesc4DeserializeTransaction(String message) {
		return getErrorDesc(ExceptionConst.Code_Deserialize_err_Transaction, message);
	}
	public static String getErrorDesc4DeserializeBlock(String message) {
		return getErrorDesc(ExceptionConst.Code_Deserialize_err_Block, message);
	}
	public static String getErrorDesc4Encrypto(String message) {
		return getErrorDesc(ExceptionConst.Code_Encrypto, message);
	}
	public static String getErrorDesc4Decrypto(String message) {
		return getErrorDesc(ExceptionConst.Code_Decrypto, message);
	}
	public static String getErrorDesc4NoBalance(String message) {
		return getErrorDesc(ExceptionConst.Code_NoBalance, message);
	}
	public static String getErrorDesc4DatabaseErr(String message) {
		return getErrorDesc(ExceptionConst.Code_DatabaseErr, message);
	}
	public static String getErrorDesc4NetworkErr(String message) {
		return getErrorDesc(ExceptionConst.Code_NetWorkErr, message);
	}
	public static String getErrorDesc(long error, String message) {
		return String.format(ExceptionConst.Json_ErrorDesc, error, message);
	}
	
	public static void main(String[] args) {
		String ss = getCodeMsg("60001", "No available balance");
		CM cm = JSON.parseObject(ss, CM.class);
		System.out.println(cm);
		ss = getErrorDesc4ComposeIssTransaction("failed to send issvervice");
		ED ed = JSON.parseObject(ss, ED.class);
		System.out.println(ed);
		
	}
}
class CM {
	public String Code;
	public String Message;
	@Override
	public String toString() {
		return "CM [Code=" + Code + ", Message=" + Message + "]";
	}
}
class ED {
	public long Error;
	public String Desc;
	@Override
	public String toString() {
		return "ED [Error=" + Error + ", Desc=" + Desc + "]";
	}
}
class ExceptionConst {
	public static final String Flag_Code = "CCC";
	public static final String Flag_Message = "MMM";
	public static final String Json_CodeMessage = "{\"Code\":\"CCC\",\"Message\":\"MMM\"}";
	public static final String Json_ErrorDesc = "{\"Error\":%s,\"Desc\":\"%s\"}";
	public static final long Code_NetWorkErr = 60000;
	public static final long Code_DatabaseErr = 60001;
	public static final long Code_NoBalance = 60002;
	public static final long Code_Decrypto = 60003;
	public static final long Code_Encrypto = 60004;
	public static final long Code_Deserialize_err_Block = 60005;
	public static final long Code_Deserialize_err_Transaction = 60006;
	public static final long Code_getErrorDesc4ComposeIssTransaction = 60007;
	public static final long Code_getErrorDesc4ComposeTrfTransaction = 60008;
}

/**

异常代码：
60000 网络错误
60001 数据库操作错误
60002 余额不足
60003 解密错误
60004 加密错误
60005 反序列化Block错误
60006 反序列化Transaction错误
60007 分发组合交易错误
60008 资产注销交易错误


捕获到的异常可以通过获取异常信息查看异常代码及说明，异常信息为json格式，eg:
try {

} catch (CoinException ex) {
	String exMsg = ex.getMessage();
	ExMsg rr = JSON.parseObject(exMsg, ExMsg.class);
	// 判断异常代码
}
class ExMsg {
	public long Error;
	public String Desc;
}

 
 
 */
