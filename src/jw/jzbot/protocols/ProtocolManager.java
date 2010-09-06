package jw.jzbot.protocols;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jw.jzbot.protocols.newstyle.NewIrcProtocol;

public class ProtocolManager
{
    private static Map<String, Protocol> protocolMap =
            Collections.synchronizedMap(new LinkedHashMap<String, Protocol>());
    
    public static void installProtocol(Protocol protocol)
    {
        protocol.initialize();
        protocolMap.put(protocol.getName(), protocol);
    }
    
    public static boolean hasProtocol(String name)
    {
        return protocolMap.containsKey(name);
    }
    
    public static Protocol getProtocol(String name)
    {
        if (!protocolMap.containsKey(name))
            throw new IllegalArgumentException("No such protocol: " + name);
        return protocolMap.get(name);
    }
    
    public static Connection createConnection(String name)
    {
        return getProtocol(name).createConnection();
    }
    
    public static Set<String> getProtocolNames()
    {
        return protocolMap.keySet();
    }
    
}
