package jw.jzbot;

import java.nio.charset.Charset;

import jw.jzbot.storage.MapEntry;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

import net.sf.opengroove.common.utils.StringUtils;

public enum ConfigVars
{
    delay("1000",
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
    evalengine("jeval", "")
    {
        public String getDescription()
        {
            return "See \"~help functions eval\" for information. When {eval} is "
                + "used without an engine specified, whatever engine is set in this "
                + "config variable is the engine that will be used. For example, if "
                + "this config variable is \"jeval\", then running {eval||5+3} "
                + "will function the same as {eval|jeval|5+3}.";
        }
        
    },
    charset(Charset.defaultCharset().name(), "")
    {
        public String getDescription()
        {
            return "This config variable is the charset used to read and write characters "
                + "from and to the IRC server. Available charsets are, separated "
                + "by spaces: http://pastebin.com/"
                + Pastebin.createPost("jzbot", StringUtils.delimited(Charset
                        .availableCharsets().keySet().toArray(new String[0]), "   "),
                        Duration.DAY, null, null);
        }
        
        public void set(String value)
        {
            JZBot.setCurrentCharset(value);
            super.set(value);
        }
    },
    keys("", "A pipe-separated list of hashes (as obtained from the {hash} function). "
        + "If a user runs \"~superop key <text>\", and then hash of <text> is equal to "
        + "one of the hashes in this list, the user will be made a superop. Note that "
        + "the plaintext forms of keys cannot contain spaces."), notfound(
            "",
            "This config variable is the name of a factoid to run when users "
                + "send a message that isn't recognized. If this is blank, then the text \""
                + "Huh? (pm \"help\" for more info)\" will be sent instead."), primary(
            "",
            "This config variable is the name of a channel that error messages "
                + "will be sent to when there's not a logical channel to send them to. For"
                + " example, if the global _onready factoid has a syntax error, the message will "
                + "be sent to the channel specified in this config variable. Leaving this empty "
                + "will cause such errors to simply be ignored."), chanops("0",
            "This config variable specifies whether channel operators are treated as "
                + "bot ops. 1 means they are, 0 means they are not. Channel operators at "
                + "the bot's primary channel are also treated as bot superops.")
    {
        public void set(String value)
        {
            if (!(value.equals("0") || value.equals("1")))
                throw new ResponseException(
                        "Invalid value; must be 0 or 1, see \"~config chanops\" for help");
            super.set(value);
        }
    },
    openstatus("1", "This config variable specifies whether everyone can run \"~status\". "
        + "If this is 1, then everyone can. If this is 0, only superops can.")
    {
        public void set(String value)
        {
            if (!(value.equals("0") || value.equals("1")))
                throw new ResponseException(
                        "Invalid value; must be 0 or 1, see \"~config openstatus\" for help");
            super.set(value);
        }
    },
    servicemsg("0", "If this is 1, the bot will authenticate to NickServ by messaging "
        + "it directly. If this is 0, the bot will authenticate to NickServ by using "
        + "the IRC \"NICKSERV\" command.")
    {
        public void set(String value)
        {
            if (!(value.equals("0") || value.equals("1")))
                throw new ResponseException(
                        "Invalid value; must be 0 or 1, see \"~config servicemsg\" for help");
            super.set(value);
        }
    },
    proxytrace("0", "If this is 1, all ProxyStorage calls will be traced and "
        + "the statistics made available via \"~status proxytrace\". "
        + "This tends to decrease performance quite a bit, so it should "
        + "generally only be used by developers wanting to improve "
        + "database performance or by people curious as to what queries "
        + "are being made by the bot. If this is 0, no such tracing will "
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
            JZBot.proxyTraceConfigChanged();
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
    
    private ConfigVars(String defaultValue, String description)
    {
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
}
