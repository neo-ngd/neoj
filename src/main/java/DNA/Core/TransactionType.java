package DNA.Core;

/**
 * list transaction types supported by DNA 
 */
public enum TransactionType {
    /**
     *  used for accounting
     */
    BookKeeping(0x00),
    /**
     *  used for accounting
     */
    IssueTransaction(0x01),
    /**
     *  
     */
    BookKeeper(0x02),
    /**
     * 
     */
    DataFile(0x12),
    /**
     * 
     */
    DeployCode(0xd0),
    /**
     *  
     */
    PrivacyPayload(0x20),
    /**
     *  
     */
    RegisterTransaction(0x40),
    /**
     *  used for transfering Transaction, this is 
     */
    TransferTransaction(0x80), 
    /**
     * used for storing certificate
     */
    RecordTransaction(0x81),
    
    /**
     * 账本状态资产
     */
    StateUpdateTransaction(0x90),
    
    /**
     * 账本状态资产控制
     */
    StateUpdaterTransaction(0x91),
    
    /**
     * 销毁资产
     */
    DestroyTransaction(0x18),
    
    ;

    private byte value;
    TransactionType(int v) {
        value = (byte)v;
    }
    public byte value() {
        return value;
    }

    public static TransactionType valueOf(byte v) {
    	for (TransactionType e : TransactionType.values()) {
    		if (e.value == v) {
    			return e;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}
