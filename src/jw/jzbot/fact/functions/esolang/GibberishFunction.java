package jw.jzbot.fact.functions.esolang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.gibberish.Gibberish;
import jw.jzbot.fact.gibberish.Gibberish.Input;
import jw.jzbot.fact.gibberish.Gibberish.BufferedReaderInput;
import jw.jzbot.fact.gibberish.Gibberish.Output;

public class GibberishFunction extends Function
{
    
    public class SinkOutput implements Output
    {
        private Sink sink;
        
        public SinkOutput(Sink sink)
        {
            this.sink = sink;
        }
        
        @Override
        public void write(String s) throws IOException
        {
            sink.write(s);
        }
        
        @Override
        public void writeln(String s) throws IOException
        {
            sink.write(s);
            sink.write("\n");
        }
        
    }
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String code = arguments.resolveString(0);
        String input;
        if (arguments.length() > 1)
            input = arguments.resolveString(1);
        else
            input = "";
        Input reader = new BufferedReaderInput(new BufferedReader(new StringReader(input)));
        Output writer = new SinkOutput(sink);
        Gibberish interpreter = new Gibberish(reader, writer, null);
        interpreter.interpret(code);
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {gibberish|<code>} or {gibberish|<code>|<input>} -- Runs the "
                + "specified code as Gibberish code (see http://esolangs.org/wiki/Gibberish "
                + "for what Gibberish is). If <input> is provided, it will be used as input "
                + "to the Gibberish program if it asks for any input. Otherwise, the "
                + "program will not be able to read any input, and attempts to read input "
                + "will get an end-of-file. This function then evaluates to whatever the " +
                		"Gibberish program outputted.";
    }
    
}
