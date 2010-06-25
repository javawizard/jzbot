package jw.jzbot.fact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.fact.exceptions.FactoidException;

public class FactQuota
{
    private int messageCount = 0;
    private int importCount = 0;
    private int maxMessageCount = MAX_MESSAGE_COUNT;
    private int maxImportCount = MAX_IMPORT_COUNT;
    public static final int MAX_IMPORT_COUNT = 500000;
    /*
     * This is essentially disabled for now.
     */
    public static final int MAX_MESSAGE_COUNT = 9000;
    private Map<String, String> chainVars =
            Collections.synchronizedMap(new HashMap<String, String>());
    
    public Map<String, String> getChainVars()
    {
        return chainVars;
    }
    
    public void setChainVars(Map<String, String> chainVars)
    {
        this.chainVars = chainVars;
    }
    
    public void incrementMessageCount()
    {
        messageCount += 1;
        if (messageCount > maxMessageCount)
            throw new FactoidException("Maximum limit of " + maxMessageCount
                + " messages per factoid invocation exceeded.");
    }
    
    public int getMaxMessageCount()
    {
        return maxMessageCount;
    }
    
    public void setMaxMessageCount(int maxMessageCount)
    {
        this.maxMessageCount = maxMessageCount;
    }
    
    public int getMaxImportCount()
    {
        return maxImportCount;
    }
    
    public void setMaxImportCount(int maxImportCount)
    {
        this.maxImportCount = maxImportCount;
    }
    
    public void incrementImportCount()
    {
        importCount += 1;
        if (importCount > maxImportCount)
            throw new FactoidException("Maximum limit of " + MAX_IMPORT_COUNT
                + " {import} and {run} calls per " + "factoid invocation exceeded.");
    }
    
    public int getMessageCount()
    {
        return messageCount;
    }
    
    public void setMessageCount(int messageCount)
    {
        this.messageCount = messageCount;
    }
    
    public int getImportCount()
    {
        return importCount;
    }
    
    public void setImportCount(int importCount)
    {
        this.importCount = importCount;
    }
    
}
