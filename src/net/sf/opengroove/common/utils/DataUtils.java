package net.sf.opengroove.common.utils;

import java.io.File;
import java.lang.reflect.Array;

import base64.Base64Coder;

/**
 * A collection of utilities for manipulating data, particularly byte arrays.
 * 
 * @author Alexander Boyd
 * 
 */
public class DataUtils
{
    /**
     * Base64 encodes the bytes specified.
     * 
     * @param bytes
     *            the bytes to encode
     * @return the encoded string
     */
    public static String encode(byte[] bytes)
    {
        return new String(Base64Coder.encode(bytes));
    }
    
    /**
     * Base64 decodes the string specified.
     * 
     * @param data
     *            the data to decode
     * @return the decoded data
     */
    public static byte[] decode(String data)
    {
        return Base64Coder.decode(data);
    }
    
    public static long recursiveSizeScan(File file)
    {
        if (!file.exists())
            return 0;
        if (file.isFile())
            return file.length();
        if (file.isDirectory())
        {
            int totalSize = 0;
            for (File f : file.listFiles())
            {
                totalSize += recursiveSizeScan(f);
            }
            return totalSize;
        }
        throw new RuntimeException(
            "unercognized file type for file "
                + file.getAbsolutePath());
    }
    
    public static void recursiveDelete(
        File transmissionFolder)
    {
        if (transmissionFolder.isDirectory())
        {
            for (File file : transmissionFolder.listFiles())
            {
                recursiveDelete(file);
            }
        }
        transmissionFolder.delete();
    }
    
    /**
     * Concatenates multiple arrays together, storing the result in a
     * newly-allocated array.
     * 
     * @param <T>
     *            The element type of the array to return
     * @param <E>
     *            The element type of the arrays that will be passed in
     * @param in
     *            The arrays to concatenate
     * @return A new array that holds the values of the arrays passed in,
     *         concatenated.
     */
    
    public static <T, E extends T> T[] concat(
        Class<T> newType, E[]... in)
    {
        int length = 0;
        for (E[] array : in)
        {
            length += array.length;
        }
        T[] result = (T[]) Array.newInstance(newType,
            length);
        int index = 0;
        for (E[] array : in)
        {
            for (E element : array)
            {
                result[index++] = element;
            }
        }
        return result;
    }
    
}
