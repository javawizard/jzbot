package org.opengroove.jw.jmlogo.lang;

public class Variable
{
    private Token value;
    /**
     * The context that this variable is declared on, or null if this variable
     * is global
     */
    private InterpreterContext context;
    
    public Variable()
    {
        super();
    }
    
    public Variable(Token value, InterpreterContext context)
    {
        super();
        this.value = value;
        this.context = context;
    }
    
    public Token getValue()
    {
        return value;
    }
    
    public void setValue(Token value)
    {
        this.value = value;
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Variable other = (Variable) obj;
        if (value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }
    
    public InterpreterContext getContext()
    {
        return context;
    }
    
    public void setContext(InterpreterContext context)
    {
        this.context = context;
    }
}
