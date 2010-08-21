package jw.jzbot.plugins;

public class PluginContext
{
    public Plugin plugin;
    
    public void log(String message)
    {
        log(false, message);
    }
    
    public void log(boolean error, String message)
    {
        PluginSystem.log(plugin.info.name, error, message);
    }
    
    public void error(String message)
    {
        log(true, message);
    }
}
