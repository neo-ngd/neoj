package DNA.Implementations.Wallets;

public enum AccountStoreType {
	SQLITE(0x01),
	MYSQL(0x02),
	ORACLE(0x02),
	
	;
	
	private byte value;
	
	public int value() {
		return value;
	}
	
	AccountStoreType(int value) {
		this.value = (byte)value;
	}
	
	public static AccountStoreType from(int type) {
		for(AccountStoreType tt: AccountStoreType.values()) {
			if(tt.value == type) {
				return tt;
			}
		}
		throw new IllegalArgumentException();
	}
}
