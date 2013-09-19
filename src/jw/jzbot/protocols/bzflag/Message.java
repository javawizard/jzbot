package jw.jzbot.protocols.bzflag;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Message
{
    /**
     * A large number, so large that a bzflag packet of this size will never occur. Right
     * now, this is 65541, and since that number cannot be stored in just two bytes (which
     * a bzflag packet uses to represent length), there cannot be a packet this large.
     */
    private static final int LARGE = 65541;
    
    /**
     * Packs this message into the specified output stream.
     * 
     * @param out
     */
    public abstract void pack(DataOutputStream out) throws IOException;
    
    /**
     * Unpacks this message from the specified input stream.
     * 
     * @param in
     * @param length
     */
    public abstract void unpack(DataInputStream in, int length) throws IOException;
    
    /**
     * Creates a new string from the specified byte array up until the first 0'ed byte in
     * the byte array.
     * 
     * @param bytes
     * @return
     */
    protected String unpackString(byte[] bytes)
    {
        int nullIndex = -1;
        for (int i = 0; i < bytes.length; i++)
        {
            if (bytes[i] == 0)
            {
                nullIndex = i;
                break;
            }
        }
        if (nullIndex == -1)
            nullIndex = bytes.length;
        return new String(bytes, 0, nullIndex);
    }
    
    /**
     * Same as {@link #unpackString(byte[])}, but reads in the bytes manually from the
     * stream before doing the unpacking. At most <tt>length</tt> bytes will be read. If
     * there are less than that number of bytes remaining in the stream, an exception will
     * not be thrown, but the bytes that are available will be unpacked and returned.
     * 
     * @param in
     * @param length
     * @throws IOException
     */
    protected String unpackString(DataInputStream in, int length) throws IOException
    {
        int available = in.available();
        if (available < length)
            length = available;
        byte[] data = new byte[length];
        in.readFully(data);
        return unpackString(data);
    }
    
    /**
     * Same as {@link #unpack(DataInputStream, int)}, but reads every byte until the end
     * of the stream.
     * 
     * @param in
     * @return
     * @throws IOException
     */
    protected String unpackString(DataInputStream in) throws IOException
    {
        return unpackString(in, LARGE);
    }
    
    /**
     * Packs the specified string, padding with nulls until <tt>length</tt> bytes have
     * been written. If the string is longer than that, the string will be truncated.
     * 
     * @param out
     * @param string
     * @param length
     * @throws IOException
     */
    protected void packStringPad(DataOutputStream out, String string, int length)
            throws IOException
    {
        if (string.length() > length)
            string = string.substring(0, length);
        out.writeBytes(string);
        for (int i = 0; i < (length - string.length()); i++)
            out.write(0);
    }
    
    /**
     * Packs the specified string and writes a null character. If the string is longer
     * than <tt>max</tt>, it will be truncated.
     * 
     * @param out
     * @param string
     * @param max
     * @throws IOException
     */
    protected void packStringNull(DataOutputStream out, String string, int max)
            throws IOException
    {
        if (string.length() > max)
            string = string.substring(0, max);
        out.writeBytes(string);
        out.write(0);
    }
}
