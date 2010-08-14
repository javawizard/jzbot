package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * Allows the length of a string property to be set (it's default is 1024). If a
 * value other than 1024 is desired, then the getter of a property should be
 * annotated with this, and the value will be used as the size of the varchar
 * column in the database used to store that property.
 * 
 * This can also annotate properties of type BigInteger, in which case it
 * determines the number of nybbles that can be present in the big integer. More
 * precisely, BigIntegers are stored as strings that contain a hexadecimal
 * representation of the big integer's value. The length, then, specified the
 * maximum number of characters that the big integer can have in it's
 * hexidecimal representation.
 */
public @interface Length
{
    public int value() default 1024;
}
