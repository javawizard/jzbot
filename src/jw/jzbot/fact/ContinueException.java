package jw.jzbot.fact;

/**
 * An exception thrown from within
 * 
 * @author Alexander Boyd
 * 
 */
public class ContinueException extends NestedLoopException
{
    
    public ContinueException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    
    public ContinueException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public ContinueException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }
    
}
