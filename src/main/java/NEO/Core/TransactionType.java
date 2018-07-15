package NEO.Core;

/**
 * list transaction types supported by NEO
 */
public enum TransactionType {
    /**
     *  
     */
    MinerTransaction(0x00),
    /**
     *  
     */
    IssueTransaction(0x01),
    /**
     *  
     */
    ClaimTransaction(0x02),
    /**
     * 
     */
    DataFile(0x12),
    /**
     *  
     */
    EnrollmentTransaction(0x20),
    /**
     *  
     */
    RegisterTransaction(0x40),
    /**
     *  used for transfer global asset 
     */
    ContractTransaction(0x80), 
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
    PublishTransaction(0xd0),
    /**
     *  used for change state in blockchain, for example: transfer nep5 asset 
     */
    InvocationTransaction(0xd1),
    
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
