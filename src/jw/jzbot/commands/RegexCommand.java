package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.ServerUser;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Regex;
import jw.jzbot.storage.Server;
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
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        if (server == null || channel == null)
            throw new ResponseException(
                    "The regex command needs to be scoped to a channel.");
        sender.verifySuperop();
        doRegexCommand(server, channel, pm, sender, source, arguments, true);
    }
    
    public static void doRegexCommand(String server, String channel, boolean pm,
            ServerUser sender, Messenger source, String arguments, boolean reply)
    {
        Server dServer = JZBot.storage.getServer(server);
        String[] argSplit1 = arguments.split(" ", 2);
        String command = argSplit1[0];
        String args = (argSplit1.length > 1 ? argSplit1[1] : "");
        Channel c = dServer.getChannel(channel);
        if (c == null)
            throw new ResponseException("This channel does not have a factoid database.");
        if (command.equals("add"))
        {
            int index = args.lastIndexOf(" ");
            if (index == -1)
                throw new ResponseException("You need to specify the regex and the factoid"
                    + " that it should call.");
            String regexString = args.substring(0, index);
            String factoid = args.substring(index + 1);
            if (factoid.length() < 1)
                throw new ResponseException("The factoid to run must be "
                    + "non-empty. Make sure you don't have a trailing "
                    + "space in the command string you juse used.");
            Regex existing = c.getRegex(regexString);
            if (existing != null)
                throw new ResponseException("That exact same regex already exists here.");
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
                source.sendMessage("Successfully added and activated.");
        }
        else if (command.equals("delete") || command.equals("del"))
        {
            if (args.equals(""))
                throw new ResponseException("You need to specify the regex to delete. "
                    + "Make sure that you only specify the regex pattern "
                    + "itself, not the factoid that will be run when the "
                    + "regex is triggered.");
            Regex regex = c.getRegex(args);
            if (regex == null)
                throw new ResponseException("That regex (" + args
                    + ") doesn't exist on this channel. Make sure you're "
                    + "only including the regex pattern to match, *not* the "
                    + "name of the factoid that is supposed to be run "
                    + "when something matches the regex.");
            c.getRegularExpressions().remove(regex);
            JZBot.reloadRegexes();
            if (reply)
                source.sendMessage("Successfully removed and deactivated.");
            
        }
        // else if (command.equals("deleteall"))
        // {
        // c.getRegularExpressions().clear();
        // JZBot.reloadRegexes();
        // if (reply)
        // source.sendMessage("Successfully deleted all regexes on this channel.");
        // }
        else if (command.equals("list"))
        {
            StringBuffer buffer = new StringBuffer();
            for (Regex regex : c.getRegularExpressions().isolate())
            {
                buffer.append(regex.getExpression() + " " + regex.getFactoid() + "\n");
            }
            String[] split = buffer.toString().split("\n");
            if (split.length > 2 && source.likesPastebin())
            {
                if (reply)
                    source.sendMessage("Regex list: "
                        + Pastebin.createPost("jzbot", buffer.toString(), Duration.DAY,
                                null, null));
            }
            else
            {
                for (String s : split)
                {
                    if (reply)
                        source.sendMessage(s);
                }
                if (reply)
                    source.sendMessage("End of regex list");
            }
        }
        else
        {
            throw new ResponseException("Specify one of add, delete (or del), or list.");
        }
    }

    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
