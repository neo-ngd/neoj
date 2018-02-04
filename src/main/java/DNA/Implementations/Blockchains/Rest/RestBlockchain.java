package DNA.Implementations.Blockchains.Rest;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Stream;

import DNA.Fixed8;
import DNA.UInt256;
import DNA.Core.Block;
import DNA.Core.Blockchain;
import DNA.Core.BlockchainAbility;
import DNA.Core.Claimable;
import DNA.Core.RegisterTransaction;
import DNA.Core.Transaction;
import DNA.Core.TransactionOutput;
import DNA.Core.Vote;
import DNA.Network.Rest.RestNode;

public class RestBlockchain extends Blockchain {
	private RestNode rest;
	public RestBlockchain(RestNode rest) {
		this.rest = rest;
	}
	@Override
	public EnumSet<BlockchainAbility> ability() {
		return BlockchainAbility.None;
	}

	@Override
	public int height() throws Exception {
		return rest.getBlockHeight();
	}
	
	@Override
	public Block getBlock(int height) throws Exception {
		return rest.getBlock(height);
	}
	
	public int getBlockHeightFromDb() throws Exception {
		return rest.getBlockHeightFromDb();
	}
	@Override
	public Block getBlockFromDb(int height) throws Exception {
		return rest.getBlockFromDb(height);
	}
	 
	@Override
    public Block getBlock(UInt256 hash) throws Exception {
		return rest.getBlock(hash.toString());
    }
    
    @Override
	public Transaction getTransaction(UInt256 hash) throws Exception {
		return rest.getRawTransaction(hash.toString());
    }

    @Override
	public UInt256 currentBlockHash() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    
	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean addBlock(Block block) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void addHeaders(Iterable<Block> headers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsUnspent(UInt256 hash, int index) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Stream<RegisterTransaction> getAssets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UInt256[] getLeafHeaderHashes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getNextBlock(UInt256 hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UInt256 getNextBlockHash(UInt256 hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Fixed8 getQuantityIssued(UInt256 asset_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSysFeeAmount(UInt256 hash) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<Short, Claimable> getUnclaimed(UInt256 hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionOutput getUnspent(UInt256 hash, int index)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<Vote> getVotes(Stream<Transaction> others) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDoubleSpend(Transaction tx) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public UInt256 getBlockHash(int height) {
		// TODO Auto-generated method stub
		return null;
	}

}
