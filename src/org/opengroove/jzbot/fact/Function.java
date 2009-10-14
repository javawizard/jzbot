package org.opengroove.jzbot.fact;

public abstract class Function
{
    // public abstract String getName();
    /**
     * Runs this function.
     * 
     * @param arguments
     *            The arguments that are being given to this function
     * @param context
     *            The context that this function is being run in, which can be
     *            used to get and modify local, global, and persistent
     *            variables, get the channel that this factoid is running in,
     *            get the person that sent the factoid, and so on.
     * @return The value that this function returns.
     */
    public abstract String evaluate(ArgumentList arguments, FactContext context);
    
    /**
     * Gets help on the specified topic for this function. Most functions
     * should, at the very least, return a value from this describing the
     * function. When a user sends "~help functions &lt;name>", where &lt;name>
     * is this function's name, then this method is called with a topic of
     * &lt;tt>null&lt;/tt>. When a user sends
     * "~help functions &lt;name> &lt;topic>", this method is called with a
     * topic of &lt;topic>.
     * 
     * @param topic
     * @return
     */
    public abstract String getHelp(String topic);
    
    /**
     * Returns an empty string array. Subclasses should override this and return
     * topics that <tt>getHelp()</tt> responds to if it responds to specific
     * topics.
     * 
     * @return
     */
    public String[] getTopics()
    {
        return new String[0];
    }
}
