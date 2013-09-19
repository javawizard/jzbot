package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation that can be applied to a proxy bean interface method to indicate that it
 * should return a formatted string consisting of other properties. The string to return
 * is specified in printf notation.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Printf
{
    /**
     * The printf-style format string that the method should return when invoked.
     * 
     * @return
     */
    public String format();
    
    /**
     * The list of properties present on the proxy bean that should be passed to the
     * printf formatting operation. A property name may be present multiple times; this is
     * useful if a given property is present in the format string multiple times.
     * 
     * @return
     */
    public String[] properties();
}
