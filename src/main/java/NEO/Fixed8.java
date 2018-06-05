package NEO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;

import NEO.IO.BinaryReader;
import NEO.IO.BinaryWriter;
import NEO.IO.Serializable;

/**
 * Number type in block. this type Can be accurate to 64-bit fixed-point, the rounding 
 * error to a minimum. By controlling the multiplier's accuracy, 
 * the rounding error can be completely eliminated
 *
 */
public class Fixed8 implements Comparable<Fixed8>, Serializable {
    private static final long D = 100000000L;
    private long value;

    public static final Fixed8 MAX_VALUE = new Fixed8(Long.MAX_VALUE);

    public static final Fixed8 MIN_VALUE = new Fixed8(Long.MIN_VALUE);

    public static final Fixed8 ONE = new Fixed8(D);

    public static final Fixed8 SATOSHI = new Fixed8(1);

    public static final Fixed8 ZERO = new Fixed8(0);
    
    public Fixed8() {
    	this(0);
    }

    public Fixed8(long data) {
        this.value = data;
    }

    public Fixed8 abs() {
        if (value >= 0) {
        	return this;
        }
        return new Fixed8(-value);
    }

    public Fixed8 ceiling() {
        long remainder = value % D;
        if (remainder == 0) return this;
        if (remainder > 0) {
            return new Fixed8(value - remainder + D);
        } else {
            return new Fixed8(value - remainder);
        }
    }

    @Override
    public int compareTo(Fixed8 other) {
        return Long.compare(value, other.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fixed8)) {
        	return false;
        }
        return value == ((Fixed8) obj).value;
    }

    public static Fixed8 fromDecimal(BigDecimal val) {
        return new Fixed8(val.multiply(new BigDecimal(D)).longValueExact());
    }
    
    public static Fixed8 fromLong(long val) {
    	return new Fixed8(Math.multiplyExact(val, D));
    }
    
    public static Fixed8 parse(String s) {
        return fromDecimal(new BigDecimal(s));
    }

    public long getData() { 
    	return value; 
    }

    @Override
    public int hashCode() {
        return Long.valueOf(value).hashCode();
    }

    public static Fixed8 max(Fixed8 first, Fixed8 ...others) {
        for (Fixed8 other : others) {
            if (first.compareTo(other) < 0) {
            	first = other;
            }
        }
        return first;
    }

    public static Fixed8 min(Fixed8 first, Fixed8 ...others) {
        for (Fixed8 other : others) {
            if (first.compareTo(other) > 0) {
            	first = other;
            }
        }
        return first;
    }

    public static Fixed8 sum(Fixed8[] values) {
    	return sum(values, p -> p);
    }
    
    public static <T> Fixed8 sum(T[] values, Function<T, Fixed8> selector) {
    	Fixed8 sum = Fixed8.ZERO;
        for (T item : values) {
            sum = sum.add(selector.apply(item));
        }
        return sum;
    }

    @Override
    public String toString() {
        BigDecimal v = new BigDecimal(value);
        v = v.divide(new BigDecimal(D), 8, BigDecimal.ROUND_UNNECESSARY);
        return v.toPlainString();
    }

    /**
     * Try to parse string into Fixed8
     * @param s The string
     * @param out_result out parameter. The value will be changed as output
     * @return true if successfully parsed.
     */
    public static boolean tryParse(String s, Fixed8 out_result) {
        try {
            BigDecimal val = new BigDecimal(s);
            Fixed8 fixed8Val = fromDecimal(val);
            out_result.value = fixed8Val.value;
            return true;
        } catch(NumberFormatException | ArithmeticException ex) {
            return false;
        }
    }

    public long toLong() {
        return value / D;
    }

    public Fixed8 multiply(long other) {
        return new Fixed8(Math.multiplyExact(value, other));
    }

    public Fixed8 divide(long other) {
        return new Fixed8(value / other);
    }

    public Fixed8 add(Fixed8 other) {
    	return new Fixed8(Math.addExact(this.value, other.value));
    }

    public Fixed8 subtract(Fixed8 other) {
    	return new Fixed8(Math.subtractExact(this.value, other.value));
    }

    public Fixed8 negate() {
        return new Fixed8(-value);
    }

	@Override
	public void serialize(BinaryWriter writer) throws IOException {
		writer.writeLong(value);
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		value = reader.readLong();
	}
}