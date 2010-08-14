package net.sf.opengroove.common.proxystorage;

/**
 * An interface that provides the ability to regenerate properties.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Regenerator
{
    /**
     * Generates a new value for the property specified, as a result of a change
     * to another property.
     * 
     * @param on
     *            The object that all of this is occuring on (IE the object that
     *            has the properties specified)
     * @param changedProperty
     *            The property that changed
     * @param property
     *            The property that this regenerator is supposed to regenerate
     * @return A new value for the property specified
     */
    public Object regenerate(Object on,
        String changedProperty, String property);
}
