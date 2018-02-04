package DNA.Cryptography;

import java.math.BigInteger;

public class Base58 {
    /**
     *  base58编码的字母表
     */
    public static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(ALPHABET.length());
    
    /**
     *  解码
     *  <param name="input">要解码的字符串</param>
     *  <returns>返回解码后的字节数组</returns>
     */
    public static byte[] decode(String input) {
        BigInteger bi = BigInteger.ZERO;
        for (int i = input.length() - 1; i >= 0; i--) {
            int index = ALPHABET.indexOf(input.charAt(i));
            if (index == -1) {
                throw new IllegalArgumentException();
            }
            bi = bi.add(BASE.pow(input.length() - 1 - i).multiply(BigInteger.valueOf(index)));
        }
        byte[] bytes = bi.toByteArray();
        boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;
        int leadingZeros = 0;
        for (; leadingZeros < input.length() && input.charAt(leadingZeros) == ALPHABET.charAt(0); leadingZeros++);
        byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
        System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
        return tmp;
    }

    /**
     *  编码
     *  <param name="input">要编码的字节数组</param>
     *  <returns>返回编码后的字符串</returns>
     */
    public static String encode(byte[] input) {
        BigInteger value = new BigInteger(1, input);
        StringBuilder sb = new StringBuilder();
        while (value.compareTo(BASE) >= 0) {
        	BigInteger[] r = value.divideAndRemainder(BASE);
            sb.insert(0, ALPHABET.charAt(r[1].intValue()));
            value = r[0];
        }
        sb.insert(0, ALPHABET.charAt(value.intValue()));
        for (byte b : input) {
            if (b == 0) {
                sb.insert(0, ALPHABET.charAt(0));
            } else {
                break;
            }
        }
        return sb.toString();
    }
}
