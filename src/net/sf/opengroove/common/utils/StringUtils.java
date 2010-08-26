package net.sf.opengroove.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.opengroove.common.proxystorage.ProxyStorage.ToString;

public class StringUtils
{
    public static boolean isMemberOf(String string, String[] strings)
    {
        for (String test : strings)
        {
            if (test.equals(string))
                return true;
        }
        return false;
    }
    
    public static boolean isMemberOfIgnoreCase(String string, String[] strings)
    {
        for (String test : strings)
        {
            if (test.equalsIgnoreCase(string))
                return true;
        }
        return false;
    }
    
    public static <T> String delimited(T[] items, ToString<T> generator, String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++)
        {
            if (i != 0)
                sb.append(delimiter);
            sb.append(generator.toString(items[i]));
        }
        return sb.toString();
    }
    
    public static String delimited(Iterable<String> items, String delimiter)
    {
        return delimited(list(items).toArray(new String[0]), delimiter);
    }
    
    public static String delimited(String[] items, String delimiter)
    {
        return delimited(items, new ToString<String>()
        {
            
            public String toString(String object)
            {
                return object;
            }
        }, delimiter);
    }
    
    public static interface ToString<S>
    {
        public String toString(S object);
    }
    
    /**
     * reads the file specified in to a string. the file must not be larger than 5 MB.
     * 
     * @param file
     *            .
     * @return
     */
    public static String readFile(File file)
    {
        try
        {
            if (file.length() > (5 * 1000 * 1000))
                throw new RuntimeException("the file is " + file.length()
                    + " bytes. that is too large. it can't be larger than 5000000 bytes.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Writes the string specified to the file specified.
     * 
     * @param string
     *            A string to write
     * @param file
     *            The file to write <code>string</code> to
     */
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Copies the contents of one stream to another. Bytes from the source stream are read
     * until it is empty, and written to the destination stream. Neither the source nor
     * the destination streams are flushed or closed.
     * 
     * @param in
     *            The source stream
     * @param out
     *            The destination stream
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
    /**
     * Returns a byte array of exactly the size specified, using the contents of the byte
     * array specified. If the byte array is smaller than the size requested, the extra
     * space will be filled with 0s, and if the byte array is larger than the size
     * requested, the leading bytes will be removed.
     * 
     * @param bytes
     * @param length
     * @return
     */
    public static byte[] exactLength(byte[] bytes, int length)
    {
        byte[] newBytes = new byte[length];
        System.arraycopy(bytes, 0, newBytes, 0, Math.min(bytes.length, newBytes.length));
        return newBytes;
    }
    
    public static Properties loadProps(File file)
    {
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(file));
            return props;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> List<T> list(Iterable<T> iter)
    {
        return list(iter.iterator());
    }
    
    public static <T> List<T> list(Iterator<T> iter)
    {
        ArrayList<T> list = new ArrayList<T>();
        while (iter.hasNext())
            list.add(iter.next());
        return list;
    }
    
}
