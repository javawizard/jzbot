package org.opengroove.jw.jmlogo.lang.io;

public class StandardOutputSink implements InterpreterOutputSink
{
    
    public void println(String string)
    {
        System.out.println(string);
    }
    
}
