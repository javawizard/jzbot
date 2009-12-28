package jw.jzbot.fact.functions.conditional;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import org.jibble.pircbot.User;

public class IfjoinedFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        User[] users = context.checkedGetConnection().getConnection().getUsers(
                context.getChannel());
        boolean isJoined = false;
        for (User user : users)
        {
            if (user.getNick().equalsIgnoreCase(arguments.getString(0)))
            {
                isJoined = true;
                break;
            }
        }
        if (isJoined)
            arguments.resolve(1, sink);
        else if (arguments.length() > 2)
            arguments.resolve(2, sink);
    }
    
    public String getName()
    {
        return "ifjoined";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{ifjoined||<nick>||<trueaction>||<falseaction>}} -- Evaluates to "
                + "<trueaction> if the user <nick> is currently a member of the channel that "
                + "the factoid is being run on, and <falseaction> if the user is not currently "
                + "a member of that channel or if the user is offline. <falseaction> is optional."
                + " if <trueaction> and <falseaction> are bot missing, then this \n"
                + "function evaluates to 1 if the user is joined to the channel "
                + "and 0 if the user is not joined.";
    }
    
}
