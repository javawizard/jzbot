package jw.jzbot.help;

import jw.jzbot.HelpProvider;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.Function;

public class FunctionHelpProvider implements HelpProvider
{
    
    @Override
    public String getPage(String page)
    {
        // if (page.equals("functions"))
        // return
        // "Functions are pieces of text that you can embed within a factoid that cause "
        // + "it to do special stuff like use \"/me\" to send a message or run "
        // +
        // "another factoid some time into the future. Each subpage of this page is "
        // +
        // "the name of a function that you can use within a factoid.";
        // else
        if (page.startsWith("functions "))
        {
            String[] tokens = page.split(" ", 3);
            String functionName = tokens[1];
            String subpage = null;
            if (tokens.length > 2)
                subpage = tokens[2];
            Function function = FactParser.getFunction(functionName);
            if (function == null)
                return "No such function by that name.";
            return function.getHelp(subpage);
        }
        return null;
    }
    
    @Override
    public String[] listPages(String page)
    {
        if (page.equals("functions"))
        {
            return FactParser.getFunctionNames();
        }
        // if (page.equals(""))
        // {
        // return new String[]
        // {
        // "functions"
        // };
        // }
        return new String[0];
    }
    
}
