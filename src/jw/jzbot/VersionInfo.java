package jw.jzbot;

import java.util.Properties;

public class VersionInfo
{
    public static int revision;
    public static String shortDateString;
    
    static
    {
        try
        {
            Properties props = new Properties();
            props.load(VersionInfo.class.getResourceAsStream("version-info.props"));
            String revisionString = props.getProperty("revision");
            revisionString = revisionString.substring(revisionString.indexOf(":") + 1,
                    revisionString.lastIndexOf("$"));
            revisionString = revisionString.trim();
            revision = Integer.parseInt(revisionString);
            shortDateString = props.getProperty("date");
            shortDateString = shortDateString.substring(shortDateString.indexOf("(") + 1,
                    shortDateString.indexOf(")"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred while loading version info", e);
        }
    }
}
