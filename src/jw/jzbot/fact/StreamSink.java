package jw.jzbot.fact;

import java.io.OutputStream;

/**
 * A sink that sends its output to an OutputStream.
 * 
 * @author Alexander Boyd
 * 
 */
public class StreamSink implements Sink
{
    private OutputStream out;
    
    public StreamSink(OutputStream out)
    {
        this.out = out;
    }
    
    @Override
    public void write(String s)
    {
        try
        {
            out.write(s.getBytes());
            out.flush();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void write(char c)
    {
        try
        {
            out.write(new String(new char[]
            {
                c
            }).getBytes());
            out.flush();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void write(int i)
    {
        write(Integer.toString(i));
    }
    
}
