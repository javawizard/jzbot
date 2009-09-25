package org.opengroove.jzbot.fact;

public abstract class FactEntity
{
    /**
     * Resolves this entity into an output string. This is what you use to
     * actually run a factoid once you've parsed it with FactParser.
     * 
     * @param context
     * @return
     */
    public final String resolve(FactContext context)
    {
        try
        {
            return execute(context);
        }
        catch (FactoidException e)
        {
            addStackFrame(e);
            throw e;
        }
    }
    
    /**
     * Called by {@link #resolve(FactContext)} when a FactoidException is thrown
     * to add a stack frame to the factoid stack trace. This method does some
     * general cleanup, but doesn't actually add a stack frame. Subclasses can
     * override this to add a stack frame, but they should always call
     * <tt>super.addStackFrame</tt> within the overriden method.
     * 
     * @param e
     *            The factoid exception
     */
    protected void addStackFrame(FactoidException e)
    {
        if (factText != null && !e.getFactContentMap().containsKey(factName))
        {
            e.getFactContentMap().put(factName, factText);
        }
    }
    
    protected abstract String execute(FactContext context);
    
    /**
     * Creates a detailed version of the tree structure of this entity. Probably
     * the best way to see what this does is to run it on a particularly complex
     * factoid (such as the stereotypical roulette factoid).
     * 
     * @param indentation
     * @param increment
     * @return
     */
    public abstract String explain(int indentation, int increment);
    
    /**
     * Returns a string that contains exactly <tt>number</tt> characters, all of
     * which are spaces. This is primarily intended for the use of the explain()
     * method, to allow for indentation.
     * 
     * @param number
     * @return
     */
    protected String spaces(int number)
    {
        char[] c = new char[number];
        for (int i = 0; i < number; i++)
        {
            c[i] = ' ';
        }
        return new String(c);
    }
    
    /**
     * Calls explain(0, 4) and returns the result.
     */
    public String toString()
    {
        return explain(0, 4);
    }
    
    /**
     * Gets the character index for this fact entity. The character index is the
     * position, in number of characters, within the factoid that the start of
     * this entity appears. For example, in the factoid
     * "{{pset||something||{{pget||somethingelse}}}}", the character index of
     * the function reference corresponding to the "pget" function call would be
     * 19, since that's the position at which the first opening-brace of the
     * function call occurs. The literal entity "pget", however, would have a
     * character index of 21, since that's where the "p" is at.,
     * 
     * @return
     */
    public int getCharIndex()
    {
        return charIndex;
    }
    
    /**
     * Sets the character index for this fact entity. See
     * {@link #getCharIndex()} for information on what the character index is.
     * 
     * @param charIndex
     */
    public void setCharIndex(int charIndex)
    {
        this.charIndex = charIndex;
    }
    
    private int charIndex;
    
    private String factName;
    
    public String getFactName()
    {
        return factName;
    }
    
    public void setFactName(String factName)
    {
        this.factName = factName;
    }
    
    private String factText;
    
    public String getFactText()
    {
        return factText;
    }
    
    public void setFactText(String factText)
    {
        this.factText = factText;
    }
}
