package jw.jzbot.plugins.packaged;

import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginSystem;
import jw.jzbot.plugins.java.JavaPlugin;

public class PythonLanguagePlugin implements JavaPlugin
{
    
    @Override
    public void init(PluginContext context)
    {
        PluginSystem.installPluginLanguage(new PythonLanguageSupport());
    }
    
}
