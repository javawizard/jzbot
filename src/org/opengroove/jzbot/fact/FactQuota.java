package org.opengroove.jzbot.fact;

public class FactQuota
{
    private int messageCount = 0;
    private int importCount = 0;
    public static final int MAX_IMPORT_COUNT = 50;
    public static final int MAX_MESSAGE_COUNT = 7;
    
    public void incrementMessageCount()
    {
        messageCount += 1;
        if (messageCount > MAX_MESSAGE_COUNT)
            throw new FactoidException("Maximum limit of " + MAX_MESSAGE_COUNT
                    + " messages per factoid invocation exceeded.");
    }
    
    public void incrementImportCount()
    {
        importCount += 1;
        if (importCount > MAX_IMPORT_COUNT)
            throw new FactoidException("Maximum limit of " + MAX_IMPORT_COUNT
                    + " {{import}} and {{run}} calls per "
                    + "factoid invocation exceeded.");
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
