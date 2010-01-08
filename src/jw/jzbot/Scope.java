package jw.jzbot;

import jw.jzbot.fact.FactContext;

/**
 * An interface representing an object that can specify a server and channel for something
 * to be scoped to. The primary implementations of this interface are
 * {@link ServerChannel}, which returns the server and channel that it was passed during
 * construction, and {@link FactContext}, which returns the server and channel that it has
 * been scoped to with the {scope} function or the server and channel at which the
 * factoid was run.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Scope
{
    /**
     * Gets the name of the server that this scope represents, or null if there is no
     * server. There must be a server if there is a channel.
     * 
     * @return
     */
    public String getServerName();
    
    /**
     * Gets the name of the channel that this scope represents, or null if there is no
     * channel. There must not be a channel if there is no server.
     * 
     * @return
     */
    public String getChannel();
}
