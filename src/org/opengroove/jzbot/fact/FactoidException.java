package org.opengroove.jzbot.fact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FactoidException extends RuntimeException
{
    private Map<String, String> factContents = new HashMap<String, String>();
    private ArrayList<FactoidStackFrame> stackFrames = new ArrayList<FactoidStackFrame>();
    
    public void addFrame(FactoidStackFrame frame)
    {
        stackFrames.add(frame);
    }
    
    public Map<String, String> getFactContentMap()
    {
        return factContents;
    }
    
    public FactoidException(String message)
    {
        super(message);
    }
    
    public FactoidException(Throwable cause)
    {
        super(cause);
    }
    
    public FactoidException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public String createFactoidStackTrace()
    {
        StringBuffer buffer = new StringBuffer();
        FactoidStackFrame firstFrame = stackFrames.size() > 0 ? stackFrames
                .get(0) : null;
        String charMessage = firstFrame == null ? "" : " in "
                + firstFrame.getFactName() + " at char "
                + firstFrame.getCharIndex();
        buffer.append("Factoid error" + charMessage + ": " + getMessage()
                + "\n");
        if (firstFrame != null)
        {
            String content = factContents.get(firstFrame.getFactName());
            if (content != null)
            {
                buffer.append("\n").append(content).append("\n");
                buffer.append(FactEntity.spaces(firstFrame.getCharIndex()))
                        .append("^\n");
            }
        }
        buffer.append("\n");
        for (FactoidStackFrame frame : stackFrames)
        {
            buffer.append("    in " + frame.getFactName() + " -> "
                    + frame.getFunctionName() + " (" + frame.getCharIndex()
                    + ")\n");
        }
        return buffer.toString();
    }
}
