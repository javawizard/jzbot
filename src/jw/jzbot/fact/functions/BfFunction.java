package jw.jzbot.fact.functions;

import java.io.ByteArrayInputStream;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.AsciiSinkStream;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.bf.Bfi;

public class BfFunction extends Function
{
    public static final int MAX_MEMORY_SIZE = 256 * 1024;
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String code = arguments.getString(0);
        String input = "";
        int size = 1024;
        if (arguments.length() > 1)
        {
            input = arguments.getString(1);
            if (arguments.length() > 2)
                size = Integer.parseInt(arguments.getString(2));
        }
        if (size > MAX_MEMORY_SIZE)
            throw new FactoidException("Requested memory size " + size
                    + ", but maximum is " + MAX_MEMORY_SIZE);
        Bfi interpreter = new Bfi();
        ByteArrayInputStream inputstream = new ByteArrayInputStream(input.getBytes());
        interpreter.setInputStream(inputstream, new AsciiSinkStream(sink));
        interpreter.setProgram(code);
        interpreter.start();
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{bf||<code>||<input>||<size>}} -- Executes <code> as BF code and evaluates "
                + "to whatever the code outputs. The memory bank provided to the code is "
                + "<size> positions. <size> is optional, and will be 1024 if not present. "
                + "Each position is a 32-bit signed integer. <input> is also optional, and, if "
                + "present, provides textual input to the bf program.";
    }
    
}
