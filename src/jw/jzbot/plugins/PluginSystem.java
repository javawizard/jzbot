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

import jw.jzbot.plugins.java.JavaPluginLanguage;

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
    
    public static Set<Plugin> knownPlugins = new TreeSet<Plugin>();
    
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
        installPluginLanguage(new JavaPluginLanguage());
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
        int passNumber = 1;
        while (previousLoadedCount < loadedPlugins.size())
        {
            System.out.println("PLUGIN LOADER: Pass " + passNumber++);
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
                List<Plugin> languagePluginList = getPluginsForLanguage(language);
                /*
                 * Now we add all of the plugins that don't already have a plugin with the
                 * same name.
                 */
                for (Plugin languagePlugin : languagePluginList)
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
                            + languagePlugin.info.name
                            + "\". Only one such plugin can be installed at "
                            + "the same time. The others will be ignored.");
                    }
                }
            }
            /*
             * Now we'll iterate over the list of known plugins and activate the ones that
             * are supposed to be active but that aren't yet. We'll make sure all
             * dependencies for the plugin are activated.
             */
            pluginLoadLoop: for (Plugin plugin : knownPlugins)
            {
                if (!enabledPluginNames.contains(plugin.info.name))
                    /*
                     * The plugin isn't enabled.
                     */
                    continue;
                if (loadedPlugins.contains(plugin))
                    /*
                     * The plugin has already been activated.
                     */
                    continue;
                for (String dependency : plugin.info.dependencies)
                {
                    if (!loadedPluginNames.contains(dependency))
                    {
                        /*
                         * A dependency of the plugin is not yet loaded.
                         */
                        System.out.println("Plugin " + plugin.info.name
                            + " is missing dependency " + dependency
                            + "; saving for next pass.");
                        continue pluginLoadLoop;
                    }
                }
                /*
                 * We're good to load the plugin.
                 */
                try
                {
                    pluginLanguages.get(plugin.language).loadPlugin(plugin,
                            new PluginContext(plugin));
                    loadedPlugins.add(plugin);
                    loadedPluginNames.add(plugin.info.name);
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                    log(null, true, "Exception while loading plugin " + plugin.info.name
                        + ": " + e.getClass().getName() + ": " + e.getMessage());
                }
            }
            System.out.println("PLUGIN LOADER: Loaded "
                + (loadedPlugins.size() - previousLoadedCount) + " plugins this pass");
        }
        /*
         * That's it! Now we go through the list of enabled plugins and make sure all of
         * the got enabled. If they didn't, we issue an error message.
         */
        TreeSet<String> failedPlugins = new TreeSet<String>();
        failedPlugins.addAll(enabledPluginNames);
        failedPlugins.removeAll(loadedPluginNames);
        for (String failedPlugin : failedPlugins)
            log(null, true, "Plugin " + failedPlugin + " failed to load. This is "
                + "usually because of a dependency error. More information "
                + "has been printed to stdout.");
        /*
         * We're done!
         */
        log(null, false, "" + knownPlugins.size() + " plugins available, "
            + enabledPluginNames.size() + " plugins enabled, " + loadedPlugins.size()
            + " plugins activated.");
    }
    
    /**
     * Gets the list of plugins available for this language.
     * 
     * @param language
     * @return
     */
    private static List<Plugin> getPluginsForLanguage(PluginLanguage language)
    {
        List<Plugin> list = new ArrayList<Plugin>();
        for (File folder : getPluginFolderList())
        {
            if (!folder.exists())
                folder.mkdirs();
            File languageFolder = new File(folder, language.getName());
            if (languageFolder.exists())
            {
                PluginInfo[] infos = language.listPlugins(languageFolder);
                for (PluginInfo info : infos)
                {
                    Plugin plugin = new Plugin();
                    plugin.info = info;
                    plugin.folder = folder;
                    plugin.language = language.getName();
                    list.add(plugin);
                }
            }
        }
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
        // TODO: In the future, somehow notify plugins that we're shutting down. Or allow
        // plugins to register hooks that will be invoked when this is called.
    }
    
    public static void installPluginLanguage(PluginLanguage language)
    {
        pluginLanguages.put(language.getName(), language);
    }
}
