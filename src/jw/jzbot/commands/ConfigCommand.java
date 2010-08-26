package jw.jzbot.commands;

import java.nio.charset.Charset;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ScopeLevel;
import jw.jzbot.ServerUser;
import jw.jzbot.configuration.Configuration;
import jw.jzbot.configuration.Configuration.VarType;
import jw.jzbot.utils.SpacedParser;
import jw.jzbot.utils.Utils;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

import net.sf.opengroove.common.utils.StringUtils;

public class ConfigCommand implements Command
{
    
    public String getName()
    {
        return "config";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        SpacedParser parser = new SpacedParser(arguments);
        if (parser.empty())
            respondWithNoScope(server, channel);
        String command = null;
        ScopeLevel scopeLevel = null;
        if (Utils.contains(ScopeLevel.class, parser.observe()))
        {
            scopeLevel = ScopeLevel.valueOf(parser.next());
        }
        else
        {
            command = parser.next();
            if (parser.empty())
                respondWithNoScope(server, channel);
            scopeLevel = ScopeLevel.valueOf(parser.next());
        }
        String scope = scopeLevel.createScope(server, channel);
        /*
         * We've got the command and the scope. The command can be null, which means that
         * we're supposed to read the variable or set its value. Now we'll go read input
         * until we've run out of valid folders.
         */
        String folderPath = "";
        while (parser.more() && Configuration.exists(scope, parser.observe())
            && Configuration.getType(scope, parser.observe()) == VarType.folder)
        {
            folderPath += "/" + parser.next();
        }
        if (folderPath.length() > 0)
            folderPath = folderPath.substring(1);
        /*
         * We've pulled all of the folders. Now we check to see if there's another token.
         * If there is, we use that as the variable to use, and we remove it from the
         * parser. If there isn't, we use the folder itself as the variable to use.
         */
        String varPath = folderPath;
        if (parser.more())
            varPath += "/" + parser.next();
        if (varPath.startsWith("/"))
            varPath = varPath.substring(1);
        String input = parser.more() ? parser.remaining() : null;
        /*
         * Now we process the actual command.
         */
        if (command.equals("info"))
        {
            source.sendSpaced(getTypeInfo(scope, varPath) + " "
                + Configuration.getDescription(scope, varPath));
        }
        else if (command == null)
        {
            throw new ResponseException("This still needs to be written.");
        }
        else
        {
            throw new ResponseException("That command (" + command + ") doesn't exist.");
        }
    }
    
    private String getTypeInfo(String scope, String path)
    {
        VarType type = Configuration.getType(scope, path);
        if (type == null)
            return "(scope)";
        return "(" + type.name() + ")";
    }
    
    private void respondWithNoScope(String server, String channel)
    {
        throw new ResponseException("You need to specify a "
            + "scope level. At your current scope, allowed scope levels are: "
            + ScopeLevel.validLevelsSpacedString(server, channel)
            + ". For more help with the config command, use \"help config\".");
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        return true;
    }
}
