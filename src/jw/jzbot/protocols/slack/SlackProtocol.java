package jw.jzbot.protocols.slack;

import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.Protocol;

public class SlackProtocol implements Protocol {
    @Override
    public Connection createConnection()
    {
        return new SlackConnection();
    }

    @Override
    public String getName()
    {
        return "slack";
    }

    @Override
    public void initialize()
    {
    }
}
