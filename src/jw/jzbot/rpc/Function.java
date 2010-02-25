package jw.jzbot.rpc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that must be present on all methods of an RPC target instance that can be
 * invoked via the RPC mechanism.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface Function
{
    
}
