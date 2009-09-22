package org.opengroove.jzbot.fact;

public abstract class Function
{
    // public abstract String getName();
    
    public abstract String evaluate(ArgumentList arguments, FactContext context);
    
    public abstract String getHelp(String topic);
    
    /**
     * Returns an empty string array. Subclasses should override this and return
     * topics that they respond to if they respond to specific topics.
     * 
     * @return
     */
    public String[] getTopics()
    {
        return new String[0];
    }
}
