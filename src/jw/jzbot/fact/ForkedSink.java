package jw.jzbot.fact;

/**
 * A sink that forks the data stream. In other words, this sink will send all received
 * data to two other sinks. This sink is a filtered sink in the sense indicated by the
 * class summary of {@link FilteredSink}; this class, however, does not implement
 * FilteredSink since it sends its output to two sinks instead of just one.<br/><br/>
 * 
 * This class always sends data to the first sink before sending data to the second sink.
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
