package jw.jzbot.fact.debug.wire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ClientLink
{
    public InputStream getInputStream() throws IOException;
    
    public OutputStream getOutputStream() throws IOException;
    
    public boolean isClosed();
    
    public void close() throws IOException;
}
