package org.opengroove.jw.jmlogo.lang;

import java.util.Vector;

public class InterpreterException extends RuntimeException
{
    private Vector stackFrames = new Vector();
    
    public InterpreterException()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public InterpreterException(String s)
    {
        super(s);
        // TODO Auto-generated constructor stub
    }
    
    public void addStackFrame(StackFrame frame)
    {
        stackFrames.addElement(frame);
    }
    
    public StackFrame[] getStackFrames()
    {
        StackFrame[] frames = new StackFrame[stackFrames.size()];
        stackFrames.copyInto(frames);
        return frames;
    }
    
}
