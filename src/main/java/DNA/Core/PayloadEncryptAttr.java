package DNA.Core;

import DNA.IO.Serializable;

public interface PayloadEncryptAttr extends Serializable{
	public byte[] encrypt(byte[] msg, byte[] keys);
	public byte[] decrypt(byte[] msg, byte[] keys);
}
