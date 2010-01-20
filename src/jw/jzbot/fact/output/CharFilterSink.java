package jw.jzbot.fact.output;

import jw.jzbot.fact.Sink;

/**
 * A sink that wraps another sink, and applies some character-level filtering to
 * operations that add data to the sink.
 * 
 * @author Alexander Boyd
 * 
 */
public abstract class CharFilterSink implements FilteredSink
{
    protected Sink delegate;
    
    public CharFilterSink(Sink delegate)
    {
        this.delegate = delegate;
    }
    
    @Override
    public void write(String s)
    {
        for (int i = 0; i < s.length(); i++)
        {
            process(s.charAt(i));
        }
    }
    
    @Override
    public void write(char c)
    {
        process(c);
    }
    
    @Override
    public void write(int i)
    {
        write(Integer.toString(i));
    }
    
    /**
     * Processes the specified character. Implementations of this method should write the
     * character, or some translated form of it, to {@link #delegate}.
     * 
     * @param c
     */
    public abstract void process(char c);
    
    public Sink getTarget()
    {
        return delegate;
    }
}
