package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomProperty
{
    public Class<? extends Delegate> value();
}
