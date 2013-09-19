package jw.jzbot.plugins;

import java.util.Date;

public class PluginLogMessage
{
    public String plugin;
    public Date date;
    public boolean error;
    public String message;
    
    public PluginLogMessage()
    {
        super();
    }
    
    public PluginLogMessage(String plugin, boolean error, String message)
    {
        super();
        this.date = new Date();
        this.message = message;
        this.plugin = plugin;
        this.error = error;
    }
    
}
