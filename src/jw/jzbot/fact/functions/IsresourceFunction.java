package jw.jzbot.fact.functions;

import java.io.File;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class IsresourceFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String name = arguments.resolveString(0);
        if (name.contains("..") || name.contains("\\") || name.contains("/"))
            throw new FactoidException("The resource name \"" + name
                    + "\" contains invalid characters.");
        File file = new File("resources", name);
        sink.write(file.exists() ? '1' : '0');
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {isresource|<name>} -- Returns 1 if <name> denotes a valid "
                + "resource (IE {getresource|<name>} could be called without an error "
                + "occuring), or 0 if <name> does not denote a valid resource.";
    }
    
}
