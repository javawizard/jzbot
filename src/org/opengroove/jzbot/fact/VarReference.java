package org.opengroove.jzbot.fact;

public class VarReference extends FactEntity
{
    private String name;
    
    public VarReference(String name)
    {
        this.name = name;
    }
    
    @Override
    public String explain(int indentation, int increment)
    {
        return spaces(indentation) + "variable: \"" + name + "\"\n";
    }
    
    @Override
    public String resolve(FactContext context)
    {
        String var = context.getLocalVars().get(name);
        if (var == null)
            return "";
        return var;
    }
    
}
