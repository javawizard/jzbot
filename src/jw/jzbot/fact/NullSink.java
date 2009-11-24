package jw.jzbot.fact;

/**
 * A sink that discards all input given to it.
 * 
 * @author Alexander Boyd
 * 
 */
public class NullSink implements ObservableSink
{
    /**
     * A singleton instance of NullSink that can be used when it doesn't matter that a
     * single instance is being used.
     */
    public static final NullSink sink = new NullSink();
    
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
    
    @Override
    public String getCurrentValue()
    {
        return "";
    }
    
}
