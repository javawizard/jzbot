package jw.jzbot.scope;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.utils.Utils;

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
    
    public boolean likesPastebin()
    {
        return con.getConnection().likesPastebin();
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
    
    @Override
    public void sendSpaced(String message)
    {
        Utils.sendSpaced(this, message);
    }
    
    @Override
    public String getScopeName()
    {
        throw new IllegalStateException("Generic messengers don't have "
            + "the ability to provide scope information.");
    }
    
}
