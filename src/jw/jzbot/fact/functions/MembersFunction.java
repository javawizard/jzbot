package jw.jzbot.fact.functions;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

import org.jibble.pircbot.User;

public class MembersFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return StringUtils.delimited(JZBot.bot.getUsers(arguments.get(0)),
                new ToString<User>()
                {
                    
                    @Override
                    public String toString(User object)
                    {
                        return object.getNick();
                    }
                }, " ");
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{members||<channel>}} -- Evaluates to a space-separated list "
                + "of the nicknames of the people that are currently in <channel>.";
    }
    
}
