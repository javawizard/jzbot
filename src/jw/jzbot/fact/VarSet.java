package jw.jzbot.fact;

public interface VarSet
{
    /**
     * Returns a list of all var names that match the specified regex. If the regex is
     * null, returns a list of all var names.
     * 
     * @return
     */
    public String[] list(String regex);
    
    /**
     * Gets the value of the specified variable, or null if that variable doesn't exist
     * 
     * @param name
     * @return
     */
    public String get(String name);
    
    /**
     * Sets the specified variable, or deletes it if the value is null.
     * 
     * @param name
     * @param value
     */
    public void set(String name, String value);
}
