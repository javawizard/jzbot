package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test07
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // Matcher m = Pattern.compile("^(H|h)(...)(....)").matcher(
        // "hello world,");
        // System.out.println(m.find());
        // for (int i = 0; i <= m.groupCount(); i++)
        // {
        // System.out.println("Group " + i + ": " + m.group(i));
        // }
        System.out.println("starting");
        for (int i = 0; i < 1000000; i++)
        {
            Pattern p = Pattern.compile("<([A-Z][A-Z0-9]*)\\b[^>]*>(.*?)</$1>");
        }
        System.out.println("done");
    }
    
}
