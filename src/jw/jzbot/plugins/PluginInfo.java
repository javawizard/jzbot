package jw.jzbot.plugins;

/**
 * @author Alexander Boyd
 * 
 */
public class PluginInfo
{
    /**
     * The name of a plugin. This must not contain spaces. In general, it should only
     * contain letters, numbers, and hyphens. I might change this later.
     */
    public String name;
    /**
     * The name of the language that the plugin is written in.
     */
    public String description;
    public String[] dependencies;
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PluginInfo other = (PluginInfo) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
