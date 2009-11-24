package jw.jzbot.fact.functions;

import java.io.UnsupportedEncodingException;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.FilterSink;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.StringSink;

public class EscapeFunction extends Function
{
    /**
     * A sink that escapes all characters considered "special" in the factoid language
     * with appropriate escapes or calls to the char function.
     * 
     * @author Alexander Boyd
     * 
     */
    public static class EscapedSink extends FilterSink
    {
        
        public EscapedSink(Sink delegate)
        {
            super(delegate);
        }
        
        @Override
        public void process(char c)
        {
            if ("$%{}|\\".indexOf(c) != -1)
            {
                delegate.add('\\');
                delegate.add(c);
            }
            else if (c == '\n')
                delegate.add("\\n");
            else if (c < 32 || c > 126)
            {
                delegate.add("{{char||");
                delegate.add((int) c);
                delegate.add("}}");
            }
            else
                delegate.add(c);
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
        return "Syntax: {{escape||<text>}} -- Escapes <text> with backslashes, \"\\n\", and "
                + "such, so that the resulting text, when embedded directly within a "
                + "factoid, would evaluate to <text>. For example, all \"|\" characters, "
                + "\"{\" characters, and \"}\" characters are prefixed with a \"\\\". "
                + "<text> can also contain non-ascii-visible characters, and these will "
                + "\nbe replaced with a call to the {{char}} function. Currently, this "
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
        new EscapedSink(sink).add(text);
        return sink.toString();
    }
    
}
