package net.sf.opengroove.common.proxystorage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * THIS IS NO LONGER USED. 
 * 
 * The class that handles calls to implementations of proxy interfaces.
 * 
 * @author Alexander Boyd
 * 
 */
public class ProxyHandler implements InvocationHandler
{
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        return null;
    }
    
}
