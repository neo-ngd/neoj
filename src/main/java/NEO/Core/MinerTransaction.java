package NEO.Core;

import java.io.IOException;

import NEO.IO.BinaryReader;
import NEO.IO.BinaryWriter;

public class MinerTransaction extends Transaction {
	private long nonce; // nonce is not exist when version=2

	public MinerTransaction() {
		super(TransactionType.MinerTransaction);
	}

	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		nonce = reader.readInt();
	}
	
	@Override
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		if(version == 3) {
			writer.writeLong(nonce);
		}
	}

	@Override
	protected void onDeserialized() throws IOException{
		if (inputs.length != 0)
			throw new IOException();
		for (TransactionOutput output : outputs) {
			//if (output.assetId != Blockchain.utilityToken().hash())
			if (output.assetId != Blockchain.UtilityToken)
				throw new IOException();
		}
	}
}
