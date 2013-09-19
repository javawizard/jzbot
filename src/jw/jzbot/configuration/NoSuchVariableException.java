package jw.jzbot.configuration;

public class NoSuchVariableException extends RuntimeException
{
    
    public NoSuchVariableException(String variable)
    {
        super(variable);
    }
    
}
