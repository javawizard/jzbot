package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class ReplaceFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String mode = "regex";
        if (arguments.length() == 4)
        {
            mode = arguments.resolveString(0);
            arguments = arguments.subList(1);
        }
        if (mode.equals("regex"))
        {
            sink.write(arguments.resolveString(0).replaceAll(arguments.resolveString(1),
                    arguments.resolveString(2)));
        }
        else if (mode.equals("text"))
        {
            sink.write(arguments.resolveString(0).replace(arguments.resolveString(1),
                    arguments.resolveString(2)));
        }
        else
            throw new FactoidException("Mode to {replace} was " + mode
                + ", not \"regex\" or \"text\"");
    }
    
    public String getName()
    {
        return "replace";
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {replace|<mode>|<text>|<search>|<replacement>} -- Replaces "
            + "<search> in the text <text> with <replacement> if <mode> is \"text\", or "
            + "replaces any string that matches the regular expression <search> in the "
            + "text <text> with <replacement> (which can contain back references) if <mode> "
            + "is \"regex\". <mode> can be omitted, and will default to regex.\n"
            + "This function is deprecated; {regexreplace} and {textreplace} "
            + "should be used instead.";
    }
    
}
