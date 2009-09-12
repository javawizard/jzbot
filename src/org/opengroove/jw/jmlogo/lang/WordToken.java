package org.opengroove.jw.jmlogo.lang;

public class WordToken extends Token
{
    private String value;
    
    public WordToken(String value)
    {
        super();
        this.value = value;
    }
    
    public WordToken(boolean value)
    {
        this(value ? "true" : "false");
    }
    
    public boolean getBool()
    {
        if (value.equalsIgnoreCase("true"))
            return true;
        else if (value.equalsIgnoreCase("false"))
            return false;
        else
            throw new InterpreterException("The word " + value
                + " needed a boolean value, but didn't have one");
    }
    
    public WordToken(double d)
    {
        if (Math.floor(d) == Math.ceil(d))
        {
            /*
             * This double is a whole number, and has no fractional part.
             */
            this.value = "" + (long) Math.floor(d);
        }
        else
        {
            /*
             * There is a fractional part to this double. We might want to add
             * some sort of rounding capability (so that a logo var could be set
             * that indicates that stuff should be rounded to the nearest
             * 1000th, for example).
             */
            this.value = "" + d;
        }
    }
    
    public String getValue()
    {
        return value;
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
        final WordToken other = (WordToken) obj;
        if (value == null)
        {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }
    
    public double getNumeric()
    {
        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            throw new InterpreterException("The value " + value + " is not a number");
        }
    }
    
    public boolean isNumeric()
    {
        try
        {
            Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
    
    public long getLongStrict()
    {
        double numeric = getNumeric();
        if (Math.floor(numeric) != Math.ceil(numeric))
            throw new InterpreterException("" + numeric
                + " is fractional but it needs to be a whole number");
        return (long) Math.floor(numeric);
    }
}
