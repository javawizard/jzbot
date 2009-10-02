package org.opengroove.jzbot.fact.functions;

import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

import org.jibble.pircbot.User;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

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
