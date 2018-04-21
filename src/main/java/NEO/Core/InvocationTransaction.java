package NEO.Core;


import NEO.Core.Scripts.Program;
import NEO.Fixed8;
import NEO.IO.BinaryReader;
import NEO.IO.BinaryWriter;
import NEO.UInt160;
import NEO.Wallets.Contract;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class InvocationTransaction extends Transaction {
	public byte[] script;
	public Fixed8 gas;
	public ECPoint invoker;

	public InvocationTransaction() {
		super(TransactionType.InvocationTransaction);
	}
	public InvocationTransaction(ECPoint invoker) {
		super(TransactionType.InvocationTransaction);
		this.invoker = invoker;
	}
	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			script = reader.readVarBytes();
			gas = reader.readSerializable(Fixed8.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeVarBytes(script);
		writer.writeSerializable(gas);
	}
	@Override
	public UInt160[] getScriptHashesForVerifying() {
		HashSet<UInt160> hashes = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
		hashes.add(Program.toScriptHash(Contract.createSignatureRedeemScript(invoker)));
		return hashes.stream().sorted().toArray(UInt160[]::new);
	}
}
