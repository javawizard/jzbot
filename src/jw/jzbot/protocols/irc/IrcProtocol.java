package jw.jzbot.protocols.irc;

import jw.jzbot.configuration.Configuration;
import jw.jzbot.configuration.VarFilter;
import jw.jzbot.configuration.Configuration.VarType;
import jw.jzbot.events.Notify;
import jw.jzbot.events.ScopeListener;
import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;
import jw.jzbot.scope.ScopeLevel;

public class IrcProtocol implements Protocol
{
    
    public static class PortRangeFilter implements VarFilter
    {
        
        @Override
        public boolean filter(String scope, String name, String value)
        {
            int i = Integer.parseInt(value);
            if (i < 0 || i > 65535)
                throw new IllegalArgumentException("The port " + value
                    + " is out of range. Ports must be 0 through 65535.");
            return true;
        }
        
    }
    
    @Override
    public Connection createConnection()
    {
        return new IrcConnection();
    }
    
    @Override
    public String getName()
    {
        return "irc";
    }
    
    @Override
    public void initialize()
    {
        Notify.serverAdded.addListener(new ScopeListener()
        {
            
            @Override
            public void notify(ScopeLevel level, String scope, boolean initial)
            {
                registerConfigVars(scope);
            }
        });
    }
    
    protected void registerConfigVars(String scope)
    {
        Configuration.register(scope, "irc", "Holds configuration "
            + "variables related to the IRC protocol, such as the "
            + "server to connect to and the nickname to use.", VarType.folder, null);
        Configuration.register(scope, "irc/server",
                "The hostname or IP address of the server to connect "
                    + "to. Don't include a port here; instead, use the port "
                    + "configuration variable if you need a port other than 6667.",
                VarType.text, null);
        Configuration.register(scope, "irc/port",
                "The port to use when connecting to the IRC server", VarType.integer,
                "6667");
        Configuration.register(scope, "irc/nick", "The nickname to use when connecting",
                VarType.text, null);
        Configuration.addFilter(scope, "irc/port", new PortRangeFilter());
    }
}
