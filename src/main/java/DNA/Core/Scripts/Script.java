package DNA.Core.Scripts;

import java.io.IOException;

import DNA.Helper;
import DNA.UInt160;
import DNA.Cryptography.Digest;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;
import DNA.IO.JsonReader;
import DNA.IO.JsonSerializable;
import DNA.IO.JsonWriter;
import DNA.IO.Serializable;
import DNA.IO.Json.JObject;
import DNA.IO.Json.JString;

/**
 *  脚本
 */
public class Script implements Serializable, JsonSerializable {
    public byte[] stackScript;
    public byte[] redeemScript;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        stackScript = reader.readVarBytes();
        redeemScript = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(stackScript);
        writer.writeVarBytes(redeemScript);
    }

    /**
     *  变成json对象
     *  <returns>返回json对象</returns>
     */
    public JObject json() {
        JObject json = new JObject();
        json.set("stack", new JString(Helper.toHexString(stackScript)));
        json.set("redeem", new JString(Helper.toHexString(redeemScript)));
        return json;
    }

    public static UInt160 toScriptHash(byte[] script) {
    	return new UInt160(Digest.hash160(script));
    }
    
	/**
	 * byte格式数据反序列化
	 */
	@Override
	public void fromJson(JsonReader reader) {
		JObject json = reader.json();
		stackScript = Helper.hexToBytes(json.get("Code").asString());
		redeemScript = Helper.hexToBytes(json.get("Parameter").asString());
	}
	
	@Override
	public void toJson(JsonWriter writer) {
		// ...
	}
}
