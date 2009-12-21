package jw.jzbot;

public interface Command
{
    public String getName();
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            String arguments);
}
