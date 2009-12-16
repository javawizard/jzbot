package jw.jzbot.commands;

import java.nio.charset.Charset;

import jw.jzbot.Command;
import jw.jzbot.ConfigVars;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.utils.JZUtils;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

import net.sf.opengroove.common.utils.StringUtils;

public class ConfigCommand implements Command
{
    
    public String getName()
    {
        return "config";
    }
    
    public void run(String server, String channel, boolean pm, String sender,
            String hostname, String arguments)
    {
        if (!JZBot.isSuperop(server, hostname))
        {
            JZBot.bot.sendMessage(pm ? sender : channel, "You're not a superop.");
            return;
        }
        String[] tokens = arguments.split(" ", 2);
        if (!tokens[0].equals(""))
        {
            ConfigVars var = ConfigVars.valueOf(tokens[0]);
            if (var == null)
                throw new ResponseException(
                        "That isn't a valid var name. Use \"~config\" to see "
                                + "a list of var names.");
            if (tokens.length == 1)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "This variable's current value is \"" + var.get()
                                + "\". You can use \"~config " + var.name()
                                + " <newvalue>\" to set a new value."
                                + " The variable's description is:");
                JZBot.bot.sendMessage(pm ? sender : channel, var.getDescription());
            }
            else
            {
                var.set(tokens[1]);
                JZBot.bot.sendMessage(pm ? sender : channel, "Successfully set the var \""
                        + var.name() + "\" to have the value \"" + tokens[1] + "\".");
            }
        }
        else
        {
            String[] configVarNames = new String[ConfigVars.values().length];
            for (int i = 0; i < configVarNames.length; i++)
            {
                configVarNames[i] = ConfigVars.values()[i].name();
            }
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Use \"~config <varname>\" to see a variable's current value and "
                            + "a short description of the variable, " + "or \"~config "
                            + "<varname> <value>\" to " + "set the value "
                            + "of a variable. Currently, "
                            + "allowed variable names are, separated by spaces:");
            JZUtils
                    .ircSendDelimited(configVarNames, "  ", JZBot.bot, pm ? sender
                            : channel);
        }
    }
}
