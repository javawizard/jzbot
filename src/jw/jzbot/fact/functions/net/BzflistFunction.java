package jw.jzbot.fact.functions.net;

import java.util.ArrayList;
import java.util.Map;

import jw.jzbot.bzf.ListservConnector;
import jw.jzbot.bzf.Server;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class BzflistFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            Server[] servers = ListservConnector.getServers();
            String prefix = arguments.resolveString(0);
            String delimiter = "";
            if (arguments.length() > 2)
                delimiter = arguments.resolveString(2);
            DelimitedSink result = new DelimitedSink(sink, delimiter);
            for (Server server : servers)
            {
                setVars(context.getLocalVars(), server, prefix);
                result.next();
                arguments.resolve(1, result);
                if ("1".equals(context.getLocalVars().get(prefix + "-quit")))
                    break;
            }
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception occured while running {{bzflist}}", e);
        }
    }
    
    private void setVars(Map<String, String> vars, Server server, String prefix)
    {
        server.loadIntoVars(vars, prefix);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {bzflist|<prefix>|<action>|<delimiter>} -- Contacts the "
                + "public BZFlag list server and retrieves a list of all servers. For each "
                + "server, <action> is then invoked, with several variables starting with "
                + "\"<prefix>-\" set. The best way to get a list of all of these variables "
                + "is to run {bzflist} with the action being {llvars|<prefix>-}.\n"
                + "If the action sets a variable called <prefix>-quit to the value \"1\", "
                + "{bzflist} will stop iterating over servers and return immediately. "
                + "{bzflist} evaluates to what its actions evaluated to, separated "
                + "by <delimiter>. Unlike most iterating functions, \"<prefix>-\" variables "
                + "set during iteration will not be deleted afterward.";
    }
    
}
