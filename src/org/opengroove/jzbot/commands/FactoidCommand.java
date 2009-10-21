package org.opengroove.jzbot.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.opengroove.common.proxystorage.StoredList;
import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.Factpack;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.Factpack.Dependency;
import org.opengroove.jzbot.Factpack.FactpackEntry;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactEntity;
import org.opengroove.jzbot.fact.FactParser;
import org.opengroove.jzbot.fact.functions.IfFunction;
import org.opengroove.jzbot.storage.Channel;
import org.opengroove.jzbot.storage.Factoid;
import org.opengroove.jzbot.storage.HasFactoids;
import org.opengroove.jzbot.utils.JZUtils;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;
import org.opengroove.utils.English;

public class FactoidCommand implements Command
{
    public static String PASTEBIN_REGEX = "^http\\://pastebin\\.com/.{6,12}$";
    
    public String getName()
    {
        return "factoid";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        boolean isGlobal = false;
        if (arguments.startsWith("global "))
        {
            isGlobal = true;
            arguments = arguments.substring("global ".length());
        }
        String[] argumentsTokenized1 = arguments.split(" ", 2);
        String command = argumentsTokenized1[0];
        if ((!isGlobal) && (channel == null) && (!command.equalsIgnoreCase("isglobal")))
        {
            JZBot.bot.sendMessage(pm ? sender : channel,
                    "For non-global commands, you must specify "
                            + "a channel (unless it is the isglobal command)");
            return;
        }
        String afterCommand = (argumentsTokenized1.length > 1) ? argumentsTokenized1[1]
                : "";
        /*
         * command is something like create, delete, isglobal, etc., and afterCommand is
         * the rest
         */
        Channel c = null;
        if (!isGlobal)
            c = JZBot.storage.getChannel(channel);
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
            verifyOpSuperop(isGlobal, channel, hostname);
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2 && command.equals("replace"))
                throw new ResponseException("You need to specify the factoid itself");
            String factoidName = argumentsTokenized2[0];
            if (factoidName.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f;
            if (isGlobal)
                f = JZBot.storage.getFactoid(factoidName);
            else
                f = c.getFactoid(factoidName);
            if (f == null)
            {
                if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                    throw new ResponseException(
                            "That factoid doesn't exist. However, there is a global "
                                    + "factoid with that name. Use \"factoid global\" instead "
                                    + "of \"factoid\" in the command to do stuff with "
                                    + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist");
            }
            if (isGlobal)
                JZBot.storage.getFactoids().remove(f);
            else
                c.getFactoids().remove(f);
            if (command.equals("delete"))
                JZBot.bot.sendMessage(pm ? sender : channel, "Factoid " + afterCommand
                        + " deleted.");
            if (command.equals("replace"))
                oldFact = f;
        }
        if (command.equals("create") || command.equals("replace"))
        {
            processed = true;
            verifyOpSuperop(isGlobal, channel, hostname);
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid name");
            String[] argumentsTokenized2 = afterCommand.split(" ", 2);
            if (argumentsTokenized2.length != 2)
                throw new ResponseException("You need to specify the factoid contents");
            String factoidName = argumentsTokenized2[0];
            if (JZBot.commands.get(factoidName) != null)
                throw new ResponseException("That is a reserved keyword.");
            String factoidContents = argumentsTokenized2[1];
            if (c != null && c.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a channel-specific factoid");
            else if (c == null && JZBot.storage.getFactoid(factoidName) != null)
                throw new ResponseException(
                        "That factoid already exists as a global factoid");
            factoidContents = scanForPastebin(factoidContents);
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
                recreate(oldFact, isGlobal, c);
                throw new ResponseException(
                        "There is a syntax error in the contents of the factoid: "
                                + JZBot.pastebinStack(e));
            }
            Factoid f = JZBot.storage.createFactoid();
            f.setCreator(hostname);
            f.setName(factoidName);
            f.setActive(true);
            f.setValue(factoidContents);
            // history stuff
            f.setCreationTime(System.currentTimeMillis());
            f.setCreatorNick(sender);
            f.setCreatorUsername(JZBot.getThreadLocalUsername());
            f.setDirectRequests(0);
            f.setIndirectRequests(0);
            if (oldFact != null)
            {
                f.setRestricted(oldFact.isRestricted());
                f.setDirectRequests(oldFact.getDirectRequests());
                f.setIndirectRequests(oldFact.getIndirectRequests());
            }
            if (isGlobal)
                JZBot.storage.getFactoids().add(f);
            else
                c.getFactoids().add(f);
            System.out.println("created fact " + factoidName + " " + factoidContents);
            JZBot.bot.sendMessage(pm ? sender : channel, "Factoid " + factoidName
                    + (command.equals("replace") ? " replaced. " : " created."));
        }
        if (command.equals("list"))
        {
            processed = true;
            System.out.println("command is list, " + isGlobal);
            // JZBot.bot.sendMessage(pm ? sender : channel,
            // "Start of factoid list");
            StoredList<Factoid> list;
            if (isGlobal)
                list = JZBot.storage.getFactoids();
            else
                list = c.getFactoids();
            if (list != null)
            {
                String currentList = "";
                for (Factoid f : list.isolate())
                {
                    currentList += (f.isLibrary() ? "%" : "")
                            + (f.isRestricted() ? "@" : "") + f.getName() + "  ";
                    if (currentList.length() > 400)
                    {
                        JZBot.bot.sendMessage(pm ? sender : channel, currentList);
                        currentList = "";
                    }
                }
                if (!currentList.equals(""))
                    JZBot.bot.sendMessage(pm ? sender : channel, currentList);
            }
            JZBot.bot.sendMessage(pm ? sender : channel, "End of factoid list. "
                    + (isGlobal ? "" : "You should also run factoid global list for"
                            + " global factoids. These were not included "
                            + "in this list."));
        }
        if (command.equals("restrict") || command.equals("unrestrict"))
        {
            
        }
        if (command.equals("isrestricted"))
        {
            
        }
        if (command.equals("literal"))
        {
            processed = true;
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f;
            if (isGlobal)
                f = JZBot.storage.getFactoid(afterCommand);
            else
                f = c.getFactoid(afterCommand);
            if (f == null)
            {
                if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                    throw new ResponseException(
                            "That factoid doesn't exist. However, there is a global "
                                    + "factoid with that name. Use \"factoid global\" instead "
                                    + "of \"factoid\" in the command to do stuff with "
                                    + "the global factoid.");
                throw new ResponseException("That factoid doesn't exist");
            }
            String value = f.getValue();
            if (value.contains("\n") || value.contains("\r") || value.length() > 400
                    || value.matches(PASTEBIN_REGEX))
                value = "http://pastebin.com/"
                        + Pastebin.createPost("jzbot", value, Duration.DAY, null);
            JZBot.bot.sendMessage(pm ? sender : channel, value);
        }
        if (command.equals("info"))
        {
            processed = true;
            if (afterCommand.equals(""))
                throw new ResponseException("You need to specify the factoid");
            Factoid f;
            if (isGlobal)
                f = JZBot.storage.getFactoid(afterCommand);
            else
                f = c.getFactoid(afterCommand);
            if (f == null)
            {
                if ((!isGlobal) && JZBot.storage.getFactoid(afterCommand) != null)
                    throw new ResponseException(
                            "That factoid doesn't exist. However, there is a global "
                                    + "factoid with that name. Use \"factoid global\" instead "
                                    + "of \"factoid\" in the command to do stuff with "
                                    + "the global factoid.");
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
            JZBot.bot.sendMessage(pm ? sender : channel, "" + f.getName()
                    + " -- created by " + f.getCreatorNick() + " <"
                    + f.getCreatorUsername() + "@" + f.getCreator() + "> at "
                    + new Date(f.getCreationTime()).toString() + "; requested "
                    + totalRequests + " times (" + directRequests + " directly, "
                    + indirectRequests + " indirectly)" + factpackMessage);
        }
        if (command.equals("pack"))
        {
            doFactpackCommand(pm, sender, hostname, afterCommand, isGlobal, channel, c);
            processed = true;
        }
        if (!processed)
        {
            throw new ResponseException("Invalid factoid command. Try 'factoid [global] "
                    + "<list|create|replace|delete|literal|info|pack"
                    + "|restrict|unrestrict|isrestricted>'");
        }
    }
    
    private void doFactpackCommand(boolean pm, String sender, String hostname,
            String commandString, boolean isGlobal, String channel, Channel storedChannel)
    {
        String[] argumentList = commandString.split(" ", 2);
        if (commandString.equals(""))
        {
            throw new ResponseException(
                    "Use \"factoid pack <available|list|install|remove|details|info>\"");
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
                    + "to install one of these, or \"factoid pack available "
                    + "list\" to pastebin info about each one):";
            if (afterCommand.equals(""))
                JZUtils.ircSendDelimited(items, "  ", JZBot.bot, pm ? sender : channel);
            else if (afterCommand.equals("list"))
            {
                StringBuffer buffer = new StringBuffer();
                for (int i = 1; i < items.length; i++)
                {
                    Factpack pack = packMap.get(items[i]);
                    buffer.append("@@").append(items[i]).append("\n\n");
                    if (!pack.description.equals(""))
                        buffer.append(pack.description).append("\n\n");
                    buffer.append(StringUtils.delimited(generateDescriptionStrings(pack,
                            true), ", "));
                    buffer.append("\n\n");
                }
                JZBot.bot.sendMessage(pm ? sender : channel, "http://pastebin.com/"
                        + JZBot.pastebin(buffer.toString()));
            }
            else
            {
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "Invalid \"factoid pack available\" subcommand. "
                                + "Try \"factoid pack available\" for help.");
            }
        }
        else if (command.equals("list"))
        {
            boolean all = afterCommand.equals("all");
            if (all)
                JZBot.verifySuperop(hostname);
            else
                verifyOpSuperop(isGlobal, channel, hostname);
            ArrayList<String> items = new ArrayList<String>();
            if (all)
            {
                buildFactpackList(JZBot.storage, items);
                for (Channel c : JZBot.storage.getChannels().isolate())
                {
                    buildFactpackList(c, items);
                }
            }
            else
            {
                HasFactoids container;
                if (isGlobal)
                    container = JZBot.storage;
                else
                    container = storedChannel;
                buildFactpackList(container, items);
            }
            JZUtils.ircSendDelimited(items.toArray(new String[0]), "  ", JZBot.bot,
                    pm ? sender : channel);
        }
        else if (command.equals("install"))
        {
            doFactpackInstall(pm, sender, hostname, isGlobal, channel, storedChannel,
                    force, absolute, afterCommand);
        }
        else if (command.equals("remove"))
        {
            doFactpackRemove(pm, sender, hostname, isGlobal, channel, storedChannel, force,
                    absolute, afterCommand);
        }
        else if (command.equals("details"))
        {
            doFactpackDetails(pm, sender, hostname, isGlobal, channel, storedChannel,
                    force, absolute, afterCommand);
        }
        else if (command.equals("info"))
        {
            
        }
        else
        {
            throw new ResponseException(
                    "Invalid pack command. Try \"factoid pack\" for a list "
                            + "of available pack commands.");
        }
    }
    
