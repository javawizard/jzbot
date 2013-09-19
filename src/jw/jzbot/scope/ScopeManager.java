package jw.jzbot.scope;

import jw.jzbot.JZBot;
import jw.jzbot.storage.Server;
import jw.jzbot.storage.StorageContainer;
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
    
    public static final String CHANNEL_RELATIVE_REGEX = "(#[^ ]+)";
    
    public static final String USER_RELATIVE_REGEX = "!?([^#@][^ ]*)";
    
    /**
     * Gets an agent representing the specified name. The name can be relative, in which
     * case it will be resolved against the specified scope. The specified scope can be
     * null if the name is an absolute name.
     * 
     * @param name
     * @return
     */
    public static Agent getAgent(Scope scope, String name)
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
        if (scope == null)
            throw new IllegalArgumentException("Agent name \"" + name
                + "\" isn't an absolute name, and no relative scope was "
                + "specified so this method can't check to see if the "
                + "agent name is relative.");
        if ((groups = Utils.match(CHANNEL_RELATIVE_REGEX, name)) != null)
        {
            verifyHasServer(scope);
            return new ChannelScope(scope.getServerName(), groups[0]);
        }
        else if ((groups = Utils.match(USER_RELATIVE_REGEX, name)) != null)
        {
            verifyHasServer(scope);
            return new ServerUser(scope.getServerName(), groups[0]);
        }
        throw new IllegalArgumentException("Agent name \"" + name
            + "\" isn't in a valid format. The agent name needs to "
            + "match one of the public static *_REGEX fields on this class.");
    }
    
    public static void verifyHasServer(Scope scope)
    {
        if (!hasServer(scope))
            throw new IllegalArgumentException("The scope \"" + scope.getCanonicalName()
                + "\" does not have a server.");
    }
    
    public static boolean hasServer(Scope scope)
    {
        return scope.getServerName() != null;
    }
    
    public static Messenger getMessenger(Scope scope, String name)
    {
        Agent agent = getAgent(scope, name);
        if (!(agent instanceof Messenger))
            throw new IllegalArgumentException("Agent name \"" + name
                + "\" represents an agent that isn't a messenger.");
        return (Messenger) agent;
    }
    
    public static Scope getScope(Scope scope, String name)
    {
        Agent agent = getAgent(scope, name);
        if (!(agent instanceof Scope))
            throw new IllegalArgumentException("Agent name \"" + name
                + "\" represents an agent that isn't a scope.");
        return (Scope) agent;
    }
    
    public static Agent getAgent(String name)
    {
        return getAgent(null, name);
    }
    
    public static Messenger getMessenger(String name)
    {
        return getMessenger(null, name);
    }
    
    public static Scope getScope(String name)
    {
        return getScope(null, name);
    }
    
    public static StorageContainer getStorageContainer(Scope scope)
    {
        if (scope instanceof GlobalScope)
            return JZBot.storage;
        else if (scope instanceof ServerScope)
            return JZBot.storage.getServer(scope.getServerName());
        else if (scope instanceof ChannelScope)
            return JZBot.storage.getServer(scope.getServerName()).getChannel(
                    scope.getChannelName());
        else
            throw new IllegalArgumentException("Invalid scope: " + scope);
    }
}
