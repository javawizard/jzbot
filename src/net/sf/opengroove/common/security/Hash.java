package net.sf.opengroove.common.security;

import java.math.BigInteger;

import DE.knp.MicroCrypt.Sha512;

public class Hash
{
    public static String hash(byte[] bytes)
    {
        Sha512 encoder = new Sha512();
        encoder.append(bytes);
        return hexcode(encoder.finish());
    }
    
    public static byte[] hashRaw(byte[] bytes)
    {
        Sha512 encoder = new Sha512();
        encoder.append(bytes);
        return encoder.finish();
    }
    
    public static String hash(String string)
    {
        return hash(string.getBytes());
    }
    
    public static String hexcode(byte[] bytes)
    {
        return new BigInteger(bytes).toString(16);
    }
    
}