    private void doFactpackDetails(boolean pm, String sender, String hostname,
            boolean isGlobal, String channel, Channel storedChannel, boolean force,
            boolean absolute, String afterCommand)
    {
        String location = afterCommand;
        String packContents;
        if (location.startsWith("http://pastebin.com/"))
            packContents = Pastebin.readPost(location);
        else
        {
            File file = JZBot.getLocalFactpackFile(location);
            if (file == null)
                throw new ResponseException("Invalid factpack \"" + location
                        + "\", must be either a known factpack "
                        + "or a url at http://pastebin.com");
            packContents = StringUtils.readFile(file);
        }
        Factpack factpack = Factpack.parse(packContents);
        String s = factpack.name + ": ";
        String[] strings = generateDescriptionStrings(factpack, false);
        JZUtils.ircSendDelimited(s, strings, ", ", JZBot.bot, pm ? sender : channel);
        if (!factpack.description.equals(""))
        {
            String[] descStrings = factpack.description.split("\n");
            if (descStrings.length > 2)
            {
                descStrings = new String[]
                {
                    "See " + JZBot.pastebin(factpack.description)
                            + " for the full description"
                };
            }
            for (String l : descStrings)
                JZBot.bot.sendMessage(pm ? sender : channel, l);
        }
    }
    
