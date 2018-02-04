package NEO.Network;

import NEO.UInt256;
import NEO.Core.Signable;
import NEO.Cryptography.Digest;

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
