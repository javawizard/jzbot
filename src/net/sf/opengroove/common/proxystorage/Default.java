package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a certain property of a proxy bean type is to have a default
 * value other than 0 or false. If this is present but without any attributes,
 * then it will have a default primitive value of 0, false, or the empty string.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Default
{
    /**
     * For int properties, the default int type.
     * 
     * @return
     */
    public int intValue() default 0;
    
    /**
     * For long properties, the default long type.
     * 
     * @return
     */
    public long longValue() default 0;
    
    /**
     * For double properties, the default double type.
     * 
     * @return
     */
    public double doubleValue() default 0;
    
    /**
     * For boolean properties, the default boolean type.
     * 
     * @return
     */
    public boolean booleanValue() default false;
    
    /**
     * For string properties, the default string type.
     * 
     * @return
     */
    public String stringValue() default "";
}
