package jw.jzbot.fact.functions.admin;

import jw.jzbot.commands.UpdateCommand;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class UpdateFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        UpdateCommand.startUpdates(context.getSource());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {update} -- Instructs the bot to update itself to "
            + "the latest JZBot version. This function will return relatively "
            + "quickly, and other factoids will continue to run, but after "
            + "updates have been downloaded and installed, the bot will restart "
            + "itself. This function currently causes messages to be sent to "
            + "whatever channel the function was run in; I'm hoping to get "
            + "rid of that some time in the future. This is currently just a "
            + "simple wrapper around an invocation of the ~update command. "
            + "If updates are already in progress, or if this system doesn't "
            + "support updates, an exception will be thrown.";
    }
    
}
