package NEO.Websocket.Utils;

import NEO.Core.Block;
import NEO.IO.JsonSerializable;
import NEO.IO.Json.JObject;

/**
 * Parsing tool class
 * 
 * @author 12146
 *
 */
public class GetBlockTransactionUtils {
	
	/**
	 * Parse block for transaction
	 * 
	 * @param ss	json data obtained from websocket server
	 * @return		transaction list
	 */
	public static Block from(String ss) {
//		try {
//			return JsonSerializable.from(JObject.parse(ss), Block.class);
//		} catch (InstantiationException | IllegalAccessException e) {
//			throw new RuntimeException("Block Parsing exception");
//		}
		return null;
	}
}