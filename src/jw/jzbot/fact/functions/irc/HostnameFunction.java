package jw.jzbot.fact.functions.irc;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class HostnameFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(context.getSender().getHostname());
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {hostname} -- Evaluates to the hostname of the user that invoked "
            + "this factoid or caused this regex to be triggered. Right now, due to "
            + "some technical restrictions of IRC, it's not possible to get the hostname "
            + "of other users. This is because IRC servers like Hyperion (TODO: see if "
            + "seven has this problem) don't cause the user to part and then rejoin all "
            + "channels that the user is a member of, which effectively makes it "
            + "impossible for the bot to detect what that user's hostname is until "
            + "they send a message using their new hostname. In the future, this function "
            + "will support getting what their last known hostmask is, and will document "
            + "that it's not reliable as outlined above.";
    }
    
}
