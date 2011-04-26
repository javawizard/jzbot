package jw.jzbot.crosstalk;

public interface Handler
{
    public Response handshake(CrosstalkSession session);
    
    public Response commandReceived(CrosstalkSession session, Command command);
    
    public void done(CrosstalkSession session);
    
    public void failed(CrosstalkSession session, boolean local, ErrorType type,
            String message);
}
