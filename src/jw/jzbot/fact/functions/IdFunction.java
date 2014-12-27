package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.utils.Utils;

/**
 * Created by aboyd on 2014-12-27.
 */
public class IdFunction extends Function {
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context) {
        if (arguments.length() == 1)
            context.getLocalVars().put(arguments.resolveString(0), Utils.randomHexId());
        else
            sink.write(Utils.randomHexId());
    }

    @Override
    public String getHelp(String topic) {
        return "TBD";
    }
}
