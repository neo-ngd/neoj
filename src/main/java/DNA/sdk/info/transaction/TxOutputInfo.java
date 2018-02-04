package DNA.sdk.info.transaction;

/**
 * 交易输出
 * 
 * @author 12146
 *
 */
public class TxOutputInfo {
	public String address;	// 地址
	public String assetid;	// 资产编号
	public long amount;		// 资产数量
	
	@Override
	public String toString() {
		return "TxOutputInfo [address=" + address + ", assetid=" + assetid
				+ ", amount=" + amount + "]";
	}
}