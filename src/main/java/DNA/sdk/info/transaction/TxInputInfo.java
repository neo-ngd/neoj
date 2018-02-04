package DNA.sdk.info.transaction;

/**
 * 交易输入
 * 
 * @author 12146
 *
 */
public class TxInputInfo {
	public String address;	// 地址
	public String assetid;	// 资产编号
	public long amount;		// 资产数量
	
	@Override
	public String toString() {
		return "TxInputInfo [address=" + address + ", assetid=" + assetid
				+ ", amount=" + amount + "]";
	}
}