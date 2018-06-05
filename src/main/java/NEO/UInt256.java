package NEO;

/**
 * Custom type which inherits UIntBase class that defines 32-bytes/256-bits data,
 * it mostly is used to defined transaction identity
 * 
 */
public class UInt256 extends UIntBase implements Comparable<UInt256> {
    public static final UInt256 ZERO = new UInt256();

    public UInt256() {
        this(null);
    }

    public UInt256(byte[] value) {
        super(32, value);
    }

    @Override
    public int compareTo(UInt256 other) {
        byte[] x = this.data_bytes;
        byte[] y = other.data_bytes;
        for (int i = x.length - 1; i >= 0; i--) {
        	int r = Byte.toUnsignedInt(x[i]) - Byte.toUnsignedInt(y[i]);
        	if (r != 0) {
        		return r;
        	}
        }
        return 0;
    }

    /**
     * Parse string into UInt256
     * @param s The string is Hex decimal format in BIG ENDIAN
     * @return the parsed UInt256 object
     */
    public static UInt256 parse(String s) {
        if (s == null) {
            throw new NullPointerException(); 
        }
        if (s.startsWith("0x")) {
            s = s.substring(2);
        }
        if (s.length() != 64) {
            throw new IllegalArgumentException();
        }
        byte[] v = Helper.hexToBytes(s);
        return new UInt256(Helper.reverse(v));
    }

    /**
     * Try to parse string into UInt256
     * @param s The string is Hex decimal format in BIG ENDIAN
     * @param out_result out parameter. The value will be changed as output
     * @return true if successfully parsed.
     */
    public static boolean tryParse(String s, UInt256 out_result) {
        try {
            UInt256 v = parse(s);
            out_result.data_bytes = v.data_bytes;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}