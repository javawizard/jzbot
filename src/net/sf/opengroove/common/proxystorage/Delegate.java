package net.sf.opengroove.common.proxystorage;

public interface Delegate
{
    public Object get(Object on, Class propertyClass,
        String property);
}
