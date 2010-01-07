package jw.jzbot.fact.functions.irc;

import jw.jzbot.Connection;
import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

import org.jibble.pircbot.User;

public class MembersFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        // TODO: change this to use a delimited sink
        String serverName = context.getServer();
        if (serverName == null)
            throw new FactoidException("Can't run {members} when there is "
                    + "no server in the current scope.");
        String channelName = context.getChannel();
        if (arguments.length() > 0)
            channelName = arguments.resolveString(0);
        else if (channelName == null)
            throw new FactoidException(
                    "Current scope only contains a server, not a channel, "
                            + "and no channel was specified as an argument");
        ConnectionWrapper wrapper = JZBot.getConnection(serverName);
        Connection con = wrapper.getContext().getConnection();
        sink.write(StringUtils.delimited(con.getUsers(channelName), new ToString<User>()
        {
            
            @Override
            public String toString(User object)
            {
                return object.getNick();
            }
        }, "\n"));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {members} or {members|<channel>} -- Evaluates to a "
                + "newline-separated list of the nicknames of the people in the current "
                + "channel. The second form is deprecated; the additional argument to the "
                + "function specifies the channel to get members from. This form is\n"
                + "deprecated, and should be replaced with (and is functionally "
                + "identical to) {scope|<channel>|{members}}.";
        // return "Syntax: {members|<channel>} -- Evaluates to a space-separated list "
        // + "of the nicknames of the people that are currently in <channel>.";
    }
    
}
