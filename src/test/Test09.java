package test;

import org.opengroove.utils.English;

public class Test09
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println(English.and("one"));
        System.out.println(English.and("one", "two"));
        System.out.println(English.and("one", "two", "three"));
        System.out.println(English.and("one", "two", "three", "four"));
        System.out.println(English.and("one", "two", "three", "four", "five"));
        System.out.println(English.and("one", "two", "three", "four", "five", "six"));
    }
    
}
