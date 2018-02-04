package DNA.sdk.info.asset;

public class UTXOInfo {
	public String Txid;
	public String Index;
	public long Value;
	
	@Override
	public String toString() {
		return "UTXOInfo [Txid=" + Txid + ", Index=" + Index + ", Value="
				+ Value + "]";
	}
}
