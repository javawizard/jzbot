package jw.jzbot.plugins.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.plugins.Plugin;
import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginInfo;
import jw.jzbot.plugins.PluginLanguage;
import jw.jzbot.utils.RegexFileFilter;

/**
 * This supports only something.props files right now. It reads description, class, and
 * dependencies from that. Dependencies should be a space-separated list of the
 * dependencies, or it can be omitted if there are none.
 * 
 * The plugin's name, if the file were named something.props, would be something. When
 * it's time to load it, the value of the class property is used as the name of a class to
 * locate on the classpath. This class must implement {@link JavaPlugin}. Its init method
 * will then be called.
 * 
 * @author Alexander Boyd
 * 
 */
public class JavaPluginLanguage implements PluginLanguage
{
    
    @Override
    public String getName()
    {
        return "java";
    }
    
    @Override
    public PluginInfo[] listPlugins(File folder)
    {
        List<PluginInfo> list = new ArrayList<PluginInfo>();
        for (File file : folder.listFiles(new RegexFileFilter(".*\\.props")))
        {
            PluginInfo info = new PluginInfo();
            String filename = file.getName();
            info.name = filename.substring(0, filename.length() - ".props".length());
            Properties props = StringUtils.loadProps(file);
            info.description = props.getProperty("description", "");
            if (props.containsKey("dependencies"))
                info.dependencies = props.getProperty("dependencies").split(" ");
            else
                info.dependencies = new String[0];
            list.add(info);
        }
        return list.toArray(new PluginInfo[0]);
    }
    
    @Override
    public void loadPlugin(Plugin plugin, PluginContext context) throws Exception
    {
        Properties props =
                StringUtils.loadProps(new File(plugin.folder, plugin.info.name + ".props"));
        String className = props.getProperty("class");
        Class<? extends JavaPlugin> c =
                (Class<? extends JavaPlugin>) Class.forName(className);
        JavaPlugin pluginImpl = c.getConstructor().newInstance();
        pluginImpl.init(context);
    }
    
}
