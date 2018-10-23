package NEO.Core;

import java.io.IOException;
import java.util.Arrays;

import NEO.Helper;
import NEO.IO.BinaryReader;
import NEO.IO.BinaryWriter;
import NEO.IO.Serializable;
import NEO.IO.Json.JObject;
import NEO.IO.Json.JString;

/**
 *  交易属性
 */
public class TransactionAttribute implements Serializable {
	/**
	 * 用途
	 */
	public TransactionAttributeUsage usage;
	/**
	 * 描述
	 */
	public byte[] data;
	
	/**
	 * byte格式数据反序列化
	 */
	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		// usage
        writer.writeByte(usage.value());
        // data
		if (usage == TransactionAttributeUsage.DescriptionUrl){
			writer.writeByte((byte)data.length);
		}
		else if (usage == TransactionAttributeUsage.Description || usage.value() >= TransactionAttributeUsage.Remark.value()) {
			writer.writeVarInt(data.length);
		}
		if (usage == TransactionAttributeUsage.ECDH02 || usage == TransactionAttributeUsage.ECDH03) {
			writer.write(data, 1, 32);
		}
		else
			writer.write(data);
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		// usage
		usage = TransactionAttributeUsage.valueOf(reader.readByte());
		// data
		if (usage == TransactionAttributeUsage.ContractHash || usage == TransactionAttributeUsage.Vote || (usage.value() >= TransactionAttributeUsage.Hash1.value() && usage.value() <= TransactionAttributeUsage.Hash15.value()))
			data = reader.readBytes(32);
		else if (usage == TransactionAttributeUsage.ECDH02 || usage == TransactionAttributeUsage.ECDH03) {
			byte[] part1 = data.clone();
			byte[] part2 = reader.readBytes(32);
			data = new byte[part1.length + part2.length];
			System.arraycopy(part1, 0, data, 0, part1.length);
			System.arraycopy(part2, 0, data, part1.length, part2.length);
		}
		else if (usage == TransactionAttributeUsage.Script)
			data = reader.readBytes(20);
		else if (usage == TransactionAttributeUsage.DescriptionUrl)
			data = reader.readBytes(reader.readByte());
		else if (usage == TransactionAttributeUsage.Description || usage.value() >= TransactionAttributeUsage.Remark.value())
			data = reader.readVarBytes(65535);
		else
			throw new IOException();
	}
	
	public JObject json() {
        JObject json = new JObject();
        json.set("usage", new JString(usage.toString()));
        json.set("data", new JString(Helper.toHexString(data)));
        return json;
	}
	
	@Override
	public String toString() {
		return "TransactionAttribute [usage=" + usage + ", data="
				+ Arrays.toString(data) + "]";
	}
}
