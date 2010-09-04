package jw.jzbot.scope;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.utils.Utils;

/**
 * A class that extends ServerUser with more information that the scope manager generally
 * doesn't have access to. Instances of this class must be constructed directly; they
 * cannot be obtained from
 * 
 * @author Alexander Boyd
 * 
 */
public class UserMessenger extends ServerUser
{
    private String hostname;
    private String username;
    
    public String getHostname()
    {
        return hostname;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public UserMessenger(String serverName, String nick, String username, String hostname)
    {
        super(serverName, nick);
        this.hostname = hostname;
        this.username = username;
    }
    
    public boolean isSuperop()
    {
        return JZBot.isSuperop(super.serverName, hostname);
    }
    
    /**
     * Throws an exception if this user is not a superop.
     */
    public void verifySuperop()
    {
        JZBot.verifySuperop(serverName, hostname);
    }
    
}
