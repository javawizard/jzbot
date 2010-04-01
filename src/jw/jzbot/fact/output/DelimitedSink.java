package jw.jzbot.fact.output;

import jw.jzbot.fact.Sink;

/**
 * A sink that can be used to write data delimited by a particular string.
 * 
 * @author Alexander Boyd
 * 
 */
public class DelimitedSink implements FilteredSink
{
    private Sink sink;
    
    private boolean written = false;
    
    private String delimiter;
    
    /**
     * Informs the sink that a new item is about to be written. The first time this is
     * called, it does nothing. The rest of the times, this writes out the delimiter.
     * 
     * @return <tt>this</tt>. This can be used to chain method calls.
     */
    public DelimitedSink next()
    {
        if (written)
            sink.write(delimiter);
        written = true;
        return this;
    }
    
    /**
     * Creates a DelimitedSink that will write to the specified sink.
     * 
     * @param sink
     * @param delimiter
     */
    public DelimitedSink(Sink sink, String delimiter)
    {
        this.sink = sink;
        this.delimiter = delimiter;
    }
    
    @Override
    public void write(String s)
    {
        sink.write(s);
    }
    
    @Override
    public void write(char c)
    {
        sink.write(c);
    }
    
    @Override
    public void write(int i)
    {
        sink.write(i);
    }
    
    @Override
    public Sink getTarget()
    {
        return sink;
    }
    
}
