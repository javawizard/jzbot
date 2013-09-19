package jw.jzbot.fact.ast;

import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;

public class VarReference extends FactEntity
{
    private String name;
    private boolean global;
    
    public VarReference(String name, boolean global)
    {
        this.name = name;
        this.global = global;
    }
    
    @Override
    public void explain(Sink sink, int indentation, int increment)
    {
        if (global)
        {
            sink.write(spaces(indentation));
            sink.write("global variable: \"");
            sink.write(name);
            sink.write("\"\n");
        }
        else
        {
            sink.write(spaces(indentation));
            sink.write("variable: \"");
            sink.write(name);
            sink.write("\"\n");
        }
    }
    
    @Override
    public void execute(Sink sink, FactContext context)
    {
        String var;
        if (global)
            var = context.getGlobalVars().get(name);
        else
            var = context.getLocalVars().get(name);
        if (var != null)
            sink.write(var);
    }
}
