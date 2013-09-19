package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * If a getter is marked as required (only getters of ProxyBean instances can be
 * marked as required, {@link Default} can be used for primitive and string
 * types), then if the getter is called before the setter is called, it will
 * create a new instance of the property's type, set it, and return it.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Required
{
    
}
