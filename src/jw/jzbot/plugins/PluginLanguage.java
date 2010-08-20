package jw.jzbot.plugins;

/**
 * Represents a language that a plugin can be written in. Almost all of the plugin loading
 * process is delegated to implementations of this interface. The implementations then
 * handle the actual loading in a manner specific to the language in which the plugin is
 * written.
 * 
 * @author Alexander Boyd
 * 
 */
public interface PluginLanguage
{
    /**
     * Returns the name of this language. The name is used as the name of the folder that
     * plugins written in this language will be placed in. This should generally be all
     * lowercase. For example, this could be "java" or "python".
     * 
     * @return
     */
    public String getName();
    
}
