package net.sf.opengroove.common.proxystorage;

/**
 * Indicates that another property should be regenerated when this one changes.
 * 
 * @author Alexander Boyd
 * 
 */
public @interface Regenerated
{
    /**
     * 
     * 
     * @return
     */
    public String property();
    
    public Class<? extends Regenerator> regenerator();
}
