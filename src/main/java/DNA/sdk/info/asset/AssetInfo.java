package DNA.sdk.info.asset;

/**
 * 资产信息
 * 
 * @author 12146
 *
 */
public class AssetInfo {
//	public String assetid;		// 资产编号
//	public String assetname;	// 资产名称
//	public long regAmount;		// 注册数量
//	public String register;		// 注册人地址
//	public String controller;	// 控制人地址
	public String Name;
	public int Precision;
	public int AssetType;
	public int RecordType;
//	"Result":{"Name":"Token001","Precision":0,"AssetType":17,"RecordType":0}
	@Override
	public String toString() {
		return "AssetInfo [Name=" + Name + ", Precision=" + Precision
				+ ", AssetType=" + AssetType + ", RecordType=" + RecordType
				+ "]";
	}
	
	
}