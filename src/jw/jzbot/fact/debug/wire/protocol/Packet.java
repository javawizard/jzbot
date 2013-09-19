package jw.jzbot.fact.debug.wire.protocol;

import java.util.concurrent.atomic.AtomicLong;

public class Packet
{
    private String id;
    
    private static AtomicLong idSequence = new AtomicLong();
    
    private static String generateId()
    {
        return "" + System.currentTimeMillis() + "-" + idSequence.getAndIncrement() + "-"
                + ("" + Math.random() + "" + Math.random()).replaceAll("[^0-9]", "");
    }
    
    {
        id = generateId();
    }
    
    private String replyFor;
    
    public String getReplyFor()
    {
        return replyFor;
    }
    
    public void setReplyFor(String replyFor)
    {
        this.replyFor = replyFor;
    }
    
    public <T extends Packet> T replyFor(T packet)
    {
        setReplyFor(packet.getId());
        return packet;
    }
    
    public String getId()
    {
        return id;
    }
    
}
