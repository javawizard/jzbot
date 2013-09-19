package jw.jzbot.plugins.packaged.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.python.core.Options;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import jw.jzbot.plugins.Plugin;
import jw.jzbot.plugins.PluginContext;
import jw.jzbot.plugins.PluginInfo;
import jw.jzbot.plugins.PluginLanguage;
import jw.jzbot.utils.RegexFileFilter;

public class PythonLanguageSupport implements PluginLanguage
{
    public static PythonInterpreter interpreter;
    
    public static PyDictionary namespace;
    
    public PythonLanguageSupport()
    {
        try
        {
            /*
             * It's extremely important that we use the default system state here instead
             * of creating our own like we used to. In particular, using a custom state
             * will cause the python_console plugin not to work (the interpreter it
             * provides over a socket will function as if it were running under its own
             * independent interpreter).
             */
            // Options.showJavaExceptions = true;
            if (interpreter != null)
                throw new RuntimeException("Only one instance of "
                    + "PythonLanguageSupport can be constructed.");
            namespace = new PyDictionary();
            interpreter = new PythonInterpreter(namespace);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String getName()
    {
        return "python";
    }
    
    @Override
    public PluginInfo[] listPlugins(File folder)
    {
        File[] files = folder.listFiles(new RegexFileFilter("[a-z].*"));
        ArrayList<PluginInfo> list = new ArrayList<PluginInfo>();
        for (File file : files)
        {
            if (!file.getName().matches("[a-z].*"))
                continue;
            try
            {
                if (file.isFile())
                    if (!file.getName().endsWith(".py"))
                        continue;
                File descFile;
                if (file.isDirectory())
                    descFile = new File(file, "__init__.py");
                else
                    descFile = file;
                BufferedReader reader = new BufferedReader(new FileReader(descFile));
                PluginInfo info = new PluginInfo();
                info.name =
                        file.isDirectory() ? file.getName() : file.getName().substring(0,
                                file.getName().length() - ".py".length());
                String line;
                while ((line = reader.readLine()) != null
                    && !line.trim().equals("\"\"\" jzbot"))
                    ;
                info.description = "";
                info.dependencies = new String[0];
                if (line != null)// Information block is present
                {
                    while ((line = reader.readLine()) != null
                        && !line.trim().equals("\"\"\""))
                    {
                        if (line.trim().startsWith("dependencies:"))
                            info.dependencies =
                                    line.trim().substring("dependencies:".length()).trim()
                                            .split(" ");
                        else
                            info.description += " " + line.trim();
                    }
                    info.description = info.description.trim();
                }
                reader.close();
                list.add(info);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return list.toArray(new PluginInfo[0]);
    }
    
    @Override
    public void loadPlugin(Plugin plugin, PluginContext context) throws Exception
    {
        String name = plugin.info.name;
        String folderPath = plugin.folder.getCanonicalPath();
        if (!Py.defaultSystemState.path.contains(new PyString(folderPath)))
            Py.defaultSystemState.path.add(new PyString(folderPath));
        interpreter.set("_context_" + name, context);
        interpreter.exec("import " + plugin.info.name);
        interpreter.exec(plugin.info.name + ".init(_context_" + name + ")");
    }
}
