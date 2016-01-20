package jw.jzbot.fact.functions.code;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.output.NullSink;

/**
 * Created by aboyd on 2016-01-19.
 */
public class BenchmarkFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        long times = 1;
        if (arguments.length() > 1)
            times = Long.parseLong(arguments.resolveString(1));
        NullSink nullSink = new NullSink();
        long start = System.nanoTime();
        for (long i = 0; i < times; i++) {
            arguments.resolve(0, nullSink);
        }
        long end = System.nanoTime();
        sink.write("" + ((((double) end) - ((double) start)) / (1*1000*1000*1000) / times));
    }

    @Override
    public String getHelp(String topic) {
        return "Syntax: {benchmark|<code>|<times>} -- Run <code> and evaluate to the number of seconds it took to " +
                "run. <times> is optional and, if given, causes the code to be run that many times, in which case " +
                "the result is the average amount of time each run took.";
    }
}
