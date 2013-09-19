package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;

/**
 * An annotation that can be present on proxy bean methods that instructs ProxyStorage to
 * do nothing when the method is called. This can be used when a method needs to be added
 * to a proxy bean interface because another class might implement it for some other
 * purpose, and that other class would make some meaningful use of the method.<br/><br/>
 * 
 * Right now, ignored methods cannot return a primitive type. If such a method is called,
 * it will throw a NullPointerException. Methods with an object return type will return
 * null, and methods without a return type will do nothing.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore
{
    
}
