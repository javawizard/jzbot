package net.sf.opengroove.common.utils;

import java.math.BigInteger;

/**
 * This class holds utilities related to math. This class essentially contains
 * algorithms and stuff
 * 
 * @author Alexander Boyd
 * 
 */
public class MathUtils
{
    /**
     * A string that can be passed into parse and toString of this class as a
     * radix string. It contains all numbers and letters (upper-case only),
     * except for those that have a number that looks like them, such as i (1)
     * or o (0).
     */
    public static final String RADIX_ALPHA_CLEAR = "0123456789ABCDEFGHJKLMNQRSTVWXYZ";
    public static final String RADIX_ALPHANUMERIC =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
     * Converts a BigInteger to a String, using the chars specified as the
     * digits to encode. The first character is treated as the least, and the
     * last character as the most. If the number specified is 0, then the output
     * will consist of the first character in the string only. For example,
     * calling this method with <code>chars</code> equal to "0123456789" would
     * have the same result as calling <code>number.toString()</code> (IE it
     * results in the base 10 representation of the number).<br/>
     * <br/>
     * 
     * The algorithm used tends to be considerably less efficient than that
     * provided by {@link BigInteger#toString()} or
     * {@link BigInteger#toString(int)}, so those methods should be used where
     * possible.
     * 
     * @param number
     * @param chars
     * @return
     */
    public static String toString(BigInteger number, String chars)
    {
        StringBuilder buffer = new StringBuilder();
        int intRadix = chars.length();
        BigInteger radix = BigInteger.valueOf(intRadix);
        if (intRadix < 2)
            throw new IllegalArgumentException(
                "at least 2 chars must be specified for the radix");
        boolean isNegative = number.signum() == -1;
        if (isNegative)
        {
            number = number.abs();
            buffer.append("-");
        }
        while (number.signum() == 1)
        {
            int index = number.mod(radix).intValue();
            buffer.insert(0, chars.charAt(index));
            number = number.divide(radix);
        }
        return buffer.toString();
    }
    
    /**
     * Parses the string specified, using the chars specified as the digits.
     * 
     * @param value
     *            The value to parse, which should contain only characters in
     *            <code>chars</code>, except that it may begin with a "-"
     *            character, in which case the resulting number will be negative
     * @param chars
     *            The digits to use when parsing. For example, using
     *            "0123456789" would cause the number to be parsed as a base-10
     *            number, and using "0123456789abcdef" would cause the number to
     *            be parsed as a base-16 number.
     * @return The parsed number
     */
    public static BigInteger parse(String value, String chars)
    {
        int intRadix = chars.length();
        BigInteger radix = BigInteger.valueOf(intRadix);
        if (intRadix < 2)
            throw new IllegalArgumentException(
                "at least 2 chars must be specified for the radix");
        boolean isNegative = false;
        if (value.startsWith("-"))
        {
            value = value.substring(1);
            isNegative = true;
        }
        BigInteger number = BigInteger.valueOf(0);
        for (char c : value.toCharArray())
        {
            BigInteger index = BigInteger.valueOf(chars.indexOf(c));
            if (index.signum() == -1)
                throw new RuntimeException("The character " + c + " in the value "
                    + value + " is not in the radix string specified.");
            number = number.add(index);
            number = number.multiply(radix);
        }
        if (isNegative)
            number = number.multiply(BigInteger.valueOf(-1));
        return number;
    }
    
    public static String toString(long number, String chars)
    {
        return toString(BigInteger.valueOf(number), chars);
    }
    
    public static long parseLong(String value, String chars)
    {
        return parse(value, chars).longValue();
    }
}
