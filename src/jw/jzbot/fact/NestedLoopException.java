package jw.jzbot.fact;

/**
 * An exception thrown from within a loop when {break} or {continue} is run.
 * 
 * @author Alexander Boyd
 * 
 */
public class NestedLoopException extends FactoidException
{
    public int level;

    public NestedLoopException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public NestedLoopException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public NestedLoopException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
