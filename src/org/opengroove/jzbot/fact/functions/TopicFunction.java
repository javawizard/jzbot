package org.opengroove.jzbot.fact.functions;

import org.jibble.pircbot.PircBot;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.Function;

public class TopicFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        if (arguments.length() == 0)// getting channel topic
        {
            String topic = JZBot.channelTopics.get(context.getChannel());
            if (topic == null)
                topic = "";
            return topic;
        }
        else
        // setting channel topic
        {
            JZBot.bot.setTopic(context.getChannel(), arguments.get(0));
            return "";
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{topic}} or {{topic||<newtopic>}} -- If used without any "
                + "arguments, evaluates to the current channel's topic. If used with "
                + "one argument, sets the current channel's topic to be <newtopic>. "
                + "The bot must be a channel operator on most channels before this can "
                + "be run.";
    }
    
}
