package org.opengroove.jzbot.fact.functions;

import java.io.File;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class GetresourceFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String name = arguments.get(0);
        if (name.contains("..") || name.contains("\\") || name.contains("/"))
            throw new FactoidException("The resource name \"" + name
                    + "\" contains invalid characters.");
        File file = new File("resources", name);
        if (!file.exists())
            throw new FactoidException("No such resource: " + name);
        if (file.length() > (100 * 1024))
            throw new FactoidException("Resource \"" + name
                    + "\" is too long (" + file.length() + " bytes, max is "
                    + (100 * 1024) + " bytes)");
        String s = StringUtils.readFile(file);
        return s;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{getresource||<name>}} -- Gets the resource by the specified name. "
                + "Resources are files present in the \"resources\" folder under the bot's main "
                + "folder. If the resource's content is longer than 100KB, an error will occur.";
    }
    
}
