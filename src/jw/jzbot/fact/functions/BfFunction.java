package jw.jzbot.fact.functions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.bf.Bfi;


public class BfFunction extends Function
{
    public static final int MAX_MEMORY_SIZE = 256 * 1024;
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        String code = arguments.get(0);
        String input = "";
        int size = 1024;
        if (arguments.length() > 1)
        {
            input = arguments.get(1);
            if (arguments.length() > 2)
                size = Integer.parseInt(arguments.get(2));
        }
        if (size > MAX_MEMORY_SIZE)
            throw new FactoidException("Requested memory size " + size
                    + ", but maximum is " + MAX_MEMORY_SIZE);
        Bfi interpreter = new Bfi();
        ByteArrayInputStream inputstream = new ByteArrayInputStream(input
                .getBytes());
        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        interpreter.setInputStream(inputstream, outputstream);
        interpreter.setProgram(code);
        interpreter.start();
        if (outputstream.size() > (256 * 1024))
            throw new FactoidException(
                    "Program output was more than 256KB, which is not allowed");
        return new String(outputstream.toByteArray());
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
