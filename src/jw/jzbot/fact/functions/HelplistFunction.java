package jw.jzbot.fact.functions;

import java.util.ArrayList;
import java.util.Arrays;

import jw.jzbot.HelpProvider;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class HelplistFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String pagename = arguments.resolveString(0);
        DelimitedSink result = new DelimitedSink(sink, " ");
        for (HelpProvider provider : JZBot.helpProviders)
        {
            String[] possible = provider.listPages(pagename);
            if (possible != null)
            {
                for (String s : possible)
                {
                    result.next();
                    result.write(s);
                }
            }
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {helplist|<page>} -- Evaluates to a space-separated list "
                + "of all subpages of the help page <page>. This also means that you "
                + "can get a space-separated list of all functions allowed in "
                + "factoids by using {helplist|functions}. {helplist|} evaluates "
                + "to a list of top-level help pages (IE those that you would see \n"
                + "if you sent \"help\" in a pm to the bot).";
    }
    
}
