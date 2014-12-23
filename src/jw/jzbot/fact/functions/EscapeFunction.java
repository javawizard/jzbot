package jw.jzbot.fact.functions;

import java.io.UnsupportedEncodingException;

import jw.jzbot.fact.*;
import jw.jzbot.fact.exceptions.FactoidException;
import jw.jzbot.fact.output.CharFilterSink;
import jw.jzbot.fact.output.StringSink;

public class EscapeFunction extends Function
{
    /**
     * A sink that escapes all characters considered "special" in the factoid language
     * with appropriate escapes or calls to the char function.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class EscapedSink extends CharFilterSink
    {
        
        public EscapedSink(Sink delegate)
        {
            super(delegate);
        }
        
        @Override
        public void process(char c)
        {
            delegate.write(FactParser.escapeLiteralCharacter(c));
        }
        
    }
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        arguments.resolve(0, new EscapedSink(sink));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {escape|<text>} -- Escapes <text> with backslashes, \"\\n\", and "
                + "such, so that the resulting text, when embedded directly within a "
                + "factoid, would evaluate to <text>. For example, all \"|\" characters, "
                + "\"{\" characters, and \"}\" characters are prefixed with a \"\\\". "
                + "<text> can also contain non-ascii-visible characters, and these will "
                + "\nbe replaced with a call to the {char} function. Currently, this "
                + "doesn't correctly support UTF-8.";
    }
    
    /**
     * Returns the specified text, escaped so that all special constructs according to the
     * factoid language are properly escaped.
     * 
     * @param text
     * @return
     */
    public static String escape(String text)
    {
        StringSink sink = new StringSink();
        new EscapedSink(sink).write(text);
        return sink.toString();
    }
    
}
