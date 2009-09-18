package org.opengroove.jzbot.commands;

import java.nio.charset.Charset;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.utils.JZUtils;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

public class ConfigCommand implements Command
{
    
    public String getName()
    {
        return "config";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (!JZBot.isSuperop(hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "You're not a superop.");
            return;
        }
        String[] tokens = arguments.split(" ");
        if (tokens[0].equals("delay"))
        {
            if (tokens.length == 1)
            {
                JZBot.bot.sendMessage(pm ? sender : channel, "Delay is "
                        + JZBot.bot.getMessageDelay());
                return;
            }
            int delay = Integer.parseInt(tokens[1]);
            JZBot.bot.setMessageDelay(delay);
            JZBot.bot.sendMessage(pm ? sender : channel, "Delay set to "
                    + delay + " (session local)");
        }
        else if (tokens[0].equals("charset"))
        {
            if (tokens.length == 1)
            {
                JZBot.bot
                        .sendMessage(
                                pm ? sender : channel,
                                "Charset is "
                                        + JZBot.getCurrentCharset()
                                        + ". Allowed charsets (separated by spaces) are: http://pastebin.com/"
                                        + Pastebin.createPost("jzbot",
                                                StringUtils.delimited(Charset
                                                        .availableCharsets()
                                                        .keySet().toArray(
                                                                new String[0]),
                                                        "   "), Duration.DAY,
                                                null));
                return;
            }
            String charset = tokens[1];
            JZBot.setCurrentCharset(charset);
            JZBot.bot.sendMessage(pm ? sender : channel, "Charset set to "
                    + charset + ". Reconnect the bot (with ~reconnect) "
                    + "as soon as possible.");
        }
        else if (tokens[0].equals("evalengine"))
        {
            if (tokens.length == 1)
            {
                JZBot.bot.sendMessage(pm ? sender : channel, "Evalengine is "
                        + JZBot.config.getEvalEngine());
                return;
            }
            if (!StringUtils.isMemberOf(tokens[1], JZBot.evalEngines.keySet()
                    .toArray(new String[0])))
            {
                throw new ResponseException(
                        "Invalid eval engine. Valid values are (separated by space): "
                                + StringUtils.delimited(JZBot.evalEngines
                                        .keySet().toArray(new String[0]), " "));
            }
            JZBot.config.setEvalEngine(tokens[1]);
            JZBot.bot.sendMessage(pm ? sender : channel, "Evalengine set to "
                    + tokens[1]);
        }
        else
        {
            JZBot.bot
                    .sendMessage(
                            pm ? sender : channel,
                            "Use \"~config <varname>\" to see a var or \"~config "
                                    + "<varname> <value>\" to set a var. Currently, "
                                    + "allowed varnames are delay, evalengine, and charset.");
        }
    }
}