    private String[] generateDescriptionStrings(Factpack factpack, boolean postDescription)
    {
        return new String[]
        {
                "Written by \"" + factpack.author + "\"",
                factpack.scope + " scope",
                "" + factpack.factoids.length + " factoid"
                        + (factpack.factoids.length == 1 ? "" : "s"),
                generateDependencyString(factpack.depends, postDescription)
                        + (postDescription ? ""
                                : factpack.description.equals("") ? "No description"
                                        : "Description:")
        };
    }
    
    private String generateDependencyString(Dependency[] deps, boolean postDescription)
    {
        if (deps.length == 0)
        {
            if (postDescription)
                return "No dependencies";
            else
                return "No dependencies, ";
        }
        String[] depNames = new String[Math.min(deps.length, 6)];
        System.out.println("deps: " + deps.length + ",depNames: " + depNames.length);
        for (int i = 0; i < depNames.length; i++)
        {
            depNames[i] = deps[i].name;
        }
        if (depNames.length < deps.length)
            depNames[depNames.length - 1] = "" + ((deps.length - depNames.length) + 1)
                    + " others";
        return "Depends on " + English.and(depNames) + (postDescription ? "" : "; ");
    }
    
    private void doFactpackRemove(boolean pm, String sender, String hostname,
            boolean isGlobal, String channel, Channel storedChannel, boolean force,
            boolean absolute, String afterCommand)
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
        boolean hasGlobalFactoids = false;
        boolean hasAnyFactoids = false;
        ArrayList<Factoid> factoidList = new ArrayList<Factoid>();
        factoidList.addAll(JZBot.storage.getFactoids().isolate());
        for (Channel c : JZBot.storage.getChannels().isolate())
        {
            factoidList.addAll(c.getFactoids().isolate());
        }
        String factpackName = (isGlobal ? "" : channel) + ":" + afterCommand;
        /*
         * Now we do the initial iteration to figure out if there are any factoids, and if
         * they are global.
         */
        for (Factoid f : factoidList)
        {
            if (factpackName.equals(f.getFactpack()))
            {
                hasAnyFactoids = true;
                if (JZBot.storage.getFactoids().contains(f))
                    hasGlobalFactoids = true;
            }
        }
        if (!hasAnyFactoids)
            throw new ResponseException("There isn't such a factpack installed at that "
                    + "scope. Try \"factoid pack list all\".");
        /*
         * Now we check permissions.
         */
        if (isGlobal)
            vpSuperop(hostname, "You need to be a superop to remove this factpack.");
        else
        {
            if (hasGlobalFactoids)
                vpSuperop(hostname, "You need to be a superop to remove this "
                        + "globally-contributed factpack.");
            else
                vpOp(channel, hostname, "You need to be an op at this channel "
                        + "to remove this factpack.");
        }
        /*
         * We have permission to delete this factpack. Now we'll go through and actually
         * delete it.
         */
        ArrayList<String> uninstallScripts = new ArrayList<String>();
        ArrayList<HasFactoids> containers = new ArrayList<HasFactoids>();
        containers.add(JZBot.storage);
        for (Channel c : JZBot.storage.getChannels().isolate())
        {
            containers.add(c);
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
        JZBot.bot.sendMessage(pm ? sender : channel,
                "The factpack has been successfully uninstalled.");
    }
    
