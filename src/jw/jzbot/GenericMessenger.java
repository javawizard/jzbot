package jw.jzbot;

/**
 * A generic messenger that can be used when only a connection wrapper and a recipient
 * name are available. Generally, ServerUser and ServerChannel should be used in
 * preference to this class.
 * 
 * @author Alexander Boyd
 * 
 */
public class GenericMessenger implements Messenger
{
    private ConnectionWrapper con;
    private String recipient;
    
    public GenericMessenger(ConnectionWrapper con, String recipient)
    {
        super();
        this.con = con;
        this.recipient = recipient;
    }
    
    @Override
    public int getProtocolDelimitedLength()
    {
        return con.getConnection().getProtocolDelimitedLength();
    }
    
    @Override
    public void sendAction(String action)
    {
        con.sendAction(recipient, action);
    }
    
    @Override
    public void sendMessage(String message)
    {
        con.sendMessage(recipient, message);
    }
    
}
