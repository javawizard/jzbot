package jw.jzbot.crosstalk;

public class Command extends Packet
{
    public String name;
    
    public Command(String name, String... properties)
    {
        for (int i = 0; i < properties.length; i += 2)
            this.properties.put(properties[i], properties[i + 1]);
    }
}
