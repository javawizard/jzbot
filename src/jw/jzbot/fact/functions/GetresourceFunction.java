package jw.jzbot.fact.functions;

import java.io.File;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

import net.sf.opengroove.common.utils.StringUtils;

public class GetresourceFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String name = arguments.resolveString(0);
        if (name.contains("..") || name.contains("\\") || name.contains("/"))
            throw new FactoidException("The resource name \"" + name
                    + "\" contains invalid characters.");
        File file = new File("resources", name);
        if (!file.exists())
            throw new FactoidException("No such resource: " + name);
        if (file.length() > (500 * 1024))
            throw new FactoidException("Resource \"" + name + "\" is too long ("
                    + file.length() + " bytes, max is " + (500 * 1024) + " bytes)");
        String s = StringUtils.readFile(file);
        // TODO: change this into opening a file reader, reading chars from the file, and
        // writing those (probably buffered into a string) to the sink
        sink.write(s);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {getresource|<name>} -- Gets the resource by the specified name. "
                + "Resources are files present in the \"resources\" folder under the bot's main "
                + "folder. If the resource's content is longer than 100KB, an error will occur.";
    }
    
}
