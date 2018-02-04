package DNA.Implementations.Wallets.SQLite;

class Coin {
	public byte[] txid;
	public int index;
	public byte[] assetId;
	public byte[] scriptHash;
	public int state;
	public long value;
}
