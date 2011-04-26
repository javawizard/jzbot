package jw.jzbot.crosstalk;

public class Response extends Packet
{
    public Response(String... properties)
    {
        for (int i = 0; i < properties.length; i += 2)
            this.properties.put(properties[i], properties[i + 1]);
    }
}
