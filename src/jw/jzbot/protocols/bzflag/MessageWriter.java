package jw.jzbot.protocols.bzflag;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class MessageWriter
{
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    public byte[] getData()
    {
        return out.toByteArray();
    }
}
