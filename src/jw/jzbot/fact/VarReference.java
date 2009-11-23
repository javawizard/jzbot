package jw.jzbot.fact;

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
            sink.add(spaces(indentation));
            sink.add("global variable: \"");
            sink.add(name);
            sink.add("\"\n");
        }
        else
        {
            sink.add(spaces(indentation));
            sink.add("variable: \"");
            sink.add(name);
            sink.add("\"\n");
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
            sink.add(var);
    }
}
