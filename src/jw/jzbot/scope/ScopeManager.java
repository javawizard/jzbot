package jw.jzbot.scope;

import jw.jzbot.storage.Server;
import jw.jzbot.utils.Utils;

public class ScopeManager
{
    public Server addServer(String name)
    {
        return null;
    }
    
    public static final String SERVER_REGEX = "@([a-zA-Z0-9\\-_]+)";
    
    public static final String GLOBAL_REGEX = "@?";
    
    public static final String CHANNEL_REGEX = SERVER_REGEX + "(#[^ ]+)";
    
    public static final String USER_REGEX = SERVER_REGEX + "!([^ ]+)";
    
    /**
     * Gets an agent representing the specified canonical name.
     * 
     * @param name
     * @return
     */
    public static Agent getAgent(String name)
    {
        String[] groups;
        if ((groups = Utils.match(GLOBAL_REGEX, name)) != null)
        {
            return new GlobalScope();
        }
        else if ((groups = Utils.match(SERVER_REGEX, name)) != null)
        {
            return new ServerScope(groups[0]);
        }
        else if ((groups = Utils.match(CHANNEL_REGEX, name)) != null)
        {
            return new ChannelScope(groups[0], groups[1]);
        }
        else if ((groups = Utils.match(USER_REGEX, name)) != null)
        {
            return new ServerUser(groups[0], groups[1]);
        }
        throw new IllegalArgumentException("Agent name \"" + name
            + "\" isn't in a valid format. The agent name needs to "
            + "match one of the public static *_REGEX fields on this class.");
    }
    
    public static Messenger getMessenger(String name)
    {
        Agent agent = getAgent(name);
        if (!(agent instanceof Messenger))
            throw new IllegalArgumentException("Agent name \"" + name
                + "\" represents an agent that isn't a messenger.");
        return (Messenger) agent;
    }
    
    public static Scope getScope(String name)
    {
        Agent agent = getAgent(name);
        if (!(agent instanceof Scope))
            throw new IllegalArgumentException("Agent name \"" + name
                + "\" represents an agent that isn't a scope.");
        return (Scope) agent;
    }
    
}
