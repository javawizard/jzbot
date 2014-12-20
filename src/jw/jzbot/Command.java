package jw.jzbot;

import jw.jzbot.scope.Messenger;
import jw.jzbot.scope.UserMessenger;

public interface Command
{
    /**
     * Gets the name of this command.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Checks to see whether this command should apply for the given context and
     * arguments. If this is false, processing will continue as if this command did not
     * exist.
     * 
     * @param server
     * @param channel
     * @param pm
     * @param sender
     * @param source
     * @param arguments
     * @return
     */
    public boolean relevant(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments);
    
    /**
     * Runs this command.
     * 
     * @param server
     * @param channel
     * @param pm
     * @param sender
     * @param source
     * @param arguments
     */
    public void run(String server, String channel, boolean pm, UserMessenger sender,
            Messenger source, String arguments);
    // FIXME: change the method to accept a source too, and have it be either a
    // ServerChannel or a ServerUser or something, both of which implement an interface
    // that has a method for sending messages on it
}
