package jw.jzbot.scope;

public interface Agent
{
    /**
     * Returns the canonical name of this agent. This can throw an exception if this agent
     * does not support canonical naming, although this practice is generally a bad idea
     * as most method accepting agents require them to have canonical names. Canonical
     * names are of the form @server, @server#channel, or @server!nick. The global agent
     * has the empty string as its canonical name.
     * 
     * @return
     */
    public String getCanonicalName();
    
    /**
     * Gets the scope name of this agent. This can throw an exception if this agent does
     * not support scope naming, although this practice is generally a bad idea for the
     * same reason that not implementing {@link #getCanonicalName()} is generally a bad
     * idea. The scope name is the smallest scope fully containing this agent. For classes
     * implementing both this interface and {@link Scope}, this method should return the
     * same value as {@link #getCanonicalName()}. For classes implementing
     * {@link Messenger}, this should return the scope closest to the messenger.
     * {@link UserMessenger}, for example, returns the scope of the server that the user
     * is connected to.
     * 
     * @return
     */
    public String getScopeName();
}
