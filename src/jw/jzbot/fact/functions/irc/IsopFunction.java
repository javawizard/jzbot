package jw.jzbot.fact.functions.irc;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import org.jibble.pircbot.User;

public class IsopFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String channel;
        String user;
        if (arguments.length() == 1)
        {
            channel = context.getChannel();
            user = arguments.getString(0);
        }
        else
        {
            channel = arguments.getString(0);
            user = arguments.getString(1);
        }
        User[] users =context.checkedGetConnection().getConnection().getUsers(channel);
        for (User userObject : users)
        {
            System.out.println("Scanning for user " + userObject.getNick());
            if (userObject.getNick().equalsIgnoreCase(user))
            {
                if (userObject.isOp())
                    sink.write('1');
                else
                    sink.write('0');
                return;
            }
        }
        throw new FactoidException("Isop on user \"" + user + "\", channel \"" + channel
                + "\": user is not connected, consider using {{ifjoined}} "
                + "to see if the user is joined");
    }
    
    public String getName()
    {
        return "isop";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{isop||<channel>||<nick>}} -- Evaluates to \"1\" if the nick specified is "
                + "a channel operator at this channel or \"0\" if the nick specified is not"
                + " a channel operator at this channel. %self% can be used to get the bot's "
                + "own nick, so {{isop||%self%}} would indicate whether the bot has operator "
                + "privileges at this channel.\n"
                + "<channel> means the channel to check at. If it's not present, then the "
                + "channel that this factoid is being run at will be used.";
    }
    
}
