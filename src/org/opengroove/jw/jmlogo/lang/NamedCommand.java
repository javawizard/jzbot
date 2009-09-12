package org.opengroove.jw.jmlogo.lang;

public abstract class NamedCommand extends Command
{
    private String name;
    private int minArgs;
    private int maxArgs;
    
    protected NamedCommand(String name, int minArgs, int maxArgs)
    {
        this.name = name;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }
    
    public int getMinArgs()
    {
        return minArgs;
    }
    
    public int getMaxArgs()
    {
        return maxArgs;
    }
    
    public String getName()
    {
        return name;
    }
    
}
