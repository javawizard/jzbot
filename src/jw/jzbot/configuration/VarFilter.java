package jw.jzbot.configuration;

/**
 * An interface that can be implemented and passed to
 * {@link Configuration#addFilter(String, String, VarFilter)} to filter the possible
 * values a variable can have.
 * 
 * @author Alexander Boyd
 * 
 */
public interface VarFilter
{
    /**
     * Asks this filter if it's ok to set a new value for this variable. The filter can
     * return false to indicate that the variable's new value should be silently ignored,
     * or it can throw an exception. If an exception is thrown, the exception will
     * propagate up to the caller that attempted to set the variable in the first place.
     * 
     * @param scope
     *            The scope that the variable is being set into
     * @param name
     *            The full path of the variable
     * @param value
     *            The new value that the variable is to have
     * @return True to allow this variable to be set, false to silently discard the value.
     *         If any of the variable's filters return false, the variable will not be
     *         set, but all of the filters will be invoked regardless of whether or not a
     *         filter early up in the list returns false.
     */
    public boolean filter(String scope, String name, String value);
}
