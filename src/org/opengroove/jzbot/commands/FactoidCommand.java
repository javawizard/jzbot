package org.opengroove.jzbot.commands;

import java.util.Date;

import net.sf.opengroove.common.proxystorage.StoredList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Factoid;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

public class FactoidCommand implements Command
{
    public static String PASTEBIN_REGEX = "^http\\://pastebin\\.com/.{6,12}$";
    
    public String getName()
    {
        return "factoid";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        boolean isGlobal = false;
        if (arguments.startsWith("global "))
        {
            isGlobal = true;
            arguments = arguments.substring("global ".length());
        }
        String[] argumentsTokenized1 = arguments.split(" ", 2);
        String command = argumentsTokenized1[0];
        if ((!isGlobal) && (channel == null)
                && (!command.equalsIgnoreCase("isglobal")))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "For non-global commands, you must specify "
                            + "a channel (unless it is the isglobal command)");
            return;
        }
        String afterCommand = (argumentsTokenized1.length > 1) ? argumentsTokenized1[1]
                : "";
        /*
         * command is something like create, delete, isglobal, etc., and
         * afterCommand is the rest
         */
        Channel c = null;
        if (!isGlobal)
            c = JZBot.storage.getChannel(channel);
        boolean processed = false;
        /*
         * oldFact is set to the old factoid when the replace command is used.
         * This is then used when the new factoid is created to set its
         * restricted status and request counts.
         */
        Factoid oldFact = null;
        if (command.equals("delete") || command.equals("replace"))
        {
            processed = true;
            verifyOpSuperop(isGlobal, channel, hostname);
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2 && command.equals("replace"))
                throw new ResponseException(
                        "You need to specify the factoid itself");
            String factoidName = argumentsTokenized2[0];
            if (factoidName.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f;
            if (isGlobal)
                f = JZBot.storage.getFactoid(factoidName);
            else
                f = c.getFactoid(factoidName);
            if (f == null)
            {
                if ((!isGlobal)
                        && JZBot.storage.getFactoid(afterCommand) != null)
                    throw new ResponseException(
                            "That factoid doesn't exist. However, there is a global "
                                    + "factoid with that name. Use \"factoid global\" instead "
                                    + "of \"factoid\" in the command to do stuff with "
                                    + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist");
            }
            if (isGlobal)
                JZBot.storage.getFactoids().remove(f);
            else
                c.getFactoids().remove(f);
            if (command.equals("delete"))
                JZBot.bot.sendMessage(pm ? sender : channel, "Factoid "
                        + afterCommand + " deleted.");
            if (command.equals("replace"))
                oldFact = f;
        }
        if (command.equals("create") || command.equals("replace"))
        {
            processed = true;
            verifyOpSuperop(isGlobal, channel, hostname);
            if (afterCommand.equals(""))
                throw new ResponseException(
                        "You need to specify the factoid name");
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2)
                throw new ResponseException(
                        "You need to specify the factoid contents");
            String factoidName = argumentsTokenized2[0];
            if (JZBot.commands.get(factoidName) != null)
                throw new ResponseException("That is a reserved keyword.");
            String factoidContents = argumentsTokenized2[1];
            if (c != null && c.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a channel-specific factoid");
            else if (c == null && JZBot.storage.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a global factoid");
            factoidContents = scanForPastebin(factoidContents);
            /*
             * The factoid does not exist. Let's create it. First, we'll try
             * parsing it to make sure we don't have syntax errors.
             */
            try
            {
                FactParser.parse(factoidContents, "__internal_create_"
                        + factoidName);
            }
            catch (Exception e)
            {
                recreate(oldFact, isGlobal, c);
                throw new ResponseException(
                        "There is a syntax error in the contents of the factoid: "
                                + JZBot.pastebinStack(e));
            }
            Factoid f = JZBot.storage.createFactoid();
            f.setCreator(hostname);
            f.setName(factoidName);
            f.setActive(true);
            f.setValue(factoidContents);
            // history stuff
            f.setCreationTime(System.currentTimeMillis());
            f.setCreatorNick(sender);
            f.setCreatorUsername(JZBot.getThreadLocalUsername());
            f.setDirectRequests(0);
            f.setIndirectRequests(0);
            if (oldFact != null)
            {
                f.setRestricted(oldFact.isRestricted());
                f.setDirectRequests(oldFact.getDirectRequests());
                f.setIndirectRequests(oldFact.getIndirectRequests());
            }
            if (isGlobal)
                JZBot.storage.getFactoids().add(f);
            else
                c.getFactoids().add(f);
            System.out.println("created fact " + factoidName + " "
                    + factoidContents);
            JZBot.bot
                    .sendMessage(pm ? sender : channel, "Factoid "
                            + factoidName
                            + (command.equals("replace") ? " replaced. "
                                    : " created."));
        }
        if (command.equals("list"))
        {
            processed = true;
            System.out.println("command is list, " + isGlobal);
            // JZBot.bot.sendMessage(pm ? sender : channel,
            // "Start of factoid list");
            StoredList<Factoid> list;
            if (isGlobal)
                list = JZBot.storage.getFactoids();
            else
                list = c.getFactoids();
            if (list != null)
            {
                String currentList = "";
                for (Factoid f : list.isolate())
                {
                    currentList += f.getName() + "    ";
                    if (currentList.length() > 400)
                    {
                        JZBot.bot.sendMessage(pm ? sender : channel,
                                currentList);
                        currentList = "";
                    }
                }
                if (!currentList.equals(""))
                    JZBot.bot.sendMessage(pm ? sender : channel, currentList);
            }
            JZBot.bot
                    .sendMessage(
                            pm ? sender : channel,
                            "End of factoid list. "
                                    + (isGlobal ? ""
                                            : "You should also run factoid global list for"
                                                    + " global factoids. These were not included "
                                                    + "in this list."));
        }
        if (command.equals("literal"))
        {
            processed = true;
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f;
            if (isGlobal)
                f = JZBot.storage.getFactoid(afterCommand);
            else
                f = c.getFactoid(afterCommand);
            if (f == null)
            {
                if ((!isGlobal)
                        && JZBot.storage.getFactoid(afterCommand) != null)
                    throw new ResponseException(
                            "That factoid doesn't exist. However, there is a global "
                                    + "factoid with that name. Use \"factoid global\" instead "
                                    + "of \"factoid\" in the command to do stuff with "
                                    + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist");
            }
            String value = f.getValue();
            if (value.contains("\n") || value.contains("\r")
                    || value.length() > 400 || value.matches(PASTEBIN_REGEX))
                value = "http://pastebin.com/"
                        + Pastebin.createPost("jzbot", value, Duration.DAY,
                                null);
            JZBot.bot.sendMessage(pm ? sender : channel, value);
        }
        if (command.equals("info"))
        {
            processed = true;
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f;
            if (isGlobal)
                f = JZBot.storage.getFactoid(afterCommand);
            else
                f = c.getFactoid(afterCommand);
            if (f == null)
            {
                if ((!isGlobal)
                        && JZBot.storage.getFactoid(afterCommand) != null)
                    throw new ResponseException(
                            "That factoid doesn't exist. However, there is a global "
                                    + "factoid with that name. Use \"factoid global\" instead "
                                    + "of \"factoid\" in the command to do stuff with "
                                    + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist");
            }
            int directRequests = f.getDirectRequests();
            int indirectRequests = f.getIndirectRequests();
            int totalRequests = directRequests + indirectRequests;
            JZBot.bot.sendMessage(pm ? sender : channel, "" + f.getName()
                    + " -- created by " + f.getCreatorNick() + " <"
                    + f.getCreatorUsername() + "@" + f.getCreator() + "> at "
                    + new Date(f.getCreationTime()).toString() + "; requested "
                    + totalRequests + " times (" + directRequests
                    + " directly, " + indirectRequests + " indirectly)");
        }
        if (!processed)
        {
            throw new ResponseException(
                    "Invalid factoid command. Try 'factoid [global] <list|create|replace|delete|literal|info>'");
        }
    }
    
    private String scanForPastebin(String factoidContents)
    {
        if (factoidContents.matches(PASTEBIN_REGEX))
        {
            factoidContents = Pastebin.readPost(factoidContents.trim());
        }
        return factoidContents;
    }
    
    private void recreate(Factoid oldFact, boolean isGlobal, Channel c)
    {
        if (oldFact != null)
        {
            if (isGlobal)
            {
                if (JZBot.storage.getFactoid(oldFact.getName()) == null)
                    JZBot.storage.getFactoids().add(oldFact);
            }
            else
            {
                if (c.getFactoid(oldFact.getName()) == null)
                    c.getFactoids().add(oldFact);
            }
        }
    }
    
    /**
     * Verifies that this is a superop if isGlobal is true. Otherwise, verifies
     * that this is an op.
     * 
     * @param isGlobal
     * @param channel
     * @param hostname
     */
    private void verifyOpSuperop(boolean isGlobal, String channel,
            String hostname)
    {
        if (isGlobal)
            JZBot.bot.verifySuperop(hostname);
        else
            JZBot.bot.verifyOp(channel, hostname);
    }
}
