package DNA.Network.Rest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;


public class RestClient {
	private String url;
	public RestClient(String url) {
//		Consts.setRestUrl(url);
		this.url = url;
	}
	
	public String sendTransaction(String authType, String accessToken, String action, String version, String type, String data) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		Map<String, String> body = new HashMap<String, String>();
		body.put("Action", action);
		body.put("Version", version);
		body.put("Type", type);
		body.put("Data", data);
		try {
			return RestHttp.post(url + Consts.Url_send_transaction, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			e.printStackTrace();
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getTransaction(String authType, String accessToken, String txid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		params.put("raw", "1");
		try {
			return RestHttp.get(url + Consts.Url_get_transaction + txid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getAsset(String authType, String accessToken, String assetid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_asset + assetid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlockHeight(String authType, String accessToken) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_block_height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlock(String authType, String accessToken, int height) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		params.put("raw", "1");
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlock(String authType, String accessToken, String hash) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		params.put("raw", "1");
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getUTXOs(String authType, String accessToken, String address, String assetid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_UTXO_By_address_assetid + address + "/" + assetid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	public String getUTXO(String authType, String accessToken, String address) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_UTXO_By_address + address, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBalance(String authType, String accessToken, String address) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_account_balance + address, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getStateUpdate(String authType, String accessToken, String namespace, String key) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_StateUpdate + namespace + "/" + key, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlockHeightFromDb() throws RestException {
		try {
			return RestHttp.get(url + Consts.Url_get_block_height_db);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			e.printStackTrace();
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	public String getBlockFromDb(int height) throws RestException {
		try {
			return RestHttp.get(url + Consts.Url_get_block + height);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			e.printStackTrace();
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	
	public String sendToIssService(String data) throws RestException {
		try {
			return RestHttp.post(url + Consts.Url_send_to_issService, data);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			e.printStackTrace();
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String sendToTrfService(String data) throws RestException {
		try {
			return RestHttp.post(url + Consts.Url_send_to_trfService, data);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			e.printStackTrace();
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	// ****************************************************************************************************8
	public String getTransactionJson(String authType, String accessToken, String txid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_transaction + txid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	public String getBlockJson(String authType, String accessToken, int height) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
	
	public String getBlockJson(String authType, String accessToken, String hash) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException("Invalid url:"+e.getMessage());
		}
	}
}

class Consts {
	public static void setRestUrl(String url) {
		Url_send_transaction = url + "/api/v1/transaction";
		Url_get_transaction = url + "/api/v1/transaction/";
		Url_get_asset = url + "/api/v1/asset/";
		Url_get_block_height = url + "/api/v1/block/height";
		Url_get_block_By_Height = url + "/api/v1/block/details/height/";
		Url_get_block_By_Hash = url + "/api/v1/block/details/hash/";
		Url_get_UTXO_By_address_assetid = url + "/api/v1/asset/utxo/";
		Url_get_UTXO_By_address = url + "/api/v1/asset/utxo/";
		Url_get_account_balance = url + "/api/v1/asset/balance/";
		Url_get_block_height_db = url + "/blocks/service/blockHeight";
		Url_get_block = url + "/blocks/service/oneBlockInfo/";
		Url_get_StateUpdate = url + "/api/v1/stateupdate/";
	}

	public static String Url_send_transaction = "/api/v1/transaction";
	public static String Url_get_transaction = "/api/v1/transaction/";
	public static String Url_get_asset = "/api/v1/asset/";
	public static String Url_get_block_height = "/api/v1/block/height";
	public static String Url_get_block_By_Height = "/api/v1/block/details/height/";
	public static String Url_get_block_By_Hash = "/api/v1/block/details/hash/";
	public static String Url_get_account_balance = "/api/v1/asset/balance/";
	public static String Url_get_UTXO_By_address_assetid = "/api/v1/asset/utxo/";
	public static String Url_get_UTXO_By_address = "/api/v1/asset/utxo/";
	public static String Url_get_block_height_db = "/blocks/service/blockHeight";
	public static String Url_get_block = "/blocks/service/oneBlockInfo/";
	public static String Url_get_StateUpdate = "/api/v1/stateupdate/";
	public static String Url_send_to_issService = "/api/transaction/assetIssue";
	public static String Url_send_to_trfService = "/api/transaction/assetTrans";
}
