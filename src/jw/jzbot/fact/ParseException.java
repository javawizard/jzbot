package jw.jzbot.fact;

public class ParseException extends RuntimeException
{
    public ParseException(int index, String message)
    {
        super("Syntax error at character " + index + ": " + message);
    }
}
