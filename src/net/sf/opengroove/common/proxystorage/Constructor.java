package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Methods annotated with this on a proxy bean create new objects. It's an
 * alternative to {@link ProxyStorage#create(Class)}.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Constructor
{
}
