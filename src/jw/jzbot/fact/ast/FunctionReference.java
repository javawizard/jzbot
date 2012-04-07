package jw.jzbot.fact.ast;

import java.util.List;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.FactoidStackFrame;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class FunctionReference extends FactEntity {
    private Sequence arguments;
    
    private String functionName;
    
    // public FunctionReference(Sequence arguments) {
    // this.arguments = arguments;
    // }
    
    public FunctionReference(List<FactEntity> arguments) {
        this.arguments = new Sequence(arguments);
    }
    
    @Override
    public void execute(Sink sink, FactContext context) {
        ArgumentList list = new ArgumentList(arguments, context);
        functionName = list.getString(0);
        Function function = FactParser.getFunction(functionName);
        if (function == null)
            function = context.createDynamicFunction(functionName);
        ArgumentList sublist = list.subList(1);
        try {
            function.evaluate(sink, sublist, context);
        } catch (FactoidException e) {
            throw e;
        } catch (Exception e) {
            throw new FactoidException("Internal error while running "
                    + functionName, e);
        }
    }
    
    @Override
    protected void addStackFrame(FactoidException e) {
        super.addStackFrame(e);
        if (isOmitFromStack())
            return;
        String functionName = this.functionName;
        if (functionName == null || functionName.equals(""))
            functionName = "(blank function reference)";
        e.addFrame(new FactoidStackFrame(super.getCharIndex(), super
                .getFactName(), functionName));
    }
    
    @Override
    public void explain(Sink sink, int indentation, int increment) {
        sink.write(spaces(indentation));
        sink.write("function:\n");
        arguments.explain(sink, indentation, increment, false);
    }
    
    public Sequence getArgumentSequence() {
        return arguments;
    }
}
