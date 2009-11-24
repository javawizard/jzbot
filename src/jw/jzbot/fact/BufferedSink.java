package jw.jzbot.fact;

/**
 * A sink that buffers all data sent to it until {@link #flush()} is called.
 * 
 * @author Alexander Boyd
 * 
 */
public class BufferedSink implements FilteredSink
{
    private Sink sink;
    private StringBuffer buffer = new StringBuffer();
    
    public BufferedSink(Sink sink)
    {
        this.sink = sink;
    }
    
    @Override
    public void write(String s)
    {
        buffer.append(s);
    }
    
    @Override
    public void write(char c)
    {
        buffer.append(c);
    }
    
    @Override
    public void write(int i)
    {
        buffer.append(i);
    }
    
    public void flush()
    {
        sink.write(buffer.toString());
        buffer = new StringBuffer();
    }

    @Override
    public Sink getTarget()
    {
        return sink;
    }
    
}
