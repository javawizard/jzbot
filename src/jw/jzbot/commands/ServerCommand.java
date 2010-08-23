package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.Command;
import jw.jzbot.ConnectionContext;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.protocols.ProtocolManager;
import jw.jzbot.storage.Server;
import jw.jzbot.utils.JZUtils;

public class ServerCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "server";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        // TODO: make a config var that, when set to a certain value, allows the list of
        // servers to be read by non-superops, too.
        if (arguments.equals(""))
        {
            source
                    .sendMessage("You need to specify one of add, delete, details, activate, "
                        + "deactivate, edit, list, priority, or current.");
            return;
        }
        String[] tokens = arguments.split(" ", 2);
        String subcommand = tokens[0];
        arguments = (tokens.length > 1 ? tokens[1] : "");
        tokens = arguments.split(" ", 2);
        String serverName = tokens[0];
        arguments = (tokens.length > 1 ? tokens[1] : "");
        tokens = arguments.split(" ");
        if (subcommand.equals("add"))
        {
            if (serverName.equals(""))
                throw new ResponseException("Syntax: \"server add <name> <protocol> "
                    + "<hostname> <port> "
                    + "<nick> <password>\" <password> is optional. <protocol> "
                    + "is one of irc, bzflag, TODO: complete this list");
            sender.verifySuperop();
            verifyOkChars(serverName);
            if (JZBot.storage.getServer(serverName) != null)
                throw new ResponseException("A server with that name already exists.");
            if (!ProtocolManager.hasProtocol(tokens[0]))
                throw new ResponseException("The protocol \"" + tokens[0]
                    + "\" is not a valid protocol. Valid protocols " + "are: "
                    + StringUtils.delimited(ProtocolManager.getProtocolNames(), " "));
            Server newServer = JZBot.storage.createServer();
            newServer.setActive(true);
            newServer.setName(serverName);
            newServer.setProtocol(tokens[0]);
            newServer.setServer(tokens[1]);
            newServer.setPort(Integer.parseInt(tokens[2]));
            newServer.setNick(tokens[3]);
            if (tokens.length > 4)
                newServer.setPassword(tokens[4]);
            JZBot.storage.getServers().add(newServer);
            JZBot.notifyConnectionCycleThread();
            source.sendMessage("That server has been added and activated. The bot "
                + "will connect to it within a few seconds.");
        }
        else if (subcommand.equals("delete"))
        {
            sender.verifySuperop();
            Server s = server(serverName);
            JZBot.storage.getServers().remove(s);
            JZBot.notifyConnectionCycleThread();
            source.sendMessage("The server and its factoids and settings have been "
                + "successfully deleted.");
        }
        else if (subcommand.equals("details"))
        {
            Server s = JZBot.storage.getServer(serverName);
            if (s == null)
                throw new ResponseException(
                        "There isn't such a server by that name. Try \"server list\" "
                            + "to get a list of servers, and then do "
                            + "\"server details <name>\".");
            String passwordString = s.getPassword();
            if (passwordString == null)
                passwordString = "";
            source.sendMessage("protocol:" + s.getProtocol() + "  host:" + s.getServer()
                + "  port:" + s.getPort() + "  nick:" + s.getNick() + "  password:"
                + passwordString.replaceAll(".", "*"));
        }
        else if (subcommand.equals("activate"))
        {
            sender.verifySuperop();
            String[] serverNames = buildIntoMultipleServers(serverName, arguments);
            boolean serversWereActivated = false;
            boolean multipleServersWereActivated = false;
            for (String name : serverNames)
            {
                Server s = server(name);
                if (s.isActive())
                {
                    source.sendSpaced("That server (" + name + ") is already active.");
                    continue;
                }
                s.setActive(true);
                if (serversWereActivated)
                    multipleServersWereActivated = true;
                else
                    serversWereActivated = true;
            }
            if (serversWereActivated)
            {
                if (multipleServersWereActivated)
                    source.sendMessage("Those servers were successfully "
                        + "activated. The bot will connect to them within a few seconds.");
                else
                    source.sendMessage("The server was successfully "
                        + "activated. The bot will connect to it within a few seconds.");
            }
            JZBot.notifyConnectionCycleThread();
        }
        else if (subcommand.equals("deactivate"))
        {
            sender.verifySuperop();
            String[] serverNames = buildIntoMultipleServers(serverName, arguments);
            boolean serversWereDeactivated = false;
            boolean multipleServersWereDeactivated = false;
            for (String name : serverNames)
            {
                Server s = server(name);
                if (!s.isActive())
                {
                    source
                            .sendSpaced("That server (" + name
                                + ") is not currently active.");
                    continue;
                }
                s.setActive(false);
                if (serversWereDeactivated)
                    multipleServersWereDeactivated = true;
                else
                    serversWereDeactivated = true;
            }
            if (serversWereDeactivated)
            {
                if (multipleServersWereDeactivated)
                    source.sendMessage("Those servers were successfully "
                        + "deactivated. The bot will disconnect "
                        + "from them within a few seconds.");
                else
                    source.sendMessage("The server was successfully "
                        + "deactivated. The bot will disconnect "
                        + "from it within a few seconds.");
            }
            JZBot.notifyConnectionCycleThread();
        }
        else if (subcommand.equals("edit"))
        {
            sender.verifySuperop();
            Server s = JZBot.storage.getServer(serverName);
            if (s == null)
                throw new ResponseException(
                        "There isn't such a server by that name. Try \"server list\" "
                            + "to get a list of servers, and then do "
                            + "\"server edit <name>\".");
            ConnectionContext realCon = JZBot.getRealConnection(s.getName());
            // if (s.isActive() || (realCon != null &&
            // realCon.getConnection().isConnected()))
            // throw new ResponseException(
            // "You can't edit servers that are connected or activated. "
            // + "So, when running \"server list\", you can only "
            // + "edit servers that do not have flags 1 and 3.");
            if (tokens[0].equals(""))
                throw new ResponseException("You need to specify the name of the property "
                    + "to edit, like \"server edit " + serverName
                    + " <propname> <propvalue>\". Allowed names are "
                    + "protocol, host, port, nick, and password. Server "
                    + "name-changing will be supported in the future.");
            String key = tokens[0];
            if (tokens.length < 2)
                throw new ResponseException("You need to specify the new value "
                    + "for this property, " + "like \"server edit " + serverName + " "
                    + key + " <newvalue>\".");
            String value = tokens[1];
            if (key.equals("protocol"))
                s.setProtocol(value);
            else if (key.equals("host"))
                s.setServer(value);
            else if (key.equals("port"))
                s.setPort(Integer.parseInt(value));
            else if (key.equals("nick"))
                s.setNick(value);
            // In the future, the bot should switch the server's nick, if the server
            // supports nick switching.
            else if (key.equals("password"))
                s.setPassword(value);
            else
                throw new ResponseException("That's not a valid property name. "
                    + "Try \"server edit " + serverName
                    + "\" for a list of valid property names.");
            source.sendMessage("The server was successfully updated. If "
                + "the server is connected, deactivate and then reactivate "
                + "it as soon as possible.");
        }
        else if (subcommand.equals("list"))
        {
            source.sendSpaced("Here's the list. Servers in this list are in the format "
                + "<flags>:<name>, where <flags> are some flags and <name> is the "
                + "name of the server. Flags are 1: active, 2: has a connection "
                + "object, 3: currently connected, 4: error occurred during last "
                + "connection attempt (use \"server error\" to read the error message).");
            List<String> items = new ArrayList<String>();
            for (Server s : JZBot.storage.getServers().isolate())
            {
                String sName = s.getName();
                String flags = "";
                if (s.isActive())
                    flags += "1";
                ConnectionContext c = JZBot.getRealConnection(sName);
                if (c != null)
                {
                    flags += "2";
                    if (c.getConnection().isConnected())
                        flags += "3";
                }
                if (JZBot.connectionLastErrorMap.get(sName) != null)
                    flags += "4";
                // if (flags.equals(""))
                // flags = "0";
                items.add(flags + ":" + sName);
            }
            JZUtils.ircSendDelimited(items.toArray(new String[0]), "  ", source);
        }
        else if (subcommand.equals("error"))
        {
            Server s = JZBot.storage.getServer(serverName);
            if (s == null)
                throw new ResponseException("There isn't a server by that name. Use "
                    + "\"server error <name>\" to get the error "
                    + "message for a server that failed to connect.");
            Throwable t = JZBot.connectionLastErrorMap.get(serverName);
            if (t == null)
                source.sendMessage("This server did not encounter an error "
                    + "since the last time it tried to connect.");
            else
                source.sendMessage("Details of the last connection error: "
                    + JZBot.pastebinStack(t));
        }
        else if (subcommand.equals("current"))
        {
            source.sendMessage("Your current server is " + server + ".");
        }
        else if (subcommand.equals("priority"))
        {
            if (serverName.equals(""))
                throw new ResponseException("Use \"server priority <name>\" to get "
                    + "the current priority of a server or \"server priority "
                    + "<name> <priority>\" to set a new priority for the specified "
                    + "server. Servers with higher priority will be connected first "
                    + "when the bot starts up or restarts.");
            Server s = JZBot.storage.getServer(serverName);
            if (s == null)
                throw new ResponseException("There isn't a server with the name \""
                    + serverName + "\".");
            if (arguments.equals(""))
            {
                source.sendMessage("That server's current priority is " + s.getPriority()
                    + ".");
                return;
            }
            arguments = translateMemeNumbers(arguments);
            try
            {
                s.setPriority(Integer.parseInt(arguments));
                source.sendMessage("That server's priority has been successfully set to "
                    + s.getPriority() + ".");
                return;
            }
            catch (NumberFormatException e)
            {
                throw new ResponseException("The new priority you just tried to set, \""
                    + arguments + "\", is not a number. The new priority for a server "
                    + "must be a whole number between " + Integer.MAX_VALUE + " and "
                    + Integer.MIN_VALUE + ", inclusive. The server's priority has "
                    + "been left at " + s.getPriority() + ".");
            }
        }
        else
        {
            throw new ResponseException(
                    "Invalid command. Try running the \"server\" command "
                        + "without arguments to see a list of all valid commands.");
        }
    }
    
    /**
     * This implements one of the more cool easter eggs in JZBot. It translates various
     * non-number strings to corresponding number strings, all legal 32-bit signed integer
     * values. For example, "Linus Torvalds" gets translated to Integer.MAX_VALUE, since
     * Linus pwns everything, and "over 9000" gets translated to 9001.
     * 
     * @param arguments
     */
    private String translateMemeNumbers(String value)
    {
        if (value.equalsIgnoreCase("Linus Torvalds"))
            return "" + Integer.MAX_VALUE;
        if (value.equalsIgnoreCase("over 9000"))
            return "9001";
        if (value.equalsIgnoreCase("life, the universe, and everything")
            || value.equalsIgnoreCase("life, the universe and everything"))
            return "42";
        if (value.equalsIgnoreCase("microsoft"))
            throw new ResponseException("Sorry, but priorities have to be above "
                + Integer.MIN_VALUE + ", which precludes using any priority that "
                + "could remotely be conjectured to represent Microsoft.");
        return value;
    }
    
    private String[] buildIntoMultipleServers(String serverName, String arguments)
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(serverName);
        String[] argumentSplit = arguments.split(" ");
        if (argumentSplit.length > 1 || !argumentSplit[0].equals(""))
            list.addAll(Arrays.asList(argumentSplit));
        return list.toArray(new String[0]);
    }
    
    private void verifyOkChars(String serverName)
    {
        if (!serverName.matches("^[a-zA-Z0-9\\-]+$"))
            throw new ResponseException(
                    "Server names can only contain letters, number, and hyphens.");
    }
    
    private static Server server(String name)
    {
        if (name == null || "".equals(name))
            throw new ResponseException("This command requires a server to be specified, "
                + "but you didn't specify one.");
        Server server = JZBot.storage.getServer(name);
        if (server == null)
            throw new ResponseException("That server (\"" + name
                + "\") doesn't exist. This command requires a server "
                + "that already exists.");
        return server;
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
