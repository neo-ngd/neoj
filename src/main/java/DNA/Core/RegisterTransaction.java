package DNA.Core;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

import org.bouncycastle.math.ec.ECPoint;

import DNA.Fixed8;
import DNA.Helper;
import DNA.UInt160;
import DNA.Core.Scripts.Program;
import DNA.Cryptography.ECC;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;
import DNA.IO.Json.JNumber;
import DNA.IO.Json.JObject;
import DNA.IO.Json.JString;
import DNA.Wallets.Contract;


/**
 *  注册资产交易
 *  
 */
public class RegisterTransaction extends Transaction {
	/**
	 * 资产名称
	 */
	public String name;				// 资产名称
	/**
	 * 资产描述
	 */
	public String description;		// 资产描述
	/**
	 * 精度
	 */
	public byte precision;			// 精度
	/**
	 * 资产类型
	 */
	public AssetType assetType;		// 资产类型
	/**
	 * 资产名称
	 */
	public RecordType recordType;	// 记账模式
	/**
	 * 资产数量
	 */
	public Fixed8 amount;			// 资产数量
	/**
	 * 发行者公钥
	 */
	public ECPoint issuer;			// 发行者公钥
	/**
	 * 管理者地址
	 */
	public UInt160 admin;			// 管理者地址
	
	public RegisterTransaction() {
		super(TransactionType.RegisterTransaction);
	}
	
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			name = reader.readVarString();
			description = reader.readVarString();
			precision = reader.readByte();
			assetType = AssetType.valueOf(reader.readByte());
			recordType = RecordType.valueOf(reader.readByte());
	        amount = reader.readSerializable(Fixed8.class);
	        byte[] xx = reader.readVarBytes();
	        byte[] yy = reader.readVarBytes();
	        admin = reader.readSerializable(UInt160.class);
            issuer = ECC.secp256r1.getCurve().createPoint(
	        		new BigInteger(1,xx), new BigInteger(1,yy));
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarString(name);
        writer.writeVarString(description);
        writer.writeByte(precision);
        writer.writeByte(assetType.value());
        writer.writeByte(recordType.value());
        writer.writeSerializable(amount);
        writer.writeVarBytes(Helper.removePrevZero(issuer.getXCoord().toBigInteger().toByteArray()));
        writer.writeVarBytes(Helper.removePrevZero(issuer.getYCoord().toBigInteger().toByteArray()));
//        writer.writeVarBytes(issuer.getXCoord().toBigInteger().toByteArray());
//        writer.writeVarBytes(issuer.getYCoord().toBigInteger().toByteArray());
        writer.writeSerializable(admin);
	}
	
	/**
	 * 获取验证脚本
	 */
	@Override
	public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(issuer)));
        return hashes.stream().sorted().toArray(UInt160[]::new);
	}
	
	@Override
    public JObject json() {
        JObject json = super.json();
        json.set("Asset", new JObject());
        json.get("Asset").set("Name", new JString(name));
        json.get("Asset").set("Precision", new JNumber(precision));
        json.get("Asset").set("AssetType", new JString(String.valueOf(assetType.value())));
        json.get("Asset").set("RecordType", new JString(String.valueOf(recordType)));
        json.set("Amount", new JNumber(amount.toLong()));
        json.set("Issuer", new JObject());
        json.get("Issuer").set("X", new JString(issuer.getXCoord().toBigInteger().toString()));
        json.get("Issuer").set("Y", new JString(issuer.getYCoord().toBigInteger().toString()));
        json.set("Controller", new JString(admin.toString()));
        return json;
    }

	@Override
	public String toString() {
		return "RegisterTransaction [name=" + name + "]";
	}
	
}
