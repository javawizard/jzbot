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
    public void add(String s)
    {
    }
    
    @Override
    public void add(char c)
    {
    }
    
    @Override
    public void add(int i)
    {
    }
    
}
