package jw.jzbot.plugins.packaged.python;

import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginSystem;
import jw.jzbot.plugins.java.JavaPlugin;

public class PythonLanguagePlugin implements JavaPlugin
{
    
    @Override
    public void init(PluginContext context)
    {
        try
        {
            PluginSystem.installPluginLanguage(new PythonLanguageSupport());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
