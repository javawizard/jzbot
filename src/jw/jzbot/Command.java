package jw.jzbot;

public interface Command
{
    public String getName();
    
    public void run(String server, String channel, boolean pm, ServerUser sender,
            Messenger source, String arguments);
    // FIXME: change the method to accept a source too, and have it be either a
    // ServerChannel or a ServerUser or something, both of which implement an interface
    // that has a method for sending messages on it
}
