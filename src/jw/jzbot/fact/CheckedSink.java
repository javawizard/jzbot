package jw.jzbot.fact;

/**
 * A sink that can report whether or not anything has been written to it. Writing the
 * empty string with {@link #write(String)} does not count as writing data.
 * 
 * @author Alexander Boyd
 * 
 */
public class CheckedSink implements FilteredSink
{
    private boolean written;
    
    public boolean isWritten()
    {
        return written;
    }
    
    private Sink sink;
    
    /**
     * Creates a new checked sink that sends all data to the specified sink.
     * 
     * @param sink
     *            The sink to send data to
     */
    public CheckedSink(Sink sink)
    {
        this.sink = sink;
    }
    
    @Override
    public void write(String s)
    {
        if (s.length() > 0)
            written = true;
        sink.write(s);
    }
    
    @Override
    public void write(char c)
    {
        written = true;
        sink.write(c);
    }
    
    @Override
    public void write(int i)
    {
        written = true;
        sink.write(i);
    }
    
    public boolean hasWritten()
    {
        return isWritten();
    }
    
    @Override
    public Sink getTarget()
    {
        return sink;
    }
    
}
