package org.opengroove.jzbot.help;

import java.util.HashMap;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.HelpProvider;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.utils.JZUtils;

public class DefaultHelpProvider implements HelpProvider
{
    @Override
    public String getPage(String page)
    {
        if (page.equals(""))
            return "" + JZBot.bot.getNick()
                    + " is an IRC bot. Use \"%HELPCMD% about\" for more info.";
        else if (page.equals("factoids"))
            return "Factoid help coming soon. In the mean time, try \"%HELPCMD% functions\" "
                    + "for functions that can be used within factoids.";
        else if (page.equals("about"))
            return "" + JZBot.bot.getNick()
                    + " is an IRC bot. The software it runs is JZBot "
                    + "(http://jzbot.googlecode.com). JZBot uses (a "
                    + "slightly modified version of) PircBot"
                    + " (http://jibble.org/pircbot.php) as its IRC "
                    + "library. For other libraries that it uses, see "
                    + "\"%HELPCMD% credits\".\n"
                    + "For a list of JZBot's main developers and authors, "
                    + "see \"%HELPCMD% authors\".";
        else if (page.equals("credits"))
        {
            String[] creditsList = new String[]
            {
                    "JZBot uses the following libraries, and the JZBot developers "
                            + "would like to thank the "
                            + "authors of these libraries for their contributions:  "
                            + "PircBot (http://jibble.org/pircbot.php)",
                    "H2 (http://h2database.org)",
                    "Log4J (http://logging.apache.org/log4j)",
                    "Commons Collections (http://commons.apache.org/collections)",
                    "JEPLite (http://jeplite.sourceforge.net)",
                    "OpenGroove Common (link coming soon)",
                    "MicroCrypt (http://sf.net/projects/microcrypt)",
                    "Eval (http://eval.dev.java.net)",
                    "Julian Bunn's Evaluator (from http://pcbunn.cithep.caltech.edu/jjb.html)",
                    "Float11 (from J2ME Map, http://j2memap.landspurg.net)",
                    "JMLogo Interpreter (http://me.opengroove.org/search?q=jmlogo)"
            };
            String[] messageList = JZUtils.ircDelimited(creditsList, ", ");
            return StringUtils.delimited(messageList, "\n");
        }
        else if (page.equals("authors"))
        {
            String[] authorsList = new String[]
            {
                    "The following people have contributed to "
                            + "the development of JZBot:  "
                            + "Alexander Boyd (javawizard2539/jcp/jpc)",
                    "MrDudle", "Maximilian Dirkmann (schrottplatz)"
            };
            String[] messageList = JZUtils.ircDelimited(authorsList, ", ");
            return StringUtils.delimited(messageList, "\n")
                    + "\nFor people that have contributed (directly or indirectly) to the "
                    + "bot's built-in factpacks, see \"%HELPCMD% packwriters\".";
        }
        else if (page.equals("packwriters"))
        {
            String[] authorsList = new String[]
            {
                    "The following people have either directly provided factoids "
                            + "for the bot's factpacks, or have provided significant inspiration "
                            + "for the bot's factpacks or other functionality:  "
                            + "blast007", "Bambino",
                    "Argooon (blast007's bot)", "ibot (TimRiker's bot)"
            };
            String[] messageList = JZUtils.ircDelimited(authorsList, ", ");
            return StringUtils.delimited(messageList, "\n");
        }
        return null;
    }
    
    @Override
    public String[] listPages(String page)
    {
        if (page.equals(""))
            return new String[]
            {
                    "about", "factoids", "credits", "authors", "packwriters"
            };
        return new String[0];
    }
}
