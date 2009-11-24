package jw.jzbot.fact.functions;

import jw.jzbot.HelpProvider;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class HelpFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String pagename = arguments.get(0);
        for (HelpProvider provider : JZBot.helpProviders)
        {
            String possible = provider.getPage(pagename);
            if (possible != null)
                return possible;
        }
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{help||<page>}} -- Evaluates to the contents of the specified help "
                + "page. <page> should be a page formatted so that sending \"help <page>\" "
                + "to the bot in a pm would get the relevant help page. The resulting help "
                + "page can contain newlines. If the specified help page does not exist, "
                + "{{help}} evaluates to nothing.\n"
                + "{{help||}} evaluates to the contents of the top level help page (IE "
                + "the one that you see if you pm \"help\" to the bot). Also, the "
                + "literal string \"%HELPCMD\" may appear in the result, which "
                + "should be translated to \"~help\" or \"/msg "
                + JZBot.bot.getNick()
                + " help\", depending on where the message was sent from.";
    }
    
}
