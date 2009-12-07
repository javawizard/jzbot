package jw.jzbot.fact.functions.admin;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RestartFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            Thread.sleep(1000);
            if (arguments.length() > 0 && !arguments.getString(0).equals(""))
            {
                JZBot.bot.disconnect(arguments.getString(0));
                Thread.sleep(1500);
            }
            /*
             * It's vital that we exit with code 17. The wrapper script looks for this and
             * restarts the bot when it exits with this code.
             */
            System.exit(17);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Problems while restarting as per {{restart}}", e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{restart||<message>}} -- Causes the bot to restart. The bot "
                + "will quit with the specified message. <message> is optional, and if "
                + "it is not present a quit message will not be used.";
    }
    
}
