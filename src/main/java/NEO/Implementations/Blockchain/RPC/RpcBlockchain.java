package NEO.Implementations.Blockchain.RPC;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import NEO.*;
import NEO.Core.*;
import NEO.Network.Rpc.*;

public class RpcBlockchain extends Blockchain
{
	private final RpcNode rpc;
	
	public RpcBlockchain(RpcNode rpc)
	{
		this.rpc = rpc;
	}
	
	@Override
	public EnumSet<BlockchainAbility> ability()
	{
		return BlockchainAbility.None;
	}

	@Override
	public UInt256 currentBlockHash() throws RpcException, IOException
	{
		return rpc.getBestBlockHash();
	}
	
	@Override
	public Block getBlock(int height) throws RpcException, IOException
	{
		return rpc.getBlock(height);
	}
	
	@Override
	public Block getBlock(UInt256 hash) throws RpcException, IOException
	{
		return rpc.getBlock(hash);
	}
	
	@Override
	public UInt256 getBlockHash(int height) throws RpcException, IOException
	{
		return rpc.getBlockHash(height);
	}
	
	@Override
	public Transaction getTransaction(UInt256 hash) throws RpcException, IOException
	{
		return rpc.getRawTransaction(hash);
	}

	@Override
	public int height() throws RpcException, IOException
	{
		return rpc.getBlockCount() - 1;
	}

	@Override
	public boolean isReadOnly()
	{
		return true;
	}

	@Override
	protected boolean addBlock(Block block)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void addHeaders(Iterable<Block> headers)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void close()
	{
	}

	@Override
	public boolean containsUnspent(UInt256 hash, int index) throws RpcException, IOException
	{
		return getUnspent(hash, index) != null;
	}

	@Override
	public Stream<RegisterTransaction> getAssets()
	{
		throw new UnsupportedOperationException();
	}

//	@Override
//	public Stream<EnrollmentTransaction> getEnrollments(Stream<Transaction> others)
//	{
//		throw new UnsupportedOperationException();
//	}

	@Override
	public UInt256[] getLeafHeaderHashes()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Block getNextBlock(UInt256 hash)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public UInt256 getNextBlockHash(UInt256 hash)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Fixed8 getQuantityIssued(UInt256 asset_id)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public long getSysFeeAmount(UInt256 hash)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<Short, Claimable> getUnclaimed(UInt256 hash)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public TransactionOutput getUnspent(UInt256 hash, int index) throws RpcException, IOException
	{
		return rpc.getTxOut(hash, index);
	}

	@Override
	public Stream<Vote> getVotes(Stream<Transaction> others)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDoubleSpend(Transaction tx)
	{
		throw new UnsupportedOperationException();
	}
}
