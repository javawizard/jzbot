package jw.jzbot.fact;

public abstract class Function
{
    // public abstract String getName();
    /**
     * Runs this function. The Fact interpreter calls this when a function is to be run to
     * actually run the function. The arguments to the function can be accessed via
     * <tt>arguments</tt>and the context in which the factoid is running (which includes
     * stuff like the factoid's local variables, factoid name, etc) is available via
     * <tt>context</tt>. The function should write its output to <tt>sink</tt> via one or
     * more of the sink's <tt>write</tt> methods.
     * 
     * @param sink
     *            The sink to which this function's output should be written
     * @param arguments
     *            The arguments that are being given to this function
     * @param context
     *            The context that this function is being run in, which can be used to get
     *            and modify local, global, and persistent variables, get the channel that
     *            this factoid is running in, get the person that sent the factoid, and so
     *            on.
     */
    public abstract void evaluate(Sink sink, ArgumentList arguments, FactContext context);
    
    /**
     * Gets help on the specified topic for this function. Most functions should, at the
     * very least, return a value from this describing the function. When a user sends
     * "~help functions &lt;name>", where &lt;name> is this function's name, then this
     * method is called with a topic of &lt;tt>null&lt;/tt>. When a user sends
     * "~help functions &lt;name> &lt;topic>", this method is called with a topic of
     * &lt;topic>.
     * 
     * @param topic
     * @return
     */
    public abstract String getHelp(String topic);
    
    /**
     * Returns an empty string array. Subclasses should override this and return topics
     * that <tt>getHelp()</tt> responds to if it responds to specific topics other than
     * just null.
     * 
     * @return
     */
    public String[] getTopics()
    {
        return new String[0];
    }
}
