package jw.jzbot.fact.functions.scripting;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class ScriptFunction extends Function {
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        String name = arguments.resolveString(0);
        String statusVar = arguments.resolveString(1);
        name = name.replace("/", "").replace("\\", "").replace(":", "");
        if (new File("storage/scripts", name).exists())
            name = "./storage/scripts/" + name;
        else if (new File("scripts", name).exists())
            name = "./scripts/" + name;
        else
            throw new RuntimeException("The specified script (\"" + name
                    + "\") does not exist.");
        String[] args = arguments.subList(1).evalToArray();
        args[0] = name;
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);
        Map<String, String> env = builder.environment();
        if (context.getChannelName() != null)
            env.put("JZBOT_CHANNEL", context.getChannelName());
        if (context.getServerName() != null)
            env.put("JZBOT_SERVER", context.getServerName());
        Process p;
        try {
            p = builder.start();
            int i;
            InputStream in = p.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            in.close();
            int code = p.waitFor();
            if (!statusVar.equals(""))
                context.getChainVars().put(statusVar, "" + code);
            sink.write(new String(out.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String getHelp(String topic) {
        return "Syntax: {script|<name>|<status-var>|<arg1>|<arg2>|...} -- Runs "
                + "the specified script (which must be located either in the scripts "
                + "directory or the storage/scripts directory). The script can be any "
                + "natively-executable application; shebang files are allowed, too. If "
                + "<status-var> is not empty, it names a variable into which the exit code "
                + "of the script will be stored. <arg1>, <arg2>, etc are given to the script "
                + "as its arguments. This function returns the script's stdout and stderr, combined.";
    }
    
}
