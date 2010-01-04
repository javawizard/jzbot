package jw.jzbot.commands;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.Command;
import jw.jzbot.FactScope;
import jw.jzbot.Factpack;
import jw.jzbot.JZBot;
import jw.jzbot.Messenger;
import jw.jzbot.ResponseException;
import jw.jzbot.ServerUser;
import jw.jzbot.Factpack.FactpackEntry;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactEntity;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.StringSink;
import jw.jzbot.fact.functions.conditional.IfFunction;
import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.pastebin.PastebinProvider.Feature;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.Factoid;
import jw.jzbot.storage.HasFactoids;
import jw.jzbot.storage.Server;
import jw.jzbot.utils.JZUtils;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

import net.sf.opengroove.common.proxystorage.StoredList;
import net.sf.opengroove.common.utils.StringUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.opengroove.utils.English;

public class FactoidCommand implements Command
{
    
    public String getName()
    {
        return "factoid";
    }
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments)
    {
        FactScope scope;
        if (arguments.startsWith("global "))
        {
            scope = FactScope.global;
            arguments = arguments.substring("global ".length());
        }
        else if (arguments.startsWith("server "))
        {
            scope = FactScope.server;
            arguments = arguments.substring("server ".length());
        }
        else
        {
            scope = FactScope.channel;
        }
        String[] argumentsTokenized1 = arguments.split(" ", 2);
        String command = argumentsTokenized1[0];
        if (isScopedCommand(command))
        {
            if (scope == FactScope.server && server == null)
                throw new ResponseException(
                        "You need to specify a server when running this.");
            if (scope == FactScope.channel && (server == null || channel == null))
                throw new ResponseException(
                        "You need to specify a channel when running this.");
        }
        // if ((!isGlobal) && (channel == null) &&
        // (!command.equalsIgnoreCase("isglobal")))
        // {
        // sender.sendMessage(pm, server, channel,
        // "For non-global commands, you must specify "
        // + "a channel (unless it is the isglobal command)");
        // return;
        // }
        String afterCommand = (argumentsTokenized1.length > 1) ? argumentsTokenized1[1]
                : "";
        /*
         * command is something like create, delete, isglobal, etc., and afterCommand is
         * the rest
         */
        Server s = null;
        if (scope == FactScope.server || scope == FactScope.channel)
            s = JZBot.storage.getServer(server);
        Channel c = null;
        if (scope == FactScope.channel)
            c = s.getChannel(channel);
        boolean processed = false;
        /*
         * oldFact is set to the old factoid when the replace command is used. This is
         * then used when the new factoid is created to set its restricted status and
         * request counts.
         */
        Factoid oldFact = null;
        if (command.equals("delete") || command.equals("replace"))
        {
            processed = true;
            sender.verifySuperop();
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2 && command.equals("replace"))
                throw new ResponseException(
                        "You need to specify the new contents of the factoid");
            String factoidName = argumentsTokenized2[0];
            if (factoidName.equals(""))
                throw new ResponseException("You need to specify the name of the factoid");
            Factoid f;
            if (scope == FactScope.global)
                f = JZBot.storage.getFactoid(factoidName);
            else if (scope == FactScope.server)
                f = s.getFactoid(factoidName);
            else
                f = c.getFactoid(factoidName);
            if (f == null)
            {
                // if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                // throw new ResponseException(
                // "That factoid doesn't exist. However, there is a global "
                // + "factoid with that name. Use \"factoid global\" instead "
                // + "of \"factoid\" in the command to do stuff with "
                // + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist at that scope.");
            }
            if (scope == FactScope.global)
                JZBot.storage.getFactoids().remove(f);
            else if (scope == FactScope.server)
                s.getFactoids().remove(f);
            else
                c.getFactoids().remove(f);
            if (command.equals("delete"))
                source.sendMessage("Factoid " + afterCommand + " deleted.");
            if (command.equals("replace"))
                oldFact = f;
        }
        if (command.equals("create") || command.equals("replace"))
        {
            processed = true;
            sender.verifySuperop();
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid name");
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2)
                throw new ResponseException("You need to specify the factoid contents");
            String factoidName = argumentsTokenized2[0];
            if (JZBot.commands.get(factoidName) != null)
                throw new ResponseException("That is a reserved keyword.");
            if (!JZBot.isValidFactoidName(factoidName))
                throw new ResponseException(
                        "That factoid name contains invalid characters. Factoid "
                                + "names must not start with \"#\", \"@\", or \"%\", and "
                                + "must contain at least one character.");
            String factoidContents = argumentsTokenized2[1];
            if (scope == FactScope.channel && c.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a channel-specific factoid");
            else if (scope == FactScope.server && s.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a server-specific factoid");
            else if (scope == FactScope.global
                    && JZBot.storage.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a global factoid");
            boolean fromPastebin = PastebinService.understands(factoidContents);
            if (fromPastebin)
                factoidContents = PastebinService.readPost(factoidContents).getData();
            /*
             * The factoid does not exist. Let's create it. First, we'll try parsing it to
             * make sure we don't have syntax errors.
             */
            try
            {
                FactParser.parse(factoidContents, "__internal_create_" + factoidName);
            }
            catch (Exception e)
            {
                recreate(oldFact, scope, s, c);
                throw new ResponseException(
                        "There is a syntax error in the contents of the factoid: "
                                + JZBot.pastebinStack(e));
            }
            Factoid f = JZBot.storage.createFactoid();
            f.setCreator(sender.getHostname());
            f.setName(factoidName);
            f.setActive(true);
            f.setValue(factoidContents);
            // history stuff
            f.setCreationTime(System.currentTimeMillis());
            f.setCreatorNick(sender.nick());
            f.setCreatorUsername(JZBot.getThreadLocalUsername());
            f.setDirectRequests(0);
            f.setIndirectRequests(0);
            if (oldFact != null)
            {
                f.setRestricted(oldFact.isRestricted());
                f.setDirectRequests(oldFact.getDirectRequests());
                f.setIndirectRequests(oldFact.getIndirectRequests());
            }
            if (scope == FactScope.global)
                JZBot.storage.getFactoids().add(f);
            else if (scope == FactScope.server)
                s.getFactoids().add(f);
            else
                c.getFactoids().add(f);
            System.out.println("created fact " + factoidName + " " + factoidContents);
            source.sendMessage("Factoid " + factoidName
                    + (command.equals("replace") ? " replaced. " : " created."));
        }
        if (command.equals("list"))
        {
            processed = true;
            System.out.println("command is list, " + scope);
            // JZBot.bot.sendMessage(pm ? sender : channel,
            // "Start of factoid list");
            StoredList<Factoid> list;
            if (scope == FactScope.global)
                list = JZBot.storage.getFactoids();
            else if (scope == FactScope.server)
                list = s.getFactoids();
            else
                list = c.getFactoids();
            if (list != null)
            {
                String currentList = "";
                for (Factoid f : list.isolate())
                {
                    currentList += (f.isLibrary() ? "%" : "")
                            + (f.isRestricted() ? "@" : "") + f.getName() + "  ";
                }
                if (currentList.length() > source.getProtocolDelimitedLength())
                {
                    currentList = JZBot.pastebinNotice(currentList, null);
                }
                if (!currentList.equals(""))
                {
                    source.sendMessage(currentList);
                }
            }
            if (scope == FactScope.global)
                source.sendMessage("End of factoid list.");
            else if (scope == FactScope.server)
                source.sendMessage("End of factoid list. You should also run "
                        + "\"factoid global list\" for"
                        + " global factoids. These were not included " + "in this list.");
            else
                source.sendMessage("End of factoid list. You should also run "
                        + "\"factoid global list\" and \"factoid server list\" for"
                        + " global factoids and server-specific factoids. These "
                        + "were not included " + "in this list.");
        }
        if (command.equals("restrict") || command.equals("unrestrict"))
        {
            // FIXME: need to implement these
            throw new ResponseException("Not implemented yet.");
        }
        if (command.equals("isrestricted"))
        {
            // FIXME: need to implement these
            throw new ResponseException("Not implemented yet.");
        }
        if (command.equals("export"))
        {
            if (scope != FactScope.server)
                throw new ResponseException(
                        "The export command requires exactly a scope of "
                                + "\"server\". I'm hoping to add the ability to "
                                + "export channel-scope and global-scope "
                                + "factoids soon.");
            processed = true;
            Document export = exportServerFactoids(server);
            String data = new XMLOutputter(Format.getPrettyFormat().setIndent("    "))
                    .outputString(export);
            source.sendMessage("Export of all factoids in this bot at this server: "
                    + Pastebin.createPost("jzbot", data, Duration.DAY, null, null));
        }
        if (command.equals("attribute") || command.equals("unattribute"))
        {
            processed = true;
            sender.verifySuperop();
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2)
                throw new ResponseException("You need to specify the name of the factoid "
                        + "and the person to attribute it to.");
            String factoidName = argumentsTokenized2[0];
            if (factoidName.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f = getScopedFactoid(scope, s, c, factoidName);
            if (f == null)
            {
                // if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                // throw new ResponseException(
                // "That factoid doesn't exist. However, there is a global "
                // + "factoid with that name. Use \"factoid global\" instead "
                // + "of \"factoid\" in the command to do stuff with "
                // + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist at that scope.");
            }
            if (command.equals("attribute"))
            {
                f.setAttribution(argumentsTokenized2[1]);
                source.sendMessage("The factoid " + f.getName()
                        + " has been attributed to \"" + f.getAttribution() + "\".");
            }
            else
            {
                String previousAttribution = f.getAttribution();
                if (previousAttribution == null)
                    throw new ResponseException(
                            "This factoid does not currently have an attribution.");
                f.setAttribution(null);
                source.sendMessage("The attribution for " + f.getName()
                        + " (which was previously \"" + previousAttribution
                        + "\") has been removed.");
            }
        }
        if (command.equals("literal"))
        {
            processed = true;
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f = getScopedFactoid(scope, s, c, afterCommand);
            if (f == null)
            {
                // if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                // throw new ResponseException(
                // "That factoid doesn't exist. However, there is a global "
                // + "factoid with that name. Use \"factoid global\" instead "
                // + "of \"factoid\" in the command to do stuff with "
                // + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist at that scope.");
            }
            String value = f.getValue();
            if (value.contains("\n") || value.contains("\r")
                    || value.length() > source.getProtocolDelimitedLength()
                    || PastebinService.understands(value))
                value = Pastebin.createPost("jzbot", value, Duration.DAY, null, null);
            source.sendMessage(value);
        }
        if (command.equals("info"))
        {
            processed = true;
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f = getScopedFactoid(scope, s, c, afterCommand);
            if (f == null)
            {
                // if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                // throw new ResponseException(
                // "That factoid doesn't exist. However, there is a global "
                // + "factoid with that name. Use \"factoid global\" instead "
                // + "of \"factoid\" in the command to do stuff with "
                // + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist");
            }
            int directRequests = f.getDirectRequests();
            int indirectRequests = f.getIndirectRequests();
            int totalRequests = directRequests + indirectRequests;
            String factpack = f.getFactpack();
            String factpackMessage = "";
            if (factpack != null)
            {
                factpackMessage += "; installed by ";
                String[] tokens = factpack.split("\\:", 2);
                factpackMessage += (tokens.length > 1 ? tokens[1] : "");
                if (!"".equals(tokens[0]))
                    factpackMessage += " on " + tokens[0];
            }
            String attribution = f.getAttribution();
            String attributionMessage = "";
            if (attribution != null)
            {
                attributionMessage = "; attributed to \"" + attribution + "\"";
            }
            source.sendMessage("" + f.getName() + " -- created by " + f.getCreatorNick()
                    + " <" + f.getCreatorUsername() + "@" + f.getCreator() + "> at "
                    + new Date(f.getCreationTime()).toString() + "; requested "
                    + totalRequests + " times (" + directRequests + " directly, "
                    + indirectRequests + " indirectly)" + attributionMessage
                    + factpackMessage);
        }
        if (command.equals("pack"))
        {
            doFactpackCommand(pm, sender, source, afterCommand, scope, server, s, channel,
                    c);
            processed = true;
        }
        if (!processed)
        {
            throw new ResponseException(
                    "Invalid factoid command. Try 'factoid [global|server] "
                            + "<list|create|replace|delete|literal|info|pack"
                            + "|restrict|unrestrict|isrestricted|attribute|unattribute >'");
        }
    }
    
    private Factoid getScopedFactoid(FactScope scope, Server s, Channel c, String name)
    {
        if (scope == FactScope.global)
            return JZBot.storage.getFactoid(name);
        else if (scope == FactScope.server)
            return s.getFactoid(name);
        return c.getFactoid(name);
    }
    
    private boolean isScopedCommand(String command)
    {
        return !(command.equals("scope"));
    }
    
    private void doFactpackCommand(boolean pm, ServerUser sender, Messenger source,
            String commandString, FactScope scope, String server, Server s, String channel,
            Channel storedChannel)
    {
        String[] argumentList = commandString.split(" ", 2);
        if (commandString.equals(""))
        {
            throw new ResponseException(
                    "Use \"factoid [global|server] pack <available|list|install|remove|details|info>\"");
        }
        String command = argumentList[0];
        boolean force = false;
        boolean absolute = false;
        if (command.startsWith("+"))
        {
            force = true;
            command = command.substring(1);
            if (command.startsWith("+"))
            {
                absolute = true;
                command = command.substring(1);
            }
        }
        String afterCommand = (argumentList.length == 1 ? "" : argumentList[1]);
        if (command.equals("available"))
        {
            // Commented out because listing available factpacks doesn't really
            // cause any permanent change, so there's not really any reason to
            // restrict it
            // JZBot.verifyOp(channel, hostname);
            File[] files = JZBot.listLocalFactpackFiles();
            String[] items = new String[files.length + 1];
            HashMap<String, Factpack> packMap = new HashMap<String, Factpack>();
            for (int i = 0; i < files.length; i++)
            {
                Factpack pack = Factpack.parse(StringUtils.readFile(files[i]));
                items[i + 1] = pack.name;
                packMap.put(pack.name, pack);
            }
            Arrays.sort(items, 1, items.length);
            items[0] = "" + files.length
                    + " factpacks (use \"factoid pack install <name>\" "
                    + "to install one of these):";
            StringBuffer buffer = new StringBuffer();
            for (int i = 1; i < items.length; i++)
            {
                Factpack pack = packMap.get(items[i]);
                buffer.append("@@").append(items[i]).append("\n");
                if (!pack.description.equals(""))
                    buffer.append(pack.description).append("\n");
                buffer.append("\n").append(StringUtils.delimited(new String[0], ", "));
                buffer.append("\n\n");
            }
            source.sendMessage(JZBot.pastebinNotice(
                    items[0] + "\n\n\n" + buffer.toString(), new Feature[]
                    {
                        Feature.highlight
                    }));
        }
        else if (command.equals("list"))
        {
            boolean all = afterCommand.equals("all");
            sender.verifySuperop();
            ArrayList<String> items = new ArrayList<String>();
            if (all)
            {
                buildFactpackList(JZBot.storage, items);
                for (Server s2 : JZBot.storage.getServers())
                {
                    buildFactpackList(s2, items);
                    for (Channel c2 : s2.getChannels().isolate())
                    {
                        buildFactpackList(c2, items);
                    }
                }
            }
            else
            {
                HasFactoids container;
                if (scope == FactScope.global)
                    container = JZBot.storage;
                else if (scope == FactScope.server)
                    container = s;
                else
                    container = storedChannel;
                buildFactpackList(container, items);
            }
            // TODO: consider pastebinning if there are more than, say, 10 factpacks, or
            // if there are more factpacks than can fit into two messages
            JZUtils.ircSendDelimited(items.toArray(new String[0]), "  ", source);
        }
        else if (command.equals("install"))
        {
            doFactpackInstall(pm, sender, source, scope, server, s, channel, storedChannel,
                    force, absolute, afterCommand);
        }
        else if (command.equals("remove"))
        {
            doFactpackRemove(pm, sender, source, scope, server, s, channel, storedChannel,
                    force, absolute, afterCommand);
        }
        else if (command.equals("details"))
        {
            doFactpackDetails(pm, sender, source, scope, server, s, channel, storedChannel,
                    force, absolute, afterCommand);
        }
        else if (command.equals("info"))
        {
            throw new ResponseException(
                    "The \"factoid pack info\" command has not yet been implemented.");
        }
        else
        {
            throw new ResponseException(
                    "Invalid pack command. Try \"factoid pack\" for a list "
                            + "of available pack commands.");
        }
    }
    
    private void doFactpackDetails(boolean pm, ServerUser sender, Messenger source,
            FactScope scope, String server, Server s, String channel,
            Channel storedChannel, boolean force, boolean absolute, String afterCommand)
    {
        String location = afterCommand;
        String packContents;
        boolean fromPastebin;
        if (PastebinService.understands(location))
            packContents = PastebinService.readPost(location).getData();
        else
        {
            File file = JZBot.getLocalFactpackFile(location);
            if (file == null)
                throw new ResponseException("Invalid factpack \"" + location
                        + "\", must be either a known factpack " + "or a pastebin url");
            packContents = StringUtils.readFile(file);
        }
        Factpack factpack = Factpack.parse(packContents);
        String sn = factpack.name + ": ";
        String[] strings = new String[0];
        JZUtils.ircSendDelimited(sn, strings, ", ", source);
        if (!factpack.description.equals(""))
        {
            String[] descStrings = factpack.description.split("\n");
            if (descStrings.length > 2)
            {
                descStrings = new String[]
                {
                    "See " + JZBot.pastebinNotice(factpack.description, null)
                            + " for the full description"
                };
            }
            for (String l : descStrings)
                source.sendMessage(l);
        }
    }
    
    private void doFactpackRemove(boolean pm, ServerUser sender, Messenger source,
            FactScope scope, String server, Server s, String channel,
            Channel storedChannel, boolean force, boolean absolute, String afterCommand)
    {
        /*
         * Here's what we need to do:
         * 
         * First, we need to validate that the user has permission to remove this factoid.
         * To do that, we do this:
         */
        // -- if isGlobal is true
        // ---- make sure that the user is a superop
        // -- if isGlobal is false
        // ---- if the factpack has factoids present globally for that
        // installation
        // ------ make sure that the user is a superop
        // ---- else (if the factpack does not have any global factoids)
        // ------ make sure that the user is a channel operator at the channel
        /*
         * Then, we set the factpack name that we're going to remove to be the channel
         * name (or "" if isGlobal is false) plus ":" plus the name of the factpack to
         * remove. We then scan over all factoids in the entire system, and if the
         * factoid's factpack matches, we delete it. If we get through and there were no
         * factoids to delete, then we report to the user that the factpack in question is
         * not installed at the scope the user specified.
         */
        boolean hasAnyFactoids = false;
        ArrayList<Factoid> factoidList = new ArrayList<Factoid>();
        factoidList.addAll(JZBot.storage.getFactoids().isolate());
        for (Server s2 : JZBot.storage.getServers())
        {
            factoidList.addAll(s2.getFactoids().isolate());
            for (Channel c : s2.getChannels().isolate())
            {
                factoidList.addAll(c.getFactoids().isolate());
            }
        }
        String factpackName = generateFactpackScopeName(scope, server, channel) + ":"
                + afterCommand;
        /*
         * Now we do the initial iteration to figure out if there are any factoids, and if
         * they are global.
         */
        for (Factoid f : factoidList)
        {
            if (factpackName.equals(f.getFactpack()))
            {
                hasAnyFactoids = true;
            }
        }
        if (!hasAnyFactoids)
            throw new ResponseException("There isn't such a factpack installed at that "
                    + "scope. Try \"factoid pack list all\".");
        /*
         * Now we check permissions.
         */
        sender.verifySuperop();
        /*
         * We have permission to delete this factpack. Now we'll go through and actually
         * delete it.
         */
        ArrayList<String> uninstallScripts = new ArrayList<String>();
        ArrayList<HasFactoids> containers = new ArrayList<HasFactoids>();
        containers.add(JZBot.storage);
        for (Server s2 : JZBot.storage.getServers())
        {
            containers.add(s2);
            for (Channel c : s2.getChannels().isolate())
            {
                containers.add(c);
            }
        }
        for (HasFactoids container : containers)
        {
            for (Factoid factoid : container.getFactoids().isolate())
            {
                if (factpackName.equals(factoid.getFactpack()))
                {
                    if (factoid.isUninstall())
                        uninstallScripts.add(factoid.getValue());
                    container.getFactoids().remove(factoid);
                }
            }
        }
        /*
         * The factpack has been uninstalled.
         * 
         * FIXME: run the uninstall scripts, and add support during factoid installation
         * for adding the factoid uninstall script.
         */
        source.sendMessage("The factpack has been successfully uninstalled.");
    }
    
    /**
     * Generates the fully-qualified name of the combination of <tt>server</tt> and
     * <tt>channel</tt>, but only nested as deep as <tt>scope</tt> is. For example, if
     * <tt>scope</tt> is {@link FactScope#global global}, then the result will always be
     * the empty string. If <tt>scope</tt> is {@link FactScope#server}, then the result
     * will be either the empty string or it will contain just a server, IE the result
     * will never contain a channel.
     * 
     * @param scope
     *            The scope
     * @param server
     *            The server
     * @param channel
     *            The channel
     * 
     * @return
     */
    private String generateFactpackScopeName(FactScope scope, String server, String channel)
    {
        /*
         * Earlier validation will ensure that server and channel are not null if they
         * would be needed by the scope, so we don't have to worry about checking for null
         * in this method.
         */
        if (scope == FactScope.global)
            return "";
        if (scope == FactScope.server)
            return "@" + server;
        return "@" + server + channel;
    }
    
    private void doFactpackInstall(boolean pm, ServerUser sender, Messenger source,
            FactScope scope, String server, Server s, String channel,
            Channel storedChannel, boolean force, boolean absolute, String afterCommand)
    {
        /*
         * Steps:
         * 
         * Figure out if this is a pastebin or a file, and read into a string the
         * factpack's contents
         * 
         * Create a parsed Factpack object
         * 
         * Look up the factpack's scope. We used then to check here on a ton of stuff to
         * figure out if we needed to see if they were an op or a superop; since ops are
         * now dealt away with, we just need to check and make sure the user is an op.
         */
        FactScope targetScopeLevel = null;
        String targetScopeName = (channel == null ? "" : channel);
        System.out.println("target scope name is " + targetScopeName);
        String location = afterCommand;
        String packContents;
        if (PastebinService.understands(location))
            packContents = PastebinService.readPost(location).getData();
        else
        {
            File file = JZBot.getLocalFactpackFile(location);
            if (file == null)
                throw new ResponseException("Invalid factpack \"" + location
                        + "\", must be either a known factpack name "
                        + "or the URL of a pastebin post containing "
                        + "the factpack to install");
            packContents = StringUtils.readFile(file);
        }
        Factpack factpack = Factpack.parse(packContents);
        if (factpack.scope.equals("global"))
        {
            targetScopeLevel = "global";
            targetScopeName = "";
            vpSuperop(hostname,
                    "That factpack has a scope of \"global\", which requires that "
                            + "you're a superop to install it. You are not, "
                            + "however, a superop.");
        }
        else if (factpack.scope.equals("any"))
        {
            if (isGlobal)
            {
                targetScopeLevel = "global";
                targetScopeName = "";
                vpSuperop(hostname, "That factpack has a scope of \"any\", and you're "
                        + "trying to install it globally. This requires that you're "
                        + "a superop. You are not, however, a superop.");
            }
            else
            {
                targetScopeLevel = "channel";
                vpOp(channel, hostname, "That factpack has a scope of \"any\", and you're "
                        + "trying to install it to the channel " + channel
                        + ". This requires that you're an op at this "
                        + "channel. You are not, however, an op at " + channel + ".");
            }
        }
        else
            throw new ResponseException("Invalid factpack scope: " + factpack.scope);
        /*
         * We've validated that the factpack exists, and that a correct scope is
         * specified, a correct target for this scope is specified, and the user has
         * correct permissions to install this factpack here. Steps:
         * 
         * build a local properties map for installation
         * 
         * run the preinstall script
         * 
         * check dependencies
         */
        Map<String, String> localVars = new HashMap<String, String>();
        localVars.put("factpack-channel", (channel == null ? "" : channel));
        localVars.put("factpack-data", "");
        localVars.put("factpack-name", factpack.name);
        localVars.put("source", pm ? sender : channel);
        localVars.put("sender", sender);
        /*
         * Time to run the preinstall script
         */
        try
        {
            FactEntity preinstallScript = FactParser.parse(factpack.preinstall,
                    "__factpack_preinstall");
            FactContext context = new FactContext();
            context.setChannel(channel);
            context.getLocalVars().putAll(localVars);
            context.setSender(sender);
            context.setServer(server);
            context.setSource(source);
            StringSink sink = new StringSink();
            preinstallScript.resolve(sink, context);
            String response = sink.toString();
            if (!response.equals(""))
                JZBot.bot.sendMessage(pm ? sender : channel, response);
            if ("1".equals(context.getLocalVars().get("fail")))
            {
                throw new ResponseException(
                        "The factpack's preinstall script aborted the installation.");
            }
        }
        catch (ResponseException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ResponseException(
                    "There is a syntax error in this factpack's preinstall script: "
                            + JZBot.pastebinStack(e));
        }
        /*
         * Preinstall script has been run. Now we check dependencies.
         */
        ArrayList<String> existingFactpacks = new ArrayList<String>();
        buildFactpackList(JZBot.storage, existingFactpacks);
        for (Channel c : JZBot.storage.getChannels().isolate())
        {
            buildFactpackList(c, existingFactpacks);
        }
        for (Dependency d : factpack.depends)
        {
            boolean atChannel = isGlobal ? false : existingFactpacks.contains(channel + ":"
                    + d.name);
            boolean atGlobal = existingFactpacks.contains(":" + d.name);
            boolean resolved = false;
            if (d.scope.equals("global"))
                resolved = atGlobal;
            else if (d.scope.equals("any"))
                resolved = atGlobal || atChannel;
            else if (d.scope.equals("exact"))
                resolved = (atGlobal && targetScopeLevel.equals("global"))
                        || (atChannel && targetScopeLevel.equals("channel"));
            if (!resolved)
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "That factpack requires the factpack \"" + d.name
                                + "\" on scope \"" + d.scope
                                + "\", but this factpack is not "
                                + "currently installed. Install \"" + d.name
                                + "\", then try again."
                                + (d.message != null ? " Additional info:" : ""));
                if (d.message != null)
                    JZBot.bot.sendMessage(pm ? sender : channel, d.message);
                if (JZBot.getLocalFactpackFile(d.name) != null)
                    JZBot.bot.sendMessage(pm ? sender : channel, "This factpack (\""
                            + d.name + "\") is available locally. To install "
                            + "it, try \"factoid pack install " + d.name
                            + "\". Then try installing \"" + factpack.name + "\" again.");
                return;
            }
        }
        /*
         * Dependencies have been checked. Steps:
         * 
         * Go through the list of factpack entries
         * 
         * For each one, check the target scope. If it's g, make sure the pack's scope is
         * global or both. If it's c, make sure the pack's scope is channel or both. If
         * it's t, make sure the pack's scope is any.
         * 
         * Then run the rename command, and store its output as the name for the factpack.
         * 
         * Then run the restrict command, and store its output as whether the factoid is
         * restricted.
         * 
         * Then, iterate over the factoids again, and create a factoid for each one, at
         * the scope specified, with the name and restricted setting as specified.
         */
        Map<String, String> realNameMap = new HashMap<String, String>();
        Map<String, Boolean> restrictedMap = new HashMap<String, Boolean>();
        Map<String, Boolean> libraryMap = new HashMap<String, Boolean>();
        HasFactoids targetScope = (targetScopeLevel.equals("channel") ? storedChannel
                : JZBot.storage);
        for (FactpackEntry entry : factpack.factoids)
        {
            String target = entry.target;
            if (target.equals("g")
                    && !(factpack.scope.equals("global") || factpack.scope.equals("both")))
                throw new ResponseException("Invalid target \"g\" for scope \""
                        + factpack.scope + "\"");
            else if (target.equals("c")
                    && !(factpack.scope.equals("channel") || factpack.scope.equals("both")))
                throw new ResponseException("Invalid target \"c\" for scope \""
                        + factpack.scope + "\"");
            else if (target.equals("t") && !(factpack.scope.equals("any")))
                throw new ResponseException("Invalid target \"t\" for scope \""
                        + factpack.scope + "\"");
            else if (!(target.equals("t") || target.equals("c") || target.equals("g")))
                throw new RuntimeException("Internal invalid target/scope error");
            /*
             * Scope is correct. Now we run rename and restricted scripts.
             */
            realNameMap.put(entry.name, runInstallScript("rename_" + entry.name,
                    entry.rename, channel, localVars, sender));
            restrictedMap.put(entry.name, IfFunction.findValue(runInstallScript("restrict_"
                    + entry.name, entry.restrict, channel, localVars, sender)));
            libraryMap.put(entry.name, IfFunction.findValue(runInstallScript("library_"
                    + entry.name, entry.library, channel, localVars, sender)));
            /*
             * Now we make sure this wouldn't overwrite anything.
             */
            if (!force)
            {
                HasFactoids container = target.equals("g") ? JZBot.storage : target
                        .equals("c") ? storedChannel : isGlobal ? JZBot.storage
                        : storedChannel;
                if (container.getFactoid(realNameMap.get(entry.name)) != null)
                    throw new ResponseException(
                            "This factpack wants to install a factoid called \""
                                    + realNameMap.get(entry.name)
                                    + "\", but such a factoid already exists. You can "
                                    + "override this with \"+install\" instead "
                                    + "of \"install\" if you want.");
                
            }
            /*
             * Now we'll check the factoid for syntax errors.
             */
            try
            {
                FactParser.parse(entry.contents, "__factpack_install_parse_" + entry.name);
            }
            catch (Exception e)
            {
                throw new ResponseException("There is a syntax error in the factoid \""
                        + entry.name + "\" in the factpack: " + JZBot.pastebinStack(e));
            }
        }
        /*
         * We've run the scripts and such. Now we go through and actually install the
         * factoids.
         */
        for (FactpackEntry entry : factpack.factoids)
        {
            if (realNameMap.get(entry.name).equals(""))
                continue;
            Factoid fact = JZBot.storage.createFactoid();
            fact.setActive(true);
            fact.setCreationTime(System.currentTimeMillis());
            fact.setCreator(hostname);
            fact.setCreatorNick(sender);
            fact.setCreatorUsername(JZBot.getThreadLocalUsername());
            fact.setDirectRequests(0);
            fact.setFactpack(targetScopeName + ":" + factpack.name);
            System.out.println("factpack is " + fact.getFactpack());
            fact.setIndirectRequests(0);
            fact.setName(realNameMap.get(entry.name));
            System.out.println("library map is " + libraryMap.get(entry.name));
            System.out.println("restricted map is " + restrictedMap.get(entry.name));
            fact.setLibrary(libraryMap.get(entry.name));
            fact.setRestricted(restrictedMap.get(entry.name));
            fact.setValue(entry.contents);
            HasFactoids container = entry.target.equals("g") ? JZBot.storage : entry.target
                    .equals("c") ? storedChannel : isGlobal ? JZBot.storage : storedChannel;
            Factoid oldFact = container.getFactoid(fact.getName());
            if (oldFact != null)
                container.getFactoids().remove(oldFact);
            container.getFactoids().add(fact);
        }
        /*
         * The factoids are installed. Now we run the postinstall script, and we're done.
         */
        String response = "";
        try
        {
            response = runInstallScript("postinstall", factpack.postinstall, channel,
                    localVars, sender);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "The postinstall script had an error. The factpack has still been installed. "
                            + JZBot.pastebinStack(e));
        }
        if (!response.equals(""))
            JZBot.bot.sendMessage(pm ? sender : channel, response);
        JZBot.bot.sendMessage(pm ? sender : channel,
                "The factpack has been successfully installed.");
    }
    
    private String runInstallScript(String name, String text, String channel,
            Map<String, String> localVars, String sender)
    {
        try
        {
            FactEntity renameScript = FactParser.parse(text, "__factpack_script_" + name);
            FactContext context = new FactContext();
            context.setChannel(channel);
            context.getLocalVars().putAll(localVars);
            context.setSender(sender);
            StringSink sink = new StringSink();
            renameScript.resolve(sink, context);
            String response = sink.toString();
            return response;
        }
        catch (Exception e)
        {
            throw new ResponseException(
                    "There is a syntax error in one of this factpack's scripts: "
                            + JZBot.pastebinStack(e));
        }
    }
    
    private void recreate(Factoid oldFact, FactScope scope, Server s, Channel c)
    {
        if (oldFact != null)
        {
            if (scope == FactScope.global)
            {
                if (JZBot.storage.getFactoid(oldFact.getName()) == null)
                    JZBot.storage.getFactoids().add(oldFact);
            }
            else if (scope == FactScope.server)
            {
                if (s.getFactoid(oldFact.getName()) == null)
                    s.getFactoids().add(oldFact);
            }
            else
            {
                if (c.getFactoid(oldFact.getName()) == null)
                    c.getFactoids().add(oldFact);
            }
        }
    }
    
    private static void buildFactpackList(HasFactoids container, ArrayList<String> list)
    {
        for (Factoid fact : container.getFactoids().isolate())
        {
            String factpack = fact.getFactpack();
            if (factpack != null && !list.contains(factpack))
                list.add(factpack);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Document exportServerFactoids(String server)
    {
        Server s = JZBot.storage.getServer(server);
        Element root = new Element("factoids");
        root.setAttribute("scope", "server");
        Document doc = new Document(root);
        Element global = new Element("global");
        root.getChildren().add(global);
        populateFactoidExport(global, JZBot.storage);
        for (Channel channel : s.getChannels().isolate())
        {
            Element chan = new Element("channel");
            chan.setAttribute("name", channel.getName());
            root.getChildren().add(chan);
            populateFactoidExport(chan, channel);
        }
        return doc;
    }
    
    public static void populateFactoidExport(Element root, HasFactoids container)
    {
        ArrayList<Factoid> factoids = container.getFactoids().isolate();
        for (Factoid factoid : factoids)
        {
            Element e = new Element("factoid");
            root.getChildren().add(e);
            setAttribute(e, "attribution", factoid.getAttribution());
            setAttribute(e, "creationtime", "" + factoid.getCreationTime());
            setAttribute(e, "creator", factoid.getCreator());
            setAttribute(e, "creatornick", factoid.getCreatorNick());
            setAttribute(e, "creatorusername", factoid.getCreatorUsername());
            setAttribute(e, "directrequests", "" + factoid.getDirectRequests());
            setAttribute(e, "factpack", factoid.getFactpack());
            setAttribute(e, "indirectrequests", "" + factoid.getIndirectRequests());
            setAttribute(e, "name", factoid.getName());
            setAttribute(e, "value", factoid.getValue());
            setAttribute(e, "active", "" + factoid.isActive());
            setAttribute(e, "library", "" + factoid.isLibrary());
            setAttribute(e, "restricted", "" + factoid.isRestricted());
            setAttribute(e, "uninstall", "" + factoid.isUninstall());
        }
    }
    
    public static void setAttribute(Element e, String name, String value)
    {
        if (value != null)
            e.setAttribute(name, URLEncoder.encode(value));
    }
}
