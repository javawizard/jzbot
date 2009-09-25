package org.opengroove.jzbot.fact;

public class Literal extends FactEntity
{
    private StringBuffer value = new StringBuffer();
    
    private String finalized;
    
    public Literal()
    {
    }
    
    public Literal(String s)
    {
        this();
        append(s);
    }
    
    public void append(String s)
    {
        value.append(s);
    }
    
    public void append(char c)
    {
        value.append(c);
    }
    
    public void finalize()
    {
        finalized = value.toString();
        value = null;
    }
    
    /**
     * Returns the value passed to {@link #Literal(String)}.
     */
    @Override
    public String execute(FactContext context)
    {
        if (finalized == null)
            finalize();
        return finalized;
    }
    
    @Override
    public String explain(int indentation, int increment)
    {
        return spaces(indentation) + "literal: \"" + resolve(null) + "\"\n";
    }
    
}
