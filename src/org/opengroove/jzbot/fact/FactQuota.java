package org.opengroove.jzbot.fact;

public class FactQuota
{
    private int messageCount = 0;
    private int importCount = 0;
    private int maxMessageCount = MAX_MESSAGE_COUNT;
    private int maxImportCount = MAX_IMPORT_COUNT;
    public static final int MAX_IMPORT_COUNT = 50;
    public static final int MAX_MESSAGE_COUNT = 7;
    
    public void incrementMessageCount()
    {
        messageCount += 1;
        if (messageCount > maxMessageCount)
            throw new FactoidException("Maximum limit of " + MAX_MESSAGE_COUNT
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
