package jw.jzbot.fact;

/**
 * A sink that forks the data stream. In other words, this sink will send all received
 * data to two other sinks.
 * 
 * @author Alexander Boyd
 * 
 */
public class ForkedSink implements Sink
{
    private Sink first;
    private Sink second;
    
    public ForkedSink(Sink first, Sink second)
    {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public void write(String s)
    {
        first.write(s);
        second.write(s);
    }
    
    @Override
    public void write(char c)
    {
        first.write(c);
        second.write(c);
    }
    
    @Override
    public void write(int i)
    {
        first.write(i);
        second.write(i);
    }
    
}
