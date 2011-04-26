package jw.jzbot.crosstalk;

public interface Callback
{
    public Command nextCommand(Response response);
    
    public void failed(boolean local, ErrorType type,
            String message);
}
