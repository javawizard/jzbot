package jw.jzbot.fact;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that converts each byte it receives to an ASCII character and writes
 * it to a {@link Sink}.
 * 
 * @author Alexander Boyd
 * 
 */
public class AsciiSinkStream extends OutputStream
{
    private Sink sink;
    
    public AsciiSinkStream(Sink sink)
    {
        this.sink = sink;
    }
    
    @Override
    public void write(int value) throws IOException
    {
        sink.write((value + 256) % 256);
    }
    
}
