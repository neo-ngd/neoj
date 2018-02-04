package DNA.Network.Rest;

import java.io.IOException;
import java.util.List;

import DNA.Helper;
import DNA.Core.Block;
import DNA.Core.Transaction;
import DNA.IO.Serializable;
import DNA.sdk.info.asset.UTXO2Info;
import DNA.sdk.info.asset.UTXOInfo;

import com.alibaba.fastjson.JSON;

public class RestNode {
	private RestClient restClient;
	private String accessToken="token001", authType="OAuth2.0";
	private String action = "sendrawtransaction",version = "v001",type = "t001";
	
	
	public RestNode(String restUrl) {
		restClient = new RestClient(restUrl);
		setAccessToken(accessToken);
	}
	
	public RestNode(String restUrl, String accessToken) {
		restClient = new RestClient(restUrl);
		setAccessToken(accessToken);
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	
	public boolean sendRawTransaction(String data) throws RestException {
		String rs = restClient.sendTransaction(authType, accessToken, action, version, type, data);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return true;
		}
		throw new RestRuntimeException(rr.toString());
	}
	
	public Transaction getRawTransaction(String txid) throws RestException {
		String rs = restClient.getTransaction(authType, accessToken, txid);	// DNA-195
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Transaction.deserializeFrom(Helper.hexToBytes(rr.Result));
			} catch (IOException e) {
				throw new RestRuntimeException("Transaction.fromJsonD(txid) failed", e);
			}
		}
		throw new RestRuntimeException(rr.toString());
	}
	
	public String getAsset(String assetid) throws RestException {
		String rs = restClient.getAsset(authType, accessToken, assetid); // DNA-195
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		return rr.Result;
	}
	
	public int getBlockHeight() throws RestException {
		String rs = restClient.getBlockHeight(authType, accessToken);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		return Integer.valueOf(rr.Result).intValue();
		
	}
	public Block getBlock(int height) throws RestException {
		String rs = restClient.getBlock(authType, accessToken, height);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		try {
			return Serializable.from(DNA.Helper.hexToBytes(rr.Result), Block.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RestRuntimeException("Block.deserialize(height) failed", e);
		}
	}
		
	public Block getBlock(String hash) throws RestException {
		String rs = restClient.getBlock(authType, accessToken, hash);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		try {
			return Serializable.from(DNA.Helper.hexToBytes(rr.Result), Block.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RestRuntimeException("Block.deserialize(hash) failed", e);
		}
		
	}
	
	public List<UTXOInfo> getUTXOs(String address, String assetid) throws RestException {
		String rs = restClient.getUTXOs(authType, accessToken, address, assetid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return JSON.parseArray(rr.Result, UTXOInfo.class);
		}
		throw new RestRuntimeException(rr.toString());
	}
	public List<UTXO2Info> getUTXO(String address) throws RestException {
		String rs = restClient.getUTXO(authType, accessToken, address);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return JSON.parseArray(rr.Result, UTXO2Info.class);
		}
		throw new RestRuntimeException(rr.toString());
	}
	
	public long getBalance(String address) throws RestException {
		String rs = restClient.getBalance(address, address, address);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Long.parseLong(rr.Result);
		}
		throw new RestRuntimeException(rr.toString());
	}
	
	public String getStateUpdate(String namespace, String key) throws RestException {
		String rs = restClient.getStateUpdate(authType, accessToken, namespace, key);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		return rr.Result;
	}
	
	
	public int getBlockHeightFromDb() throws RestException {
		String rs = restClient.getBlockHeightFromDb();
		Result4Block rr = JSON.parseObject(rs, Result4Block.class);
		if(rr.Error == 0) {
			return Integer.parseInt(rr.Result);
		}
		throw new RestRuntimeException(rr.toString());
	}
	public Block getBlockFromDb(int height) throws RestException {
		String rs = restClient.getBlockFromDb(height);
		Result4Block rr = JSON.parseObject(rs, Result4Block.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		try {
			return Serializable.from(DNA.Helper.hexToBytes(rr.Result), Block.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RestRuntimeException("Block.deserialize(hash) failed", e);
		}
	}
	
	// ********************************************************************************
	public Transaction getRawTransactionJson(String txid) throws RestException {
		String rs = restClient.getTransaction(authType, accessToken, txid);
		Result rr = JSON.parseObject(rs, Result.class);
//		if(rr.Error == 0) {
//			try {
//				return Transaction.fromJsonD(new JsonReader(JObject.parse(rr.Result)));
//			} catch (Exception e) {
//				throw new RestRuntimeException("Transaction.fromJsonD(txid) failed", e);
//			}
//		}
		throw new RestRuntimeException(rr.toString());
	}
	public Block getBlockJson(int height) throws RestException {
		String rs = restClient.getBlock(authType, accessToken, height);
		Result rr = JSON.parseObject(rs, Result.class);
//		if(rr.Error == 0) {
//			try {
//				return JsonSerializable.from(JObject.parse(rr.Result), Block.class);
//			} catch (InstantiationException | IllegalAccessException e) {
//				throw new RestRuntimeException("Block.deserialize(height) failed", e);
//			}
//		}
		throw new RestRuntimeException(rr.toString());
	}
		
	public Block getBlockJson(String hash) throws RestException {
		String rs = restClient.getBlock(authType, accessToken, hash);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
//		try {
//			return JsonSerializable.from(JObject.parse(rr.Result), Block.class);
//		} catch (InstantiationException | IllegalAccessException e) {
//			throw new RestRuntimeException("Block.deserialize(hash) failed", e);
//		}
		return null;
	}
	
	public boolean sendToIssService(String data) throws RestException {
		String rs = restClient.sendToIssService(data);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		return true;
	}
	
	public boolean sendToTrfService(String data) throws RestException {
		String rs = restClient.sendToTrfService(data);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			throw new RestRuntimeException(rr.toString());
		}
		return true;
	}
}
class Result {
	public String Action;
	public long Error;
	public String Desc;
	public String Result;
	public String Version;
	
	@Override
	public String toString() {
		return "Result [Action=" + Action + ", Error=" + Error + ", Desc="
				+ Desc + ", Result=" + Result + ", Version=" + Version + "]";
	}	
}
class Result4Block {
	public String Desc;
	public long Error;
	public String Result;
	
	@Override
	public String toString() {
		return "Result4Block [Desc=" + Desc + ", Error=" + Error + ", Result="
				+ Result + "]";
	}
}
class Result4IssService {
	public String Desc;
	public long Error;
	
	@Override
	public String toString() {
		return "Result4IssService [Desc=" + Desc + ", Error=" + Error + "]";
	}
}


