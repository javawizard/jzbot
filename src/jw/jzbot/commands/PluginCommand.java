package jw.jzbot.commands;

import java.security.SecureRandom;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.Command;
import jw.jzbot.FactScope;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.plugins.Plugin;
import jw.jzbot.plugins.PluginSystem;
import jw.jzbot.storage.PersistentKey;

public class PluginCommand implements Command
{
    
    @Override
    public String getName()
    {
        return "plugin";
    }
    
    @Override
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        synchronized (PluginSystem.class)
        {
            String[] argumentsTokenized1 = arguments.split(" ", 2);
            String command = argumentsTokenized1[0];
            String afterCommand =
                    (argumentsTokenized1.length > 1) ? argumentsTokenized1[1] : "";
            if (command.equals("available"))
            {
                String response =
                        "The following plugins are currently installed. "
                            + "This list does not include plugins whose language "
                            + "support plugin is not currently active. The plugins are: ";
                response += StringUtils.delimited(PluginSystem.knownPluginNames, " ");
                if (response.length() > (source.getProtocolDelimitedLength() * 2))
                    response = JZBot.pastebinNotice(response, null);
                source.sendSpaced(response);
            }
            else if (command.equals("enabled"))
            {
                String response =
                        "The following plugins are currently enabled. "
                            + "Note that not all of the plugins may be "
                            + "active; the bot has to be restarted after "
                            + "enabling a plugin for it to become active, "
                            + "and an error while loading a plugin may "
                            + "prevent it from becoming active. The list is: ";
                response += StringUtils.delimited(PluginSystem.enabledPluginNames, " ");
                if (response.length() > (source.getProtocolDelimitedLength() * 2))
                    response = JZBot.pastebinNotice(response, null);
                source.sendSpaced(response);
            }
            else if (command.equals("active"))
            {
                String response =
                        "The following plugins are currently active. These are "
                            + "the plugins that were successfully loaded the "
                            + "last time the bot started up. They are: ";
                response += StringUtils.delimited(PluginSystem.loadedPluginNames, " ");
                if (response.length() > (source.getProtocolDelimitedLength() * 2))
                    response = JZBot.pastebinNotice(response, null);
                source.sendSpaced(response);
            }
            else if (command.equals("enable"))
            {
                String name = afterCommand;
                if (name.equals(""))
                    throw new ResponseException("You need to specify the name "
                        + "of the plugin to enable, like \"plugin enable "
                        + "some-example-plugin\". This plugin must be in "
                        + "the list of available plugins, so its language "
                        + "support plugin, if any, must be activated first.");
                if (PluginSystem.enabledPluginNames.contains(name))
                    throw new ResponseException("That plugin is already "
                        + "enabled. That doesn't mean it's active; try "
                        + "\"plugin active\" for a list of active plugins. "
                        + "If it's enabled but not active, you might need "
                        + "to restart the bot first, or it may have "
                        + "encountered an error when loading on the last bot restart.");
                if (!PluginSystem.knownPluginNames.contains(name))
                    throw new ResponseException("That plugin is not installed, or "
                        + "its language support plugin is not currently active. "
                        + "You need to install that plugin or activate its "
                        + "language support plugin before you can enabled it.");
                PluginSystem.enabledPluginNames.add(name);
                PluginSystem.saveEnabledPlugins();
                source.sendMessage("The plugin has been successfully enabled. "
                    + "Restart the bot to activate it.");
            }
            else if (command.equals("disable"))
            {
                String name = afterCommand;
                if (name.equals(""))
                    throw new ResponseException("You need to specify the name "
                        + "of the plugin to disable, like \"plugin disable "
                        + "some-example-plugin\".");
                if (!PluginSystem.enabledPluginNames.contains(name))
                    throw new ResponseException("That plugin is not currently "
                        + "enabled. If it's still active, you should try "
                        + "restarting the bot to deactivate it.");
                PluginSystem.enabledPluginNames.remove(name);
                PluginSystem.saveEnabledPlugins();
                source.sendMessage("The plugin has been successfully disabled. "
                    + "Restart the bot to deactivate it.");
            }
            else if (command.equals("info"))
            {
                String name = afterCommand;
                if (name.equals(""))
                    throw new ResponseException("You need to specify the name "
                        + "of the plugin whose information you want to read, "
                        + "like \"plugin info some-example-plugin\".");
                Plugin plugin = PluginSystem.knownPluginMap.get(name);
                if (plugin == null)
                    throw new ResponseException("That plugin is not an "
                        + "available plugin. The plugin might not be "
                        + "installed, or its language support plugin might "
                        + "not currently be active.");
                String response =
                        "Plugin " + plugin.info.name + "; language: " + plugin.language
                            + "; folder: " + plugin.folder.getPath() + "; ";
                if (plugin.info.dependencies.length == 0)
                    response += "No dependencies";
                else
                    response +=
                            "Dependencies: "
                                + StringUtils.delimited(plugin.info.dependencies, " ");
                
                response +=
                        "; Enabled: " + PluginSystem.enabledPluginNames.contains(name)
                            + "; Active: " + PluginSystem.loadedPluginNames.contains(name)
                            + "; Description: " + plugin.info.description;
                if (response.length() > (source.getProtocolDelimitedLength() * 2))
                    response = JZBot.pastebinNotice(response, null);
                source.sendSpaced(response);
            }
            else
            {
                throw new ResponseException("Invalid plugin command. Try 'plugin "
                    + "<available|enabled|active|enable|disable|info>'");
            }
        }
    }
}
