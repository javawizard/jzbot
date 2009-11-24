package jw.jzbot.fact;

/**
 * A sink that discards all input given to it.
 * 
 * @author Alexander Boyd
 * 
 */
public class NullSink implements Sink
{
    
    @Override
    public void write(String s)
    {
    }
    
    @Override
    public void write(char c)
    {
    }
    
    @Override
    public void write(int i)
    {
    }
    
}
