package jw.jzbot.plugins.packaged;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
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
    
    public static PySystemState sys;
    
    public PythonLanguageSupport()
    {
        if (interpreter != null)
            throw new RuntimeException("Only one instance of "
                + "PythonLanguageSupport can be constructed.");
        sys = new PySystemState();
        interpreter = new PythonInterpreter(sys);
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
                if (line == null)// No information block
                {
                    info.dependencies = new String[0];
                }
                else
                {
                    while ((line = reader.readLine()) != null
                        && !line.trim().equals("\"\"\""))
                    {
                        if (line.trim().startsWith("dependencies:"))
                            info.dependencies =
                                    line.trim().substring("dependencies:".length()).trim()
                                            .split(" ");
                        else
                            info.description += line.trim();
                    }
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
        if (!sys.path.contains(new PyString(folderPath)))
            sys.path.add(new PyString(folderPath));
        interpreter.set("_context_" + name, context);
        interpreter.exec("import " + plugin.info.name);
        interpreter.exec(plugin.info.name + ".init(_context_" + name + ")");
    }
}
