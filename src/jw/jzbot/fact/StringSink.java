package jw.jzbot.fact;

/**
 * A sink that stores its output in a StringBuffer. The current value of the StringBuffer
 * can be obtained at any time.
 * 
 * @author Alexander Boyd
 * 
 */
public class StringSink implements Sink
{
    private StringBuffer buffer = new StringBuffer();
    
    @Override
    public void add(String s)
    {
        buffer.append(s);
    }
    
    @Override
    public void add(char c)
    {
        buffer.append(c);
    }
    
    @Override
    public void add(int i)
    {
        buffer.append(i);
    }
    
    public String toString()
    {
        return buffer.toString();
    }
}
