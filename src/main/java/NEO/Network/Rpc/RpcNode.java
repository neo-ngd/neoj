package NEO.Network.Rpc;

import java.io.IOException;

import NEO.*;
import NEO.Core.*;
import NEO.IO.Serializable;
import NEO.IO.Json.*;
import NEO.Wallets.Wallet;

public class RpcNode
{
	private final RpcClient rpc;
	
	public RpcNode(RpcClient rpc)
	{
		this.rpc = rpc;
	}
	
	public UInt256 getBestBlockHash() throws RpcException, IOException
	{
		JObject result = rpc.call("getbestblockhash");
		return UInt256.parse(result.asString());
	}
	
	public Block getBlock(UInt256 hash) throws RpcException, IOException
	{
		JObject result = rpc.call("getblock", new JString(hash.toString()));
		try
		{
			return Serializable.from(Helper.hexToBytes(result.asString()), Block.class);
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public Block getBlock(int index) throws RpcException, IOException
	{
		JObject result = rpc.call("getblock", new JNumber(index));
		try
		{
			return Serializable.from(Helper.hexToBytes(result.asString()), Block.class);
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public int getBlockCount() throws RpcException, IOException
	{
		JObject result = rpc.call("getblockcount");
		return (int)result.asNumber();
	}
	
	public UInt256 getBlockHash(int index) throws RpcException, IOException
	{
		JObject result = rpc.call("getblockhash", new JNumber(index));
		return UInt256.parse(result.toString());
	}
	
	public int getConnectionCount() throws RpcException, IOException
	{
		JObject result = rpc.call("getconnectioncount");
		return (int)result.asNumber();
	}
	
	public UInt256[] getRawMemPool() throws RpcException, IOException
	{
		JObject result = rpc.call("getrawmempool");
		return ((JArray)result).stream().map(p -> UInt256.parse(p.asString())).toArray(UInt256[]::new);
	}
	
	public Transaction getRawTransaction(UInt256 txid) throws RpcException, IOException
	{
		JObject result = rpc.call("getrawtransaction", new JString(txid.toString()));
		return Transaction.deserializeFrom(Helper.hexToBytes(result.asString()));
	}
	
	public TransactionOutput getTxOut(UInt256 txid, int n) throws RpcException, IOException
	{
		JObject result = rpc.call("gettxout", new JString(txid.toString()), new JNumber(n));
		TransactionOutput output = new TransactionOutput();
		output.assetId = UInt256.parse(result.get("asset").asString());
		output.value = Fixed8.parse(result.get("value").asString());
		output.scriptHash = Wallet.toScriptHash(result.get("address").asString());
		return output;
	}
	
	public boolean sendRawTransaction(Transaction tx) throws RpcException, IOException
	{
		JObject result = rpc.call("sendrawtransaction", new JString(Helper.toHexString(tx.toArray())));
		return result.asBoolean();
	}
}
