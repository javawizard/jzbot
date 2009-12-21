package jw.jzbot.commands;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.storage.Server;

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
        sender.verifySuperop();
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
            verifyOkChars(serverName);
            if (JZBot.storage.getServer(serverName) != null)
                throw new ResponseException("A server with that name already exists.");
            JZBot.instantiateConnectionForProtocol(tokens[0], false);
            Server newServer = JZBot.storage.createServer();
            newServer.setActive(false);
            newServer.setName(serverName);
            newServer.setProtocol(tokens[0]);
            newServer.setServer(tokens[1]);
            newServer.setPort(Integer.parseInt(tokens[2]));
            newServer.setNick(tokens[3]);
            if (tokens.length > 4)
                newServer.setPassword(tokens[4]);
            JZBot.storage.getServers().add(newServer);
            source
                    .sendMessage("That server has successfully been added. Use \"server activate "
                            + newServer + "\" to get the bot to actually connect to it.");
        }
        else if (subcommand.equals("delete"))
        {
            Server s = server(serverName);
            JZBot.storage.getServers().remove(s);
            JZBot.notifyConnectionCycleThread();
            source.sendMessage("The server and its factoids and settings have been "
                    + "successfully deleted.");
        }
        else if (subcommand.equals("details"))
        {
            
        }
        else if (subcommand.equals("activate"))
        {
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
            
        }
        else if (subcommand.equals("list"))
        {
            
        }
        else if (subcommand.equals("current"))
        {
            
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
