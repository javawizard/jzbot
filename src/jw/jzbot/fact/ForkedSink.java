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
    public void add(String s)
    {
        first.add(s);
        second.add(s);
    }
    
    @Override
    public void add(char c)
    {
        first.add(c);
        second.add(c);
    }
    
    @Override
    public void add(int i)
    {
        first.add(i);
        second.add(i);
    }
    
}
