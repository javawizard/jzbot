package jw.jzbot.crosstalk;

public interface Callback
{
    public Command ready(CrosstalkSession session, boolean initial, Response response);
    
    public void done(CrosstalkSession session);
    
    public void failed(CrosstalkSession session, boolean local, ErrorType type,
            String message);
}
