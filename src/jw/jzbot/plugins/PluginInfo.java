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
}
