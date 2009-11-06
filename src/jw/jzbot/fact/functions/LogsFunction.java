package jw.jzbot.fact.functions;

import java.io.File;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;

import net.sf.opengroove.common.utils.StringUtils;


public class LogsFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        File file = new File(JZBot.logsFolder, context.getChannel());
        if (!file.exists())
            return "";
        return StringUtils.readFile(file);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{logs||<max>}} -- Returns the last few messages that were "
                + "sent at this channel. The "
                + "resulting log events will be separated by a newline character. Each "
                + "line is of the format <action> <time> <source> <details>. <action> "
                + "is the action that occured, which is one of \"mode\", \"kick\", \n"
                + "\"joined\", \"left\", \"nick\", \"message\", or \"action\" right now. <time> is the "
                + "number of milliseconds since the epoch. <source> is the nick that "
                + "caused the action to occur. <details> varies depending on the action. "
                + "For mode, details is the IRC-format mode string that happened, such as "
                + "\"+o jcp\". For kick, details is the name of the person that was kicked, "
                + "a space, and the reason that the person was kicked.\n"
                + "For joined, details is \"username@host\" for that user. For left, details "
                + "is empty. For nick, details is the new nick of the user. For action and "
                + "message, details is the action or message sent by that user. For topic, "
                + "details is the new topic of the channel.";
    }
    
}
