package DNA.Network;

import DNA.UInt256;
import DNA.Core.Signable;
import DNA.Cryptography.Digest;

public abstract class Inventory implements Signable {
    //[NonSerialized]
    private UInt256 _hash = null;
    
    public UInt256 hash() {
        if (_hash == null) {
			_hash = new UInt256(Digest.hash256(getHashData()));
        }
        return _hash;
    }

    public abstract InventoryType inventoryType();

    public abstract boolean verify();
}
