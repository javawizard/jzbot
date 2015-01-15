package jw.jzbot.plugins.packaged.clojure;

import clojure.lang.*;
import jw.jzbot.plugins.Plugin;
import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginInfo;
import jw.jzbot.plugins.PluginLanguage;
import net.sf.opengroove.common.utils.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.stream.Stream;

/**
 * Created by aboyd on 2015-01-10.
 */
public class ClojureLanguageSupport implements PluginLanguage {
    @Override
    public String getName() {
        return "clojure";
    }

    @Override
    public PluginInfo[] listPlugins(File folder) {
        return Stream.of(folder.listFiles())
                .filter((file) -> file.isDirectory() && new File(file, "plugin.json").exists())
                .map((directory) -> {
                    PluginInfo info = new PluginInfo();
                    // TODO: I'm really liking this approach of having each plugin be a directory with a
                    // plugin.json file - consider adopting this approach for all plugins, and then have
                    // plugin.json specify the plugin's language instead of the folder it's located in
                    // indicating the language.
                    JSONObject json = new JSONObject(StringUtils.readFile(new File(directory, "plugin.json")));
                    info.name = json.getString("name");
                    info.description = json.getString("description");
                    info.dependencies = new String[0];
                    if (json.has("dependencies")) {
                        info.dependencies = (String[]) json.getJSONArray("dependencies").myArrayList.toArray(new String[0]);
                    }
                    return info;
                }).toArray((length) -> new PluginInfo[length]);
    }

    @Override
    public void loadPlugin(Plugin plugin, PluginContext context) throws Exception {
        JSONObject json = new JSONObject(StringUtils.readFile(new File(plugin.folder, plugin.info.name + "/plugin.json")));
        IFn require = clojure.java.api.Clojure.var("clojure.core", "require");
        require.invoke(clojure.java.api.Clojure.read(json.getString("require")));
        IFn init = clojure.java.api.Clojure.var(json.getString("init"));
        init.invoke(context);
    }
}
