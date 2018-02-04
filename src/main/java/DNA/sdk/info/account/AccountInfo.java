package DNA.sdk.info.account;

/**
 * 账户信息
 * 
 * @author 12146
 *
 */
public class AccountInfo {
	public String address;	// 合约地址
	public String pubkey;	// 公钥
	public String prikey;	// 私钥
	public String priwif;	// 私钥wif
	public String pkhash;	// 公钥hash
	@Override
	public String toString() {
		return "AccountInfo [address=" + address + ", pubkey=" + pubkey
				+ ", prikey=" + prikey + ", priwif=" + priwif + ", pkhash="
				+ pkhash + "]";
	}
	
	
}