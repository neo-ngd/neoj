package DNA.Wallets;

import java.math.BigInteger;
import java.util.Arrays;

import org.bouncycastle.math.ec.ECPoint;

import DNA.UInt160;
import DNA.Core.Scripts.Program;
import DNA.Cryptography.Base58;
import DNA.Cryptography.Digest;
import DNA.Cryptography.ECC;

/**
 * account info, including privatekey/publicKey and publicHash
 */
public class Account {
	
	/**
	 * privateKey, used for signing transaction
	 */
    public final byte[] privateKey;
    
    /**
     * publickey, used for verifying signature info
     */
    public final ECPoint publicKey;
    
    /**
     * publiekeyHash, used for identifing which account the contract belongs to
     */
    public final UInt160 publicKeyHash;

    public Account(byte[] privateKey) {
        if (privateKey.length != 32 && privateKey.length != 96 && privateKey.length != 104) {
        	throw new IllegalArgumentException();
        }
        this.privateKey = new byte[32];
        System.arraycopy(privateKey, privateKey.length - 32, this.privateKey, 0, 32);
        if (privateKey.length == 32) {
            this.publicKey = ECC.secp256r1.getG().multiply(new BigInteger(1, privateKey)).normalize();
        } else {
        	byte[] encoded = new byte[65];
        	encoded[0] = 0x04;
        	System.arraycopy(privateKey, 0, encoded, 1, 64);
            this.publicKey = ECC.secp256r1.getCurve().decodePoint(encoded);
        }
        this.publicKeyHash = Program.toScriptHash(publicKey.getEncoded(true));
    }
    
    public String export() {
        byte[] data = new byte[38];
        data[0] = (byte) 0x80;
        System.arraycopy(privateKey, 0, data, 1, 32);
        data[33] = (byte) 0x01;
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, data.length - 4));
        System.arraycopy(checksum, 0, data, data.length - 4, 4);
        String wif = Base58.encode(data);
        Arrays.fill(data, (byte) 0);
        return wif;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (!(obj instanceof Account)) {
        	return false;
        }
        return publicKeyHash.equals(((Account) obj).publicKeyHash);
    }

    @Override
    public int hashCode(){
        return publicKeyHash.hashCode();
    }
}
