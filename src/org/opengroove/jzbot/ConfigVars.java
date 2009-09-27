package org.opengroove.jzbot;

import java.nio.charset.Charset;

import net.sf.opengroove.common.utils.StringUtils;

import org.opengroove.jzbot.storage.MapEntry;
import org.opengroove.jzbot.utils.Pastebin;
import org.opengroove.jzbot.utils.Pastebin.Duration;

public enum ConfigVars
{
    delay(
            "1000",
            "This config variable sets how often marlen can send messages, in milliseconds. "
                    + "For example, if this is 100 (the default), then marlen will send "
                    + "no more than 1 "
                    + "message per second, and will buffer messages so that they aren't sent more "
                    + "often than that.")
    {
        public void set(String value)
        {
            int i = Integer.parseInt(value);
            JZBot.bot.setMessageDelay(i);
            super.set(value);
        }
    },
    evalengine("jeval", "")
    {
        public String getDescription()
        {
            return "See \"~help functions eval\" for information. When {{eval}} is "
                    + "used without an engine specified, whatever engine is set in this "
                    + "config variable is the engine that will be used. For example, if "
                    + "this config variable is \"jeval\", then running {{eval||5+3}} "
                    + "will function the same as {{eval||jeval||5+3}}.";
        }
        
    },
    charset(Charset.defaultCharset().name(), "")
    {
        public String getDescription()
        {
            return "This config variable is the charset used to read and write characters "
                    + "from and to the IRC server. Available charsets are, separated "
                    + "by spaces: http://pastebin.com/"
                    + Pastebin.createPost("jzbot", StringUtils.delimited(
                            Charset.availableCharsets().keySet().toArray(
                                    new String[0]), "   "), Duration.DAY, null);
        }
        
        public void set(String value)
        {
            JZBot.setCurrentCharset(value);
            super.set(value);
        }
    },
    keys("", "More info coming on this soon, but it's pipe-separated.");
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
            entry.setKey(name());
        }
        entry.setValue(value);
    }
    
    public String getDescription()
    {
        return description;
    }
}
