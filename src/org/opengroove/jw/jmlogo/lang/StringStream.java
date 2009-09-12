package org.opengroove.jw.jmlogo.lang;

public class StringStream
{
    private char[] buffer;
    
    private int index;
    
    public StringStream(String s)
    {
        buffer = s.toCharArray();
    }
    
    public char read()
    {
        if (index >= buffer.length)
            return (char) -1;
        return buffer[index++];
    }
    
    public char peek()
    {
        if (index >= buffer.length)
            return (char) -1;
        return buffer[index];
    }
    
    public void rollback()
    {
        if (index > 0)
            index--;
    }
}
