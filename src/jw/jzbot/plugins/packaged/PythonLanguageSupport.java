package jw.jzbot.plugins.packaged;

import java.io.File;

import jw.jzbot.plugins.Plugin;
import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginInfo;
import jw.jzbot.plugins.PluginLanguage;

public class PythonLanguageSupport implements PluginLanguage
{
    
    @Override
    public String getName()
    {
        return "python";
    }
    
    @Override
    public PluginInfo[] listPlugins(File folder)
    {
        return new PluginInfo[0];
    }
    
    @Override
    public void loadPlugin(Plugin plugin, PluginContext context) throws Exception
    {
        throw new RuntimeException("This still needs to be implemented. "
            + "It should use Jython to start everything up.");
    }
    
}
