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
    
    public String resolve(int index)
    {
        if (delegate != null)
        {
            return delegate.get(index + offset);
        }
        else
        {
            return sequence.get(index).resolve(context);
        }
    }
    
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
    
    public int length()
    {
        if (delegate == null)
            return sequence.length();
        else
            return length;
    }
    
    public ArgumentList subList(int offset)
    {
        return new ArgumentList(this, offset, length() - offset, context);
    }
    
    public ArgumentList subList(int offset, int length)
    {
        return new ArgumentList(this, offset, length, context);
    }
    
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
