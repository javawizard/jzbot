package org.opengroove.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

import net.sf.opengroove.common.utils.StringUtils;

public class English
{
    public static String and(String... items)
    {
        if (items.length == 0)
            return "";
        if (items.length == 1)
            return items[0];
        if (items.length == 2)
            return items[0] + " and " + items[1];
        String[] ns = Arrays.copyOf(items, items.length);
        ns[ns.length - 1] = "and " + ns[ns.length - 1];
        return StringUtils.delimited(ns, ", ");
    }
    
    public static String plural(String item, int number)
    {
        if (number == 1)
            return item;
        return item + "s";
    }
}
