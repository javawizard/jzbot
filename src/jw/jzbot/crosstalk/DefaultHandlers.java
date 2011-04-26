package jw.jzbot.crosstalk;

import jw.jzbot.crosstalk.handlers.factoid.*;

public class DefaultHandlers
{
    public static void init()
    {
        Crosstalk.registerHandler("jzbot.factoid", new FactoidHandler());
    }
}
