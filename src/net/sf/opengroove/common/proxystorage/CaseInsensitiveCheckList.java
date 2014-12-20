package net.sf.opengroove.common.proxystorage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An ArrayList of String that overrides the {@link ArrayList#contains(Object)}
 * method to return true if the object passed into it is a string and is equal,
 * ignoring case, to a string in this list.
 * 
 * @author Alexander Boyd
 * 
 */
public class CaseInsensitiveCheckList extends
    ArrayList<String>
{
    
    public CaseInsensitiveCheckList()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public CaseInsensitiveCheckList(
        Collection<? extends String> c)
    {
        super(c);
        // TODO Auto-generated constructor stub
    }
    
    public CaseInsensitiveCheckList(int initialCapacity)
    {
        super(initialCapacity);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * If the object specified is not a string, then the result of this method
     * is the same as {@link ArrayList#contains(Object)}. If the object
     * specified <i>is</i> a string, then this method also returns true if the
     * specified string is present in this list, ignoring case. For example,
     * contains("hello") would return true if any of "hello", "Hello", "HELLO",
     * or "hElLo" were present in the list.
     */
    @Override
    public boolean contains(Object o)
    {
        if (!(o instanceof String))
            return super.contains(o);
        for (String string : this)
        {
            if (string.equalsIgnoreCase((String) o))
                return true;
        }
        return super.contains(o);
    }
    
}
