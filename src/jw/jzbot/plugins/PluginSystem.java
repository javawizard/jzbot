package jw.jzbot.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.opengroove.common.utils.StringUtils;

public class PluginSystem
{
    public static final File enabledPluginsFile = new File("storage/enabled-plugins.txt");
    
    private static final int MAX_LOG_SIZE = 100;
    
    public static Set<String> enabledPluginNames = new TreeSet<String>();
    
    public static Map<String, PluginLanguage> pluginLanguages =
            new HashMap<String, PluginLanguage>();
    
    public static List<Plugin> loadedPlugins = new ArrayList<Plugin>();
    
    public static Set<String> loadedPluginNames = new HashSet<String>();
    
    private static ArrayList<PluginLogMessage> log = new ArrayList<PluginLogMessage>();
    
    public static Set<PluginInfo> knownPlugins = new TreeSet<PluginInfo>();
    
    private PluginSystem()
    {
        throw new RuntimeException("You're not supposed to create "
            + "instances of PluginSystem.");
    }
    
    public static void start()
    {
        /*
         * First we'll install the Java plugin language.
         */

        /*
         * Then we'll read in the list of plugins that should be enabled.
         */
        if (!enabledPluginsFile.exists())
            StringUtils.writeFile("\n", enabledPluginsFile);
        String[] enabledPluginNamesArray =
                StringUtils.readFile(enabledPluginsFile).split("\\n");
        enabledPluginNames.addAll(Arrays.asList(enabledPluginNamesArray));
        /*
         * We've got the list. Now we'll start activating the plugins.
         */
        activatePlugins();
    }
    
    private static void activatePlugins()
    {
        /*
         * We activate plugins in passes. Once we make a full pass without loading any
         * plugins, we're done. We then check to see if we managed to load all the
         * plugins, and if we didn't, we issue a message indicating why.
         */
        int previousLoadedCount = -1;
        Set<String> languagesAlreadyLoaded = new HashSet<String>();
        while (previousLoadedCount < loadedPlugins.size())
        {
            previousLoadedCount = loadedPlugins.size();
            /*
             * We scan through the available list of plugin languages. For each language
             * that's not in languagesAlreadyLoaded, we query it for the list of plugins
             * available to it. We then add those to the known plugins list. If we find a
             * duplicate, we issue an error message about it.
             */
            for (Map.Entry<String, PluginLanguage> entry : pluginLanguages.entrySet())
            {
                String languageName = entry.getKey();
                PluginLanguage language = entry.getValue();
                if (languagesAlreadyLoaded.contains(languageName))
                    /*
                     * We've already been through this language, so we'll just continue on
                     * with the other languages.
                     */
                    continue;
                languagesAlreadyLoaded.add(languageName);
                /*
                 * We haven't been through this language yet. We'll get the list of
                 * plugins for this language.
                 */
                List<PluginInfo> languagePluginList = getPluginsForLanguage(language);
                /*
                 * Now we add all of the plugins that don't already have a plugin with the
                 * same name.
                 */
                for (PluginInfo languagePlugin : languagePluginList)
                {
                    if (!knownPlugins.contains(languagePlugin))
                        knownPlugins.add(languagePlugin);
                    else
                    {
                        /*
                         * The plugin has the same name as another plugin. We'll issue a
                         * message about it and ignore the plugin.
                         */
                        log(null, true, "There is more than one plugin named \""
                            + languagePlugin.name
                            + "\". Only one such plugin can be installed at "
                            + "the same time. The others will be ignored.");
                    }
                }
            }
            /*
             * Now we'll iterate over the list of known plugins and activate the ones that
             * are supposed to be active but that aren't yet. We'll make sure all
             * dependencies for the plugin are installed.
             */
        }
    }
    
    /**
     * Gets the list of plugins available for this language.
     * 
     * @param language
     * @return
     */
    private static List<PluginInfo> getPluginsForLanguage(PluginLanguage language)
    {
        List<PluginInfo> list = new ArrayList<PluginInfo>();
        for (File folder : getPluginFolderList())
            list.addAll(Arrays.asList(language.listPlugins(new File(folder, language
                    .getName()))));
        return list;
    }
    
    private static File[] getPluginFolderList()
    {
        return new File[] { new File("plugins"), new File("storage/plugins") };
    }
    
    static void log(String plugin, boolean error, String message)
    {
        System.out.println("PLUGIN LOG: " + (plugin != null ? plugin : "<global>") + " "
            + message);
        log.add(new PluginLogMessage(plugin, error, message));
        while (log.size() > MAX_LOG_SIZE)
            log.remove(0);
        /*
         * TODO: make it possible to add listeners for new log events. JZBot should send
         * any errors to the primary channel, but it should buffer them if the primary
         * channel hasn't been joined yet.
         */
    }
    
    public static void shutdown()
    {
        
    }
    
    public static void installPluginLanguage(PluginLanguage language)
    {
        pluginLanguages.put(language.getName(), language);
    }
}
