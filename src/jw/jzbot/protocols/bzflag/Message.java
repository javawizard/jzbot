package jw.jzbot.protocols.bzflag;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Message
{
    /**
     * Packs this message into the specified output stream.
     * 
     * @param out
     */
    public abstract void pack(DataOutputStream out);
    
    /**
     * Unpacks this message from the specified input stream.
     * 
     * @param in
     * @param length
     */
    public abstract void unpack(DataInputStream in, int length);
}
