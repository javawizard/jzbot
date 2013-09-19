package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Same as {@link Search}, but searches multiple properties at the same time,
 * returning only results that match all search criteria.<br/><br/>
 * 
 * Most of the methods here aren't documented very well; see {@link Search} for
 * relevant documentation.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CompoundSearch
{
    /**
     * The property holding the list to search
     * 
     * @return
     */
    public String listProperty();
    
    /**
     * The list of properties to match search criteria against. There should be
     * exactly one parameter to the method that this is annotated with for each
     * element here, with it's type corresponding to that property's type.
     * 
     * @return
     */
    public String[] searchProperties();
    
    /**
     * If the search should be exact
     * 
     * @return
     */
    public boolean[] exact();
    
    /**
     * If the search can function anywhere
     * 
     * @return
     */
    public boolean[] anywhere();
}
