package org.opengroove.jzbot.fact;

public class ArgumentList
{
    private ArgumentList delegate;
    private int offset;
    private int length;
    private Sequence sequence;
    private String[] resolved;
    private FactContext context;
    
    public ArgumentList(Sequence sequence, FactContext context)
    {
        this.context = context;
        this.sequence = sequence;
        this.resolved = new String[sequence.length()];
    }
    
    private ArgumentList(ArgumentList delegate, int offset, int length,
            FactContext context)
    {
        this.context = context;
        this.delegate = delegate;
        this.offset = offset;
        this.length = length;
    }
    
    /**
     * Same as <tt>get()</tt>, but the argument is "run" again even if it has
     * already been run before. Functions like {{split}} use this for the
     * <action> argument to cause it to be run once for each substring in the
     * string to split.
     * 
     * @param index
     * @return
     */
    public String resolve(int index)
    {
        if (delegate != null)
        {
            return delegate.resolve(index + offset);
        }
        else
        {
            return sequence.get(index).resolve(context);
        }
    }
    
    /**
     * Gets the argument at the specified index. If this argument has not
     * actually been "run" to find out what its value should be, it is run, and
     * the value is stored so that future calls to <tt>get()</tt> will not
     * result in the argument being "run" again.
     * 
     * @param index
     *            The index of the argument. Indexes start at 0.
     * @return The result of running the argument at the specified index
     */
    public String get(int index)
    {
        if (delegate != null)
        {
            return delegate.get(index + offset);
        }
        else
        {
            if (resolved[index] == null)
            {
                resolved[index] = sequence.get(index).resolve(context);
            }
            return resolved[index];
        }
    }
    
    /**
     * Returns the number of arguments in this argument list.
     * 
     * @return
     */
    public int length()
    {
        if (delegate == null)
            return sequence.length();
        else
            return length;
    }
    
    /**
     * Returns a "sub"-list that views a portion of this list. This could be
     * compared to <tt>String.substring()</tt>.
     * 
     * @param offset
     *            The offset at which the sublist is supposed to start
     * @return The new sublist
     */
    public ArgumentList subList(int offset)
    {
        return new ArgumentList(this, offset, length() - offset, context);
    }
    
    public ArgumentList subList(int offset, int length)
    {
        return new ArgumentList(this, offset, length, context);
    }
    
    /**
     * Gets (with a call to <tt>get()</tt>) all arguments in this argument list,
     * puts them into a new String array, and returns the array.
     * 
     * @return
     */
    public String[] evalToArray()
    {
        String[] s = new String[length()];
        for (int i = 0; i < s.length; i++)
        {
            s[i] = get(i);
        }
        return s;
    }
}
