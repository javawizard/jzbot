package jw.jzbot.pastebin;

public class PastebinException extends RuntimeException
{
    
    public PastebinException()
    {
        super();
    }
    
    public PastebinException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public PastebinException(String message)
    {
        super(message);
    }
    
    public PastebinException(Throwable cause)
    {
        super(cause);
    }
    
}
