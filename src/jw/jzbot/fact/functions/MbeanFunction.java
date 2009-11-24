package jw.jzbot.fact.functions;

import java.lang.management.ManagementFactory;

import javax.management.ObjectName;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class MbeanFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        
        try
        {
            sink.write(ManagementFactory.getPlatformMBeanServer().getAttribute(
                    new ObjectName(arguments.resolveString(0)), arguments.resolveString(1))
                    .toString());
        }
        catch (Exception e)
        {
            throw new FactoidException(e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{mbean||<object>||<attribute>}} -- Gets the attribute of the "
                + "specified mbean object from the platform-local MBean server. To "
                + "get a list of all valid objects and attributes, start the bot up "
                + "and attach to it with JConsole, then go to the MBeans tab. Each item "
                + "in the tree that has children named \"attributes\" or \"operations\"\n"
                + "is an object that can be used, and you can hover your mouse over it to "
                + "see the name that <object> needs to be. Each item under \"attributes\" "
                + "is an attribute that can be used as <attributes>. If there is no "
                + "such attribute, an exception will be thrown.";
    }
    
}
