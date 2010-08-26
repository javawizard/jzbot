package jw.jzbot;

import net.sf.opengroove.common.utils.StringUtils;

public enum ScopeLevel
{
    global, server, channel;
    
    public static ScopeLevel[] validLevels(Object serverName, Object channelName)
    {
        if (serverName == null)
            return new ScopeLevel[] { global };
        if (channelName == null)
            return new ScopeLevel[] { global, server };
        return new ScopeLevel[] { global, server, channel };
    }
    
    public static String validLevelsSpacedString(Object serverName, Object channelName)
    {
        return StringUtils.delimited(validLevels(serverName, channelName),
                new StringUtils.ToString<ScopeLevel>()
                {
                    
                    @Override
                    public String toString(ScopeLevel object)
                    {
                        return object.name();
                    }
                }, " ");
    }
    
    public String createScope(String serverName, String channelName)
    {
        if (this == global)
            return "";
        if (this == server)
            return "@" + serverName;
        return "@" + serverName + channelName;
    }
}
