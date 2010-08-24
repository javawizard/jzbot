package jw.jzbot;

import java.nio.charset.Charset;

import jw.jzbot.configuration.Configuration;
import jw.jzbot.configuration.Configuration.VarType;
import jw.jzbot.storage.MapEntry;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

import net.sf.opengroove.common.utils.StringUtils;

/**
 * A bunch of global configuration variables used by the bot.<br/><br/>
 * 
 * This has been largely superseded by {@link jw.jzbot.configuration.Configuration} and
 * exists mostly for backward-compatibility. Internally, this class registers all of its
 * enum constants with the new configuration system and migrates them if needed. The new
 * system supports dynamic changing of configuration variables at runtime, typing of
 * varaibles, and channel-specific and server-specific varibles and so should be preferred
 * to this system.
 * 
 * @author Alexander Boyd
 * 
 */
public enum ConfigVars
{
    delay(VarType.integer, "1000",
            "This config variable sets how often marlen can send messages, in milliseconds. "
                + "The default is 1000. The bot will buffer messages if they are sent "
                + "more often than this. This does not take effect until "
                + "the bot is restarted.")
    {
        public void set(String value)
        {
            int i = Integer.parseInt(value);
            super.set(value);
        }
    },
    evalengine(VarType.text, "jeval",
            "See \"~help functions eval\" for information. When {eval} is "
                + "used without an engine specified, whatever engine is set in this "
                + "config variable is the engine that will be used. For example, if "
                + "this config variable is \"jeval\", then running {eval||5+3} "
                + "will function the same as {eval|jeval|5+3}."), charset(VarType.text,
            Charset.defaultCharset().name(), "The charset used by default.")
    {
        
        public void set(String value)
        {
            JZBot.setCurrentCharset(value);
            super.set(value);
        }
    },
    keys(
            VarType.text,
            "",
            "A pipe-separated list of hashes (as obtained from the {hash} function). "
                + "If a user runs \"~superop key <text>\", and then hash of <text> is equal to "
                + "one of the hashes in this list, the user will be made a superop. Note that "
                + "the plaintext forms of keys cannot contain spaces."), notfound(
            VarType.text,
            "",
            "This config variable is the name of a factoid to run when users "
                + "send a message that isn't recognized. If this is blank, then the text \""
                + "Huh? (pm \"help\" for more info)\" will be sent instead."), primary(
            VarType.text,
            "",
            "This config variable is the name of a channel that error messages "
                + "will be sent to when there's not a logical channel to send them to. For"
                + " example, if the global _onready factoid has a syntax error, the message will "
                + "be sent to the channel specified in this config variable. Leaving this empty "
                + "will cause such errors to simply be ignored."), chanops(VarType.bool,
            "0", "This config variable specifies whether channel operators are treated as "
                + "bot ops. Channel operators at the bot's primary channel are also "
                + "treated as bot superops."), openstatus(
            VarType.bool,
            "1",
            "This config variable specifies whether everyone "
                + "can run \"~status\". If this is false, only superops are allowed to run it."), servicemsg(
            VarType.bool,
            "0",
            "If this is true, the bot will authenticate to NickServ by messaging "
                + "it directly. If this is false, the bot will authenticate to NickServ by using "
                + "the IRC \"NICKSERV\" command."), proxytrace(VarType.bool, "0",
            "If this is true, all ProxyStorage calls will be traced and "
                + "the statistics made available via \"~status proxytrace\". "
                + "This tends to decrease performance quite a bit, so it should "
                + "generally only be used by developers wanting to improve "
                + "database performance or by people curious as to what queries "
                + "are being made by the bot. If this is false, no such tracing will "
                + "be performed, and performance will be better. Note that even "
                + "with tracing enabled, the first few calls needed to figure "
                + "out whether tracing should be enabled will not be traced.")
    {
        public void set(String value)
        {
            if (!(value.equals("0") || value.equals("1")))
                throw new ResponseException(
                        "Invalid value; must be 0 or 1, see \"~config proxytrace\" for help");
            super.set(value);
        }
    },
    logsize("0", "This config variable is the maximum size, in bytes, of the logs to "
        + "keep for each channel on a per-channel basis. Use the {logs} function "
        + "to actually read these logs. 0 disables logging. This doesn't take effect "
        + "until the bot is restarted. A channel-specific log size "
        + "will be coming soon."), nolog("",
            "This config variable is a pipe-separated list of channels that should "
                + "not be logged, even if the logsize variable is set to a "
                + "non-zero value. This doesn't take effect until the bot "
                + "is restarted."), modes("",
            "This config variable is a list of user modes that the bot "
                + "should set on itself whenever it connects. \"+\" and \"-\" "
                + "characters should not be included. This is deprecated in favor of "
                + "setting modes with an _onconnect notification."), helpinpm("0",
            "If this is 0 (the default), then ~help can be run in channels. "
                + "If this is 1, then help can only be run in a pm to prevent needless "
                + "spamming of channels.")
    {
        public void set(String value)
        {
            if (!(value.equals("0") || value.equals("1")))
                throw new ResponseException(
                        "Invalid value; must be 0 or 1, see \"~config helpinpm\" for help");
            super.set(value);
        }
    },
    hsubpastebin("0", "This config variable is the number of subpages of a help page to "
        + "display with ~help before pastebinning the list of subpages. 0 disables "
        + "using the pastebin service for this list. TODO: implement this"), lqdelay(
            "30000", "This config var is the delay, in milliseconds, that the bot "
                + "will wait between writing new logs to the log file. The {log} "
                + "function will typically lag behind the actual channel contents "
                + "by this many milliseconds. A restart is needed for this "
                + "to take effect.")
    {
        public void set(String value)
        {
            Integer.parseInt(value);
            super.set(value);
        }
    },
    lqmaxsize("700",
            "This config variable is the maximum number of messages that will be queued "
                + "for writing to the log. If more than this many messages arrive within "
                + "the number of milliseconds specified by the config variable lqdelay, "
                + "some messages will be silently dropped from the log file. A restart is "
                + "needed for this to take effect.")
    {
        public void set(String value)
        {
            Integer.parseInt(value);
            super.set(value);
        }
    },
    xmppdebug("0", "This config variable specifies whether an XMPP debug console "
        + "will be opened for XMPP connections. 1 means yes, all other values "
        + "mean no. A restart is required after this is changed.");
    private String defaultValue;
    private String description;
    private VarType type;
    
    private ConfigVars(VarType type, String defaultValue, String description)
    {
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
    }
    
    public String get()
    {
        MapEntry entry = JZBot.storage.getConfigVar(name());
        if (entry == null)
            return defaultValue;
        return entry.getValue();
    }
    
    public void set(String value)
    {
        MapEntry entry = JZBot.storage.getConfigVar(name());
        if (entry == null)
        {
            entry = JZBot.storage.createMapEntry();
            JZBot.storage.getConfigVars().add(entry);
            entry.setKey(name());
        }
        entry.setValue(value);
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public static void register()
    {
        for(ConfigVars var : ConfigVars.values())
        {
            boolean previouslyMigrated = Configuration.hasDatastoreVar("", var.name());
            Configuration.register("", var.name(), var.getDescription())
        }
    }
}
