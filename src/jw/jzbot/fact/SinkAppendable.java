package jw.jzbot.fact;

import java.io.IOException;

/**
 * An implementation of Appendable that sends its output to a {@link Sink}.
 * 
 * @author Alexander Boyd
 * 
 */
public class SinkAppendable implements Appendable
{
    private Sink sink;
    
    public SinkAppendable(Sink sink)
    {
        this.sink = sink;
    }
    
    @Override
    public Appendable append(CharSequence csq) throws IOException
    {
        sink.write(csq.toString());
        return this;
    }
    
    @Override
    public Appendable append(char c) throws IOException
    {
        sink.write(c);
        return this;
    }
    
    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException
    {
        sink.write(csq.subSequence(start, end).toString());
        return this;
    }
    
}
