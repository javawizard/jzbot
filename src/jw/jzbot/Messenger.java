package jw.jzbot;

public interface Messenger
{
    public void sendMessage(String message);
    
    public void sendAction(String action);
    
    public int getProtocolDelimitedLength();
}
