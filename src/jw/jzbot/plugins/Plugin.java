package jw.jzbot.plugins;

import java.io.File;

public class Plugin implements Comparable<Plugin>
{
    public PluginInfo info;
    public File folder;
    public String language;
    
    public int hashCode()
    {
        return info.hashCode();
    }
    
    public boolean equals(Object other)
    {
        if (!(other instanceof Plugin))
            return false;
        return ((Plugin) other).info.equals(info);
    }

    @Override
    public int compareTo(Plugin o)
    {
        return info.name.compareTo(o.info.name);
    }
}
