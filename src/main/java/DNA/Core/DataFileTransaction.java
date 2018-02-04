package DNA.Core;

import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;

import DNA.Helper;
import DNA.Cryptography.ECC;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;

public class DataFileTransaction extends Transaction {
	public String ipfsPath;
	public String fileName;
	public String note;
	public ECPoint issuer;
	
	protected DataFileTransaction() {
		super(TransactionType.DataFile);
	}

	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		ipfsPath = reader.readVarString();
		fileName = reader.readVarString();
		note = reader.readVarString();
		issuer = ECC.secp256r1.getCurve().createPoint(
        		new BigInteger(1,reader.readVarBytes()), new BigInteger(1,reader.readVarBytes()));
	}
	
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		writer.writeVarString(ipfsPath);
		writer.writeVarString(fileName);
		writer.writeVarString(note);
		writer.writeVarBytes(Helper.removePrevZero(issuer.getXCoord().toBigInteger().toByteArray()));
	    writer.writeVarBytes(Helper.removePrevZero(issuer.getYCoord().toBigInteger().toByteArray()));
	}
}
