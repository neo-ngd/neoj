package DNA.Core;

import java.util.Arrays;
import java.util.HashSet;

import DNA.Fixed8;
import DNA.UInt160;

/**
 *  分发资产交易
 *  
 */
public class IssueTransaction extends Transaction {	
	
	public IssueTransaction() {
		super(TransactionType.IssueTransaction);
	}
	
	/**
     * 获取验证脚本
     */
	@Override
	public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        for (TransactionResult result : Arrays.stream(getTransactionResults()).filter(p -> p.amount.compareTo(Fixed8.ZERO) < 0).toArray(TransactionResult[]::new)) {
            Transaction tx;
			try {
				tx = Blockchain.current().getTransaction(result.assetId);
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
            if (tx == null || !(tx instanceof RegisterTransaction)) {
            	throw new IllegalStateException();
            }
            RegisterTransaction asset = (RegisterTransaction)tx;
            hashes.add(asset.admin);
        }
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
	
	@Override
	public String toString() {
		return "IssueTransaction [iss]";
	}
}
