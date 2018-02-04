package DNA.Core;

import java.io.IOException;
import DNA.IO.BinaryReader;
import DNA.IO.BinaryWriter;

public class DeployCodeTransaction extends Transaction {
	public FunctionCode code;
	public String name;
	public String codeVersion;
	public String author;
	public String email;
	public String description;
	
	protected DeployCodeTransaction() {
		super(TransactionType.DeployCode);
	}

	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		code.deserialize(reader);
		name = reader.readVarString();
		codeVersion = reader.readVarString();
		author = reader.readVarString();
		email = reader.readVarString();
		description = reader.readVarString();
	}
	
	protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
		code.serialize(writer);
		writer.writeVarString(name);
		writer.writeVarString(codeVersion);
		writer.writeVarString(author);
		writer.writeVarString(email);
		writer.writeVarString(description);
	}
}
