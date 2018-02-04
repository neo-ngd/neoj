package DNA.IO.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;

public class JBigInt extends JObject{
    private BigInteger _value;
    public BigInteger value() { 
    	return _value; 
    }

    public JBigInt(String val) {
        this._value = new BigInteger(val);
    }

    @Override
    public boolean asBoolean() {
        if (_value.intValue() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public double asNumber() {
        return _value.doubleValue();
    }

    @Override
    public String asString() {
        return _value.toString();
    }

    @Override
    public boolean canConvertTo(Class<?> type) {
        if (type.equals(boolean.class)) {
            return true;
        }
        if (type.equals(double.class)) {
            return true;
        }
        if (type.equals(String.class)) {
            return true;
        }
        return false;
    }

    static JBigInt parseNumber(BufferedReader reader) throws IOException {
        skipSpace(reader);
        StringBuilder sb = new StringBuilder();
        while (true) {
        	reader.mark(1);
            int c = reader.read();
            if (c >= '0' && c <= '9' || c == '.' || c == '-') {
                sb.append((char)c);
            } else {
            	reader.reset();
                break;
            }
        }
        return new JBigInt(sb.toString());
    }

    @Override
    public String toString() {
        return asString();
    }
}
