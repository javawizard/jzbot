package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.List;

import jw.jzbot.Command;
import jw.jzbot.ConnectionContext;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
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
                        + "deactivate, edit, list, or current.");
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
            JZBot.instantiateConnectionForProtocol(tokens[0], false);
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
            Server s = server(serverName);
            if (s.isActive())
                throw new ResponseException("That server is already active.");
            s.setActive(true);
            JZBot.notifyConnectionCycleThread();
            source.sendMessage("The server was successfully activated. The bot will "
                + "connect to it within a few seconds.");
        }
        else if (subcommand.equals("deactivate"))
        {
            sender.verifySuperop();
            Server s = server(serverName);
            if (!s.isActive())
                throw new ResponseException("That server is not active.");
            s.setActive(false);
            JZBot.notifyConnectionCycleThread();
            source.sendMessage("The server was successfully deactivated. The bot will "
                + "disconnect from it within a few seconds.");
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
                + "connection attempt (use \"server error\" to read the error message)"
                + ", 0: no other flags.");
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
                if (flags.equals(""))
                    flags = "0";
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
        else
        {
            throw new ResponseException(
                    "Invalid command. Try running the \"server\" command "
                        + "without arguments to see a list of all valid commands.");
        }
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
}
