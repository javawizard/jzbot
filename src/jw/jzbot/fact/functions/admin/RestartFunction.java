package jw.jzbot.fact.functions.admin;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class RestartFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        /*
         * It's vital that we exit with code 17. The wrapper script looks for this and
         * restarts the bot when it exits with this code.
         */
        System.exit(17);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {restart} -- Causes the bot to restart. The bot will exit "
                + "immediately when this is called; {restart} will be the last "
                + "function to run in the current factoid.";
    }
    
}
