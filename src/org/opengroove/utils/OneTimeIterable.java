package org.opengroove.utils;

import java.util.Iterator;

public class OneTimeIterable implements Iterable
{
    private Iterator i;
    
    public OneTimeIterable(Iterator i)
    {
        this.i = i;
    }
    
    public Iterator iterator()
    {
        return i;
    }
    
}
