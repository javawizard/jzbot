package test;

import jw.jzbot.utils.Utils;

public class Test15
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String[] items = generateItems(7, 150);
        String[] strings = Utils.delimitedLengthRestricted(items, " | ", 456);
        for (String s : strings)
        {
            System.out.println(s);
        }
    }
    
    private static String[] generateItems(int count, int length)
    {
        String[] strings = new String[count];
        for (int i = 0; i < count; i++)
        {
            StringBuffer b = new StringBuffer();
            for (int c = 0; c < length; c++)
            {
                b.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt((int) (Math.random() * 26)));
            }
            strings[i] = b.toString();
        }
        return strings;
    }
    
}
