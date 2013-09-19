package jw.jzbot.rpc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a class or a field that can be serialized by the RPC serialization
 * scheme. Fields not annotated with this will not be sent when serialized (similar to how
 * transient fields are not serialized by standard java serialization mechanisms).
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(
{
        ElementType.FIELD, ElementType.TYPE
})
public @interface Serialized
{
}
