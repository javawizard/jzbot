package jw.jzbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.sf.opengroove.common.utils.StringUtils;

import jw.jzbot.Command;
import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.help.HelpProvider;
import jw.jzbot.help.HelpSystem;
import jw.jzbot.pastebin.PastebinUtils;
import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Server;
import jw.jzbot.utils.Utils;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

public class HelpCommand implements Command
{
    
    public String getName()
    {
        return "help";
    }
    
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments)
    {
        Server datastoreServer = JZBot.storage.getServer(server);
        // FIXME: re-add this as a channel-specific, server-specific, and global
        // configuration variable. The more specific ones override the less specific ones.
        // All except the global one can be unset, which means that the parent one should
        // be used. Global defaults to true, meaning help is allowed in channels (the var
        // would be channelhelp).
        // if (ConfigVars.helpinpm.get().equals("1") && !pm)
        // {
        // if (arguments.equals("functions"))
        // {
        // throw new ResponseException(
        // "You're not allowed to run the help command at a channel. "
        // + "You can use "
        // + "http://code.google.com/p/jzbot/wiki/FactoidFunctions "
        // + "or try sending \"help\" in a pm to the bot instead.");
        // }
        // else
        // {
        // throw new ResponseException(
        // "You're not allowed to run the help command at a channel. "
        // + "Try sending \"help\" in a pm to the bot instead.");
        // }
        // }
        ArrayList<String> subpages = new ArrayList<String>();
        String page = arguments;
        String text = null;
        boolean allSubpages = false;
        if (page.endsWith(" --") || page.equals("--"))
        {
            allSubpages = true;
            if (page.equals("--"))
                page = "";
            else
                page = page.substring(0, page.length() - " --".length());
        }
        for (HelpProvider provider : HelpSystem.getProviders())
        {
            String possibleText = provider.getPage(page);
            if (possibleText != null)
                text = possibleText;
            String[] possibleSubpages = provider.listPages(page);
            if (possibleSubpages != null)
                subpages.addAll(Arrays.asList(possibleSubpages));
        }
        text = text.replace("\n", " ");
        Collections.sort(subpages);
        if (text == null)
            throw new ResponseException("No such help page");
        String helpCommand;
        if (pm)
            helpCommand =
                    "/msg " + JZBot.getRealConnection(server).getConnection().getNick()
                        + " help";
        else
        {
            Channel c = datastoreServer.getChannel(channel);
            if (c != null)
                helpCommand = c.getTrigger() + "help";
            else
                helpCommand = "~help";
        }
        String nick = JZBot.getRealConnection(server).getConnection().getNick();
        text = text.replace("%HELPCMD%", helpCommand).replace("%SELFNICK%", nick);
        if (!allSubpages)
        {
            text = text.replace("\n", " ");
            String[] messages = text.split("\n");
            for (String s : messages)
            {
                if (!s.trim().equals(""))
                    source.sendSpaced(s);
            }
            String pageWithSpace = page;
            if (!pageWithSpace.trim().equals(""))
                pageWithSpace = " " + pageWithSpace;
            String subpagesIntro =
                    "---> Subpages (" + subpages.size() + " pages; use \"" + helpCommand
                        + pageWithSpace + " <pagename>\" to show a page):  ";
            String pageListText = (subpages.size() > 0 ? subpagesIntro : "");
            // TODO: have it pastebin the list of subpages if there are more than, say, 20
            // subpages. Also have it omit "---> No subpages." if there aren't any.
            // UPDATE: this might already be disabled, see the comment about 15 lines
            // down from here.
            pageListText += StringUtils.delimited(subpages.toArray(new String[0]), "   ");
            if (pageListText.length() > 0)
            {
                if (pageListText.length() > (source.getProtocolDelimitedLength() - 4)
                    && source.likesPastebin())
                    pageListText =
                            subpagesIntro
                                + PastebinUtils.tryPastebin(pageListText, null,
                                        pageListText);
                source.sendSpaced(pageListText);
            }
        }
        else
        // if(allSubpages)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("All immediate subpages of \"" + page + "\":\n\n");
            for (String subpage : subpages)
            {
                String subtext = "";
                for (HelpProvider provider : HelpSystem.getProviders())
                {
                    String possibleText = provider.getPage(page + " " + subpage);
                    if (possibleText != null)
                        subtext = possibleText;
                }
                buffer.append(subpage).append(":\n");
                buffer.append(subtext).append("\n\n");
            }
            source
                    .sendMessage("All subpages of \""
                        + page
                        + "\": "
                        + Pastebin.createPost("jzbot", buffer.toString(), Duration.DAY,
                                null, null));
        }
    }
    
    @Override
    public boolean relevant(String server, String channel, boolean pm,
            UserMessenger sender, Messenger source, String arguments)
    {
        return true;
    }
}
