package NEO;

/**
 * Custom type which inherits IntBase class that defines 20-bytes/160-bits data,
 * It mostly is used to defined contract address
 * 
 */
public class UInt160 extends UIntBase implements Comparable<UInt160> {
    public static final UInt160 ZERO = new UInt160();

    public UInt160() {
        this(null);
    }

    public UInt160(byte[] value) {
        super(20, value);
    }

    @Override
    public int compareTo(UInt160 other) {
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
     * Parse string into UInt160
     * @param value The string is Hex decimal format in BIG ENDIAN
     * @return the parsed UInt160 object
     */
    public static UInt160 parse(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value.startsWith("0x")) {
            value = value.substring(2);
        }
        if (value.length() != 40) {
            throw new IllegalArgumentException();
        }
        byte[] v = Helper.hexToBytes(value);
        return new UInt160(Helper.reverse(v));
    }

    /**
     * Try to parse string into UInt160
     * @param s The string is Hex decimal format in BIG ENDIAN
     * @param out_result out parameter. The value will be changed as output
     * @return true if successfully parsed.
     */
    public static boolean tryParse(String s, UInt160 out_result) {
        try {
            UInt160 v = parse(s);
            out_result.data_bytes = v.data_bytes;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}