package DNA.Core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DNA.UInt160;
import DNA.Core.Scripts.Program;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;
import DNA.IO.Serializable;
import DNA.Wallets.ContractParameterType;

public class FunctionCode implements ICode, Serializable{
	public byte[] code;
	public ContractParameterType[] parameterTypes;
	public ContractParameterType[] returnTypes;
	public UInt160 scriptHash;
	
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		parameterTypes = toEnum(reader.readVarBytes());
		code = reader.readVarBytes();
	}

	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		writer.writeVarBytes(toByte(parameterTypes));
		writer.writeVarBytes(code);
	}
	
	private ContractParameterType toEnum(byte bt) {
		return Arrays.stream(ContractParameterType.values()).filter(p -> p.ordinal() == bt).findAny().get();
	}
	private ContractParameterType[] toEnum(byte[] bt) {
		if(bt == null) {
			return null;
		}
		List<ContractParameterType> list = new ArrayList<ContractParameterType>();
		for(byte b: bt) {
			ContractParameterType type = toEnum(b);
			list.add(type);
		}
		return list.stream().toArray(ContractParameterType[]::new);
	}
	private byte[] toByte(ContractParameterType[] types) {
		if(types == null) {
			return new byte[0];
		}
		int len = types.length;
		byte[] bt = new byte[len];
		for(int i=0; i<len; ++i) {
			bt[i] = (byte) types[i].ordinal();
		}
		return bt;
	}
	

	@Override
	public byte[] getCode() {
		return code;
	}

	@Override
	public ContractParameterType[] getParameterList() {
		return parameterTypes;
	}

	@Override
	public ContractParameterType[] getReturnType() {
		return returnTypes;
	}

	public UInt160 getCodeHash() {
		if(scriptHash == null) {
			scriptHash = Program.toScriptHash(getCode());
		}
		return scriptHash;
	}
}
