package org.opengroove.jzbot.fact;

/**
 * A wrapper around a particular item in a particular ArgumentList that allows for that
 * particular argument's resolution to be deferred until the value is actually needed. An
 * instance of this class can be obtained by calling {@link ArgumentList#getDeferred(int)}
 * .
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
    
    public String get()
    {
        return list.get(index);
    }
    
    public String resolve()
    {
        return list.resolve(index);
    }
}
