package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public class ScopeCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "scope";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        if (server == null)
            throw new ResponseException("The scope command can only be run in "
                + "the context of a server.");
        sender.verifySuperop();
        String keyName = "@" + sender.getServerName() + "!" + sender.getNick();
        synchronized (JZBot.pmUserScopeLock)
        {
            if (arguments.equals(""))
            {
                if (JZBot.pmUserScopeMap.get(keyName) == null)
                    source.sendSpaced("You have not set a scope. To set one, use "
                        + "\"scope <scope-to-use>\". Your current scope "
                        + "therefore defaults to \"@" + sender.getServerName() + "\"");
                else
                    source.sendSpaced("Your current scope is \""
                        + JZBot.pmUserScopeMap.get(keyName)
                        + "\". You can change it with \"scope <scope-to-use>\" "
                        + "and you can change back to the default scope "
                        + "with \"scope implicit\".");
            }
            else
            {
                if (arguments.equals("implicit"))
                {
                    JZBot.pmUserScopeMap.remove(keyName);
                    source.sendSpaced("Your scope has been reset to \"@"
                        + sender.getServerName() + "\".");
                }
                else
                {
                    JZBot.pmUserScopeMap.put(keyName, arguments);
                    JZBot.pmUserScopeTimes.put(keyName, System.currentTimeMillis());
                    source.sendSpaced("Your scope has been set to \"" + arguments
                        + "\". If you stop sending messages for 10 minutes, this "
                        + "will be reset. You can also reset it with "
                        + "\"scope implicit\".");
                }
            }
        }
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