    private void doFactpackInstall(boolean pm, String sender, String hostname,
            boolean isGlobal, String channel, Channel storedChannel, boolean force,
            boolean absolute, String afterCommand)
    {
        /*
         * Steps:
         * 
         * Figure out if this is a pastebin or a file, and read into a string the
         * factpack's contents
         * 
         * Create a parsed Factpack object
         * 
         * Look up the factpack's scope. If it's global, then make sure we're a superop.
         * If it's channel, make sure we've specified a channel and we're op at the
         * channel. If it's both, make sure we've specified a channel and we're superop.
         * If it's any, then if we've specified a channel make sure we're op at the
         * channel, otherwise make sure we're superop. If it's any other value, or if it's
         * not present, then we throw an exception.
         */
        String targetScopeLevel = null;
        String targetScopeName = (channel == null ? "" : channel);
        System.out.println("target scope name is " + targetScopeName);
        String location = afterCommand;
        String packContents;
        if (location.startsWith("http://pastebin.com/"))
            packContents = Pastebin.readPost(location);
        else
        {
            File file = JZBot.getLocalFactpackFile(location);
            if (file == null)
                throw new ResponseException("Invalid factpack \"" + location
                        + "\", must be either a known factpack "
                        + "or a url at http://pastebin.com");
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
        else if (factpack.scope.equals("channel"))
        {
            targetScopeLevel = "channel";
            if (isGlobal)
                throw new ResponseException(
                        "That factpack has a scope of \"channel\", which "
                                + "requires that you specify a channel to "
                                + "install it at.");
            vpOp(channel, hostname, "That factpack has a scope of \"channel\", which "
                    + "requires that you're an op at the channel that "
                    + "you're trying to install it at. You are not, however, "
                    + "an op at " + channel + ".");
        }
        else if (factpack.scope.equals("both"))
        {
            targetScopeLevel = "channel";
            if (!isGlobal)
                throw new ResponseException("That factpack has a scope of \"both\", which "
                        + "requires that you specify a channel to " + "install it at.");
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
            String response = preinstallScript.resolve(context);
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
            String response = renameScript.resolve(context);
            return response;
        }
        catch (Exception e)
        {
            throw new ResponseException(
                    "There is a syntax error in one of this factpack's scripts: "
                            + JZBot.pastebinStack(e));
        }
    }
    
    private void vpSuperop(String hostname, String string)
    {
        if (!JZBot.isSuperop(hostname))
            throw new ResponseException(string);
    }
    
    private void vpOp(String channel, String hostname, String string)
    {
        if (!JZBot.isOp(channel, hostname))
            throw new ResponseException(string);
    }
    
    private String scanForPastebin(String factoidContents)
    {
        if (factoidContents.matches(PASTEBIN_REGEX))
        {
            factoidContents = Pastebin.readPost(factoidContents.trim());
        }
        return factoidContents;
    }
    
    private void recreate(Factoid oldFact, boolean isGlobal, Channel c)
    {
        if (oldFact != null)
        {
            if (isGlobal)
            {
                if (JZBot.storage.getFactoid(oldFact.getName()) == null)
                    JZBot.storage.getFactoids().add(oldFact);
            }
            else
            {
                if (c.getFactoid(oldFact.getName()) == null)
                    c.getFactoids().add(oldFact);
            }
        }
    }
    
    /**
     * Verifies that this is a superop if isGlobal is true. Otherwise, verifies that this
     * is an op.
     * 
     * @param isGlobal
     * @param channel
     * @param hostname
     */
    private void verifyOpSuperop(boolean isGlobal, String channel, String hostname)
    {
        if (isGlobal)
            JZBot.verifySuperop(hostname);
        else
            JZBot.verifyOp(channel, hostname);
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
}
