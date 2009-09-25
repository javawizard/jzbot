package org.opengroove.jzbot.fact;

public class FactoidStackFrame
{
    private int charIndex;
    private String factName;
    private String functionName;
    
    public int getCharIndex()
    {
        return charIndex;
    }
    
    public void setCharIndex(int charIndex)
    {
        this.charIndex = charIndex;
    }
    
    public FactoidStackFrame()
    {
        super();
    }
    
    public String getFactName()
    {
        return factName;
    }
    
    public FactoidStackFrame(int charIndex, String factName, String functionName)
    {
        super();
        this.charIndex = charIndex;
        this.factName = factName;
        this.functionName = functionName;
    }
    
    public void setFactName(String factName)
    {
        this.factName = factName;
    }
    
    public String getFunctionName()
    {
        return functionName;
    }
    
    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
    }
}
