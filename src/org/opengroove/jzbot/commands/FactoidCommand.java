package org.opengroove.jzbot.commands;

import net.sf.opengroove.common.proxystorage.StoredList;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Factoid;

public class FactoidCommand implements Command
{
    
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
        if (command.equals("delete") || command.equals("replace"))
        {
            processed = true;
            verifyOpSuperop(isGlobal, channel, hostname);
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2)
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
                throw new ResponseException("That factoid doesn't exist");
            if (isGlobal)
                JZBot.storage.getFactoids().remove(f);
            else
                c.getFactoids().remove(f);
            if (command.equals("delete"))
                JZBot.bot.sendMessage(pm ? sender : channel, "Factoid "
                        + afterCommand + " deleted.");
        }
        if (command.equals("create") || command.equals("replace"))
        {
            processed = true;
            verifyOpSuperop(isGlobal, channel, hostname);
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2)
                throw new ResponseException(
                        "You need to specify the factoid itself");
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
            /*
             * The factoid does not exist. Let's create it.
             */
            Factoid f = JZBot.storage.createFactoid();
            f.setCreator(hostname);
            f.setName(factoidName);
            f.setActive(true);
            f.setValue(factoidContents);
            if (isGlobal)
                JZBot.storage.getFactoids().add(f);
            else
                c.getFactoids().add(f);
            System.out.println("created fact " + factoidName + " "
                    + factoidContents);
            JZBot.bot.sendMessage(pm ? sender : channel, "Factoid "
                    + factoidName
                    + (command.equals("replace") ? "replaced. " : " created."));
        }
        if (command.equals("list"))
        {
            processed = true;
            System.out.println("command is list, " + isGlobal);
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "Start of factoid list");
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
                throw new ResponseException("That factoid doesn't exist");
            JZBot.bot.sendMessage(pm ? sender : channel, f.getValue());
        }
        if (command.equals("info"))
        {
            processed = true;
            throw new ResponseException("Info is not supported yet.");
        }
        if (!processed)
        {
            throw new ResponseException("Invalid factoid command");
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
