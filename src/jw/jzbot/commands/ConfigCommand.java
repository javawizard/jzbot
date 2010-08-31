package jw.jzbot.commands;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

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
        if (command == null)
        {
            VarType varType = Configuration.getType(scope, varPath);
            if (varType == null || varType == VarType.folder)
            {
                /*
                 * This is either a folder or the scope folder. Either way we'll list the
                 * contents of the folder and send them back.
                 */
                listFolderContents(source, scope, varPath);
            }
            else if (input == null)
            {
                /*
                 * We're supposed to read the variable
                 */
                VarType type = Configuration.getType(scope, varPath);
                String result = "(" + type.name() + ")";
                if (!Configuration.isSet(scope, varPath))
                {
                    if (Configuration.hasDefault(scope, varPath))
                        result += " default:";
                    else
                        result += " unset";
                }
                String value = getDisplayValue(scope, varPath);
                if (value != null)
                    result += " " + value;
                source.sendSpaced(result);
            }
            else
            {
                /*
                 * We're supposed to set the variable
                 */
                sender.verifySuperop();
                String oldDisplayValue = getDisplayValue(scope, varPath);
                Configuration.setText(scope, varPath, input);
                source.sendSpaced("Successfully set to " + getDisplayValue(scope, varPath)
                    + "."
                    + (oldDisplayValue != null ? " Old value: " + oldDisplayValue : ""));
            }
        }
        else if (command.equals("info"))
        {
            source.sendSpaced(getTypeInfo(scope, varPath) + " "
                + Configuration.getDescription(scope, varPath));
        }
        else if (command.equals("unset"))
        {
            sender.verifySuperop();
            String oldDisplayValue = getDisplayValue(scope, varPath);
            Configuration.setText(scope, varPath, null);
            source.sendSpaced("Successfully unset."
                + (oldDisplayValue != null ? " Old value: " + oldDisplayValue : ""));
        }
        else
        {
            throw new ResponseException("That command (" + command + ") doesn't exist.");
        }
    }
    
    private void listFolderContents(Messenger source, String scope, String varPath)
    {
        String[] names = Configuration.getChildNames(scope, varPath);
        Arrays.sort(names);
        ArrayList<String> results = new ArrayList<String>();
        for (String name : names)
        {
            String childPath = Configuration.child(varPath, name);
            VarType type = Configuration.getType(scope, childPath);
            String prefix = "";
            if (type == VarType.folder)
                prefix = "->";
            else if (Configuration.isSet(scope, childPath))
                prefix += "+";
            else if (Configuration.hasDefault(scope, childPath))
                prefix += "=";
            results.add(prefix + name);
        }
        if (results.size() == 0)
            source.sendSpaced("(no configuration variables available)");
        else
            source.sendSpaced(StringUtils.delimited(results, " "));
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
    
    public String getDisplayValue(String scope, String varPath)
    {
        String result = Configuration.getTextNormal(scope, varPath);
        if (result == null)
            return null;
        VarType type = Configuration.getType(scope, varPath);
        if (type == VarType.text)
            result = "\"" + result + "\"";
        return result;
    }
}
