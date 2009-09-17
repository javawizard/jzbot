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
    public abstract String resolve(FactContext context);
    
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
    
    public String toString()
    {
        return explain(0, 4);
    }
}
