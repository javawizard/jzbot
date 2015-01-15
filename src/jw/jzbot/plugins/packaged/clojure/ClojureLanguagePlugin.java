package jw.jzbot.plugins.packaged.clojure;

import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginSystem;
import jw.jzbot.plugins.java.JavaPlugin;

/**
 * Created by aboyd on 2015-01-10.
 */
public class ClojureLanguagePlugin implements JavaPlugin {
    @Override
    public void init(PluginContext context)
    {
        try
        {
            PluginSystem.installPluginLanguage(new ClojureLanguageSupport());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
