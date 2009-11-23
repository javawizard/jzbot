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
    public void add(String s)
    {
        out.write(s.getBytes());
        out.flush();
    }
    
    @Override
    public void add(char c)
    {
        out.write(new String(new char[]
        {
            c
        }).getBytes());
        out.flush();
    }
    
    @Override
    public void add(int i)
    {
        add(Integer.toString(i));
    }
    
}
