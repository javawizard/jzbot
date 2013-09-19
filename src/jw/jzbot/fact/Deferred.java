package jw.jzbot.fact;

/**
 * A wrapper around a particular item in a particular ArgumentList that allows for that
 * particular argument's resolution to be deferred until the value is actually needed. An
 * instance of this class can be obtained by calling {@link ArgumentList#getDeferred(int)}
 * . This class primarily exists to avoid having to pass both an argument list and an
 * index to methods that need access to a particular argument.
 * 
 * @author Alexander Boyd
 * 
 */
public class Deferred
{
    private ArgumentList list;
    private int index;
    
    Deferred(ArgumentList list, int index)
    {
        this.list = list;
        this.index = index;
    }
    
    public void get(Sink sink)
    {
        list.get(index, sink);
    }
    
    public String getString()
    {
        return list.getString(index);
    }
    
    public void resolve(Sink sink)
    {
        list.resolve(index, sink);
    }
    
    public String resolveString()
    {
        return list.resolveString(index);
    }
}
