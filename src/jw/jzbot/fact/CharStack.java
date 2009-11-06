package jw.jzbot.fact;

/**
 * A stack of characters. Instances of this class are created by passing in a
 * string. The class essentially is an iterator over that string with methods
 * that allow it to act somewhat like a queue. FactParser uses it internally to
 * aid with parsing factoids.
 * 
 * @author Alexander Boyd
 * 
 */
public class CharStack
{
    private String string;
    private int index;
    
    public CharStack(String string)
    {
        this.string = string;
        this.index = 0;
    }
    
    /**
     * Gets the next character on this char stack, throwing an exception if
     * there are no more chars to be consumed.
     * 
     * @return
     */
    public char next()
    {
        try
        {
            char c = string.charAt(index);
            index += 1;
            return c;
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new ParseException(
                    index,
                    "Expected more input. This usually means that you haven't closed "
                            + "a function call or percent-sign variable reference properly.");
        }
    }
    
    /**
     * Goes back one character. This will cause {@link #next()} to return the
     * same character as it did the last time it was called. back can be called
     * multiple times to go back even further. If back is called when we haven't
     * consumed any chars yet or we've backed up to the beginning of the string,
     * it does nothing.
     */
    public void back()
    {
        if (index > 0)
            index -= 1;
    }
    
    /**
     * Returns true if there are more characters to be consumed. In other words,
     * this returns true if {@link next()} could be called without throwing an
     * index-related exception.
     * 
     * @return
     */
    public boolean more()
    {
        return index < string.length();
    }
    
    public int at()
    {
        return index;
    }
    
    /**
     * Gets the next char without actually reading it. The next call to
     * {@link #next()} will return this same character.
     * 
     * @return
     */
    public char peek()
    {
        return string.charAt(index);
    }
    
}
