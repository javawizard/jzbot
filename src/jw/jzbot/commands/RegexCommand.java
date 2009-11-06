package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Regex;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;


public class RegexCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "regex";
    }
    
    @Override
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        JZBot.verifyOp(channel, hostname);
        doRegexCommand(channel, pm, sender, arguments, true);
    }
    
    public static void doRegexCommand(String channel, boolean pm,
            String sender, String arguments, boolean reply)
    {
        String[] argSplit1 = arguments.split(" ", 2);
        String command = argSplit1[0];
        String args = (argSplit1.length > 1 ? argSplit1[1] : "");
        Channel c = JZBot.storage.getChannel(channel);
        if (c == null)
            throw new ResponseException("Orphaned channel.");
        if (command.equals("add"))
        {
            int index = args.lastIndexOf(" ");
            if (index == -1)
                throw new ResponseException(
                        "You need to specify the regex and the factoid"
                                + " that it should call.");
            String regexString = args.substring(0, index);
            String factoid = args.substring(index + 1);
            Regex existing = c.getRegex(regexString);
            if (existing != null)
                throw new ResponseException(
                        "That exact same regex already exists here.");
            try
            {
                Pattern.compile(regexString);
            }
            catch (Exception e)
            {
                throw new ResponseException("Malformed regex pattern, see "
                        + JZBot.pastebinStack(e));
            }
            Regex regex = JZBot.storage.createRegex();
            regex.setExpression(regexString);
            regex.setFactoid(factoid);
            c.getRegularExpressions().add(regex);
            JZBot.reloadRegexes();
            if (reply)
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "Successfully added and activated.");
        }
        else if (command.equals("delete"))
        {
            if (args.equals(""))
                throw new ResponseException(
                        "You need to specify the regex to delete.");
            Regex regex = c.getRegex(args);
            if (regex == null)
                throw new ResponseException("That regex (" + args
                        + ") doesn't exist on this channel.");
            c.getRegularExpressions().remove(regex);
            JZBot.reloadRegexes();
            if (reply)
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "Successfully removed and deactivated.");
            
        }
        else if (command.equals("deleteall"))
        {
            c.getRegularExpressions().clear();
            JZBot.reloadRegexes();
            if (reply)
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "Successfully deleted all regexes on this channel.");
        }
        else if (command.equals("list"))
        {
            StringBuffer buffer = new StringBuffer();
            for (Regex regex : c.getRegularExpressions().isolate())
            {
                buffer.append(regex.getExpression() + " " + regex.getFactoid()
                        + "\n");
            }
            String[] split = buffer.toString().split("\n");
            if (split.length > 2)
            {
                if (reply)
                    JZBot.bot.sendMessage(pm ? sender : channel,
                            "Regex list: http://pastebin.com/"
                                    + Pastebin.createPost("jzbot", buffer
                                            .toString(), Duration.DAY, ""));
            }
            else
            {
                for (String s : split)
                {
                    if (reply)
                        JZBot.bot.sendMessage(pm ? sender : channel, s);
                }
                if (reply)
                    JZBot.bot.sendMessage(pm ? sender : channel,
                            "End of regex list");
            }
        }
        else
        {
            throw new ResponseException(
                    "Specify one of add, delete, deleteall, or list.");
        }
    }
}
