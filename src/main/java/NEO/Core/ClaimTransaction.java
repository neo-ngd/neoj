package NEO.Core;

import NEO.IO.BinaryReader;
import NEO.IO.BinaryWriter;
import NEO.UInt160;
import NEO.UInt256;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClaimTransaction extends Transaction{

    public TransactionInput[] claims;

    public ClaimTransaction() {
        super(TransactionType.ClaimTransaction);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
            claims = reader.readSerializableArray(TransactionInput.class);
        }
        catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public UInt160[] getScriptHashesForVerifying() {
        HashSet<UInt160> hashs = new HashSet<UInt160>(Arrays.asList(super.getScriptHashesForVerifying()));
        for (Map.Entry<UInt256, List<TransactionInput>> group : Arrays.stream(claims).collect(Collectors.groupingBy(p -> p.prevHash)).entrySet()) {
            try {
                Transaction tx = Blockchain.current().getTransaction(group.getKey());
                if (tx == null)
                    throw new IllegalStateException();
                for (TransactionInput claim : group.getValue()) {
                    if (tx.outputs.length <= claim.prevIndex)
                        throw new IllegalStateException();
                    hashs.add(tx.outputs[claim.prevIndex].scriptHash);
                }
            }
            catch (Exception e) {
                throw new IllegalStateException();
            }
        }
        return hashs.stream().sorted().toArray(UInt160[]::new);
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeSerializableArray(claims);
    }
}
