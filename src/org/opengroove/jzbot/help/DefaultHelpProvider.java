package org.opengroove.jzbot.help;

import java.util.HashMap;

import org.opengroove.jzbot.HelpProvider;
import org.opengroove.jzbot.JZBot;

public class DefaultHelpProvider implements HelpProvider
{
    @Override
    public String getPage(String page)
    {
        if (page.equals(""))
            return ""
                    + JZBot.bot.getNick()
                    + " is an IRC bot. Use \"%HELPCMD% about\" for more info.\n"
                    + "Below each help page (including this one) is a list of subpages, "
                    + "pages on topics under the current one. You can use these to get "
                    + "more information. It also explains how to show each of those pages.";
        else if (page.equals("factoids"))
            return "Factoid help coming soon. In the mean time, try \"%HELPCMD% functions\" "
                    + "for functions that can be used within factoids.";
        return null;
    }
    
    @Override
    public String[] listPages(String page)
    {
        if (page.equals(""))
            return new String[]
            {
                    "about", "factoids"
            };
        return new String[0];
    }
}
