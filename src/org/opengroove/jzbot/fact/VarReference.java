package org.opengroove.jzbot.fact;

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
    public String explain(int indentation, int increment)
    {
        if (global)
            return spaces(indentation) + "global variable: \"" + name + "\"\n";
        else
            return spaces(indentation) + "variable: \"" + name + "\"\n";
    }
    
    @Override
    public String execute(FactContext context)
    {
        String var;
        if (global)
            var = context.getGlobalVars().get(name);
        else
            var = context.getLocalVars().get(name);
        if (var == null)
            return "";
        return var;
    }
}
