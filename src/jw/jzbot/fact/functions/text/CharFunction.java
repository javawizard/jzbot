package jw.jzbot.fact.functions.text;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

public class CharFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        sink.write(((char) Integer.parseInt(arguments.resolveString(0))));
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {char|<number>} -- Evaluates to a single character, which is the "
                + "ASCII character denoted by the base-10 number <number>. For example, "
                + "{char|32} results in \" \", {char|49} results in \"1\", and "
                + "{char|65} results in \"A\".\n"
                + "TODO: Right now, this actually converts the number to a character "
                + "in the bot's current charset instead of in ASCII. Perhaps consider "
                + "using two different functions for that, or a separate UTF-8 function.";
    }
    
}
