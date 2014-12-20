package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Methods on an object annotated with ProxyBean can be annotated with this to
 * indicate that the method is a search method. When the method is called,
 * objects in a particular stored list on that object will be searched, and a
 * list of those, or the first match, depending on whether the return type for
 * the method is an array of the object or a single instance of the object, will
 * be returned. The return type of the search method should either be the list
 * type of the list to search, or an array of the list type of the list to
 * search.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Search
{
    /**
     * The name of the property on the interface that contains the method
     * annotated with this annotation that is a StoredList, and is the list to
     * be searched.
     * 
     * @return
     */
    public String listProperty();
    
    /**
     * The name of the property that contains the data to be searched. This is a
     * property that should be present on the component type of the stored list
     * to search.
     * 
     * If, for some reason, this search wishes to operate on the proxy storage
     * built-in id, then this string can be the literal "proxystorage_id", and
     * the search method's argument must be of type long.
     * 
     * @return
     */
    public String searchProperty();
    
    /**
     * Whether or not the search must be exact. This is treated as if it were
     * true for all types other than String. If it is true, then the value
     * passed into the search method must be exactly equal to the search
     * property in order for the object to be included in the result list. If it
     * is false (it can only be false if the list property is a string), then
     * the list property need only be equal to the search string as determined
     * by the SQL "like" keyword (with asterisks in the search string replaced
     * with the percent sign) for a particular element to be included in the
     * result list.
     * 
     * @return
     */
    public boolean exact() default true;
    
    /**
     * This is only used if {@link #exact()} is false (and, by extension, the
     * property type annotated is a string). If this is true, then the string is
     * prefixed and suffixed with asterisks, thereby making it so that a search
     * string can appear anywhere within the target property, instead of needing
     * to match starting exactly at the beginning and ending exactly at the end.
     * For example, with this equal to false, the string "abc*ghi" would match
     * "abcdefghi" but not "123abcdefghijkl" or "123abcdefghi". With this set to
     * true, however, all of the examples mentioned would match.

     * @return
     */
    public boolean anywhere() default false;
}
