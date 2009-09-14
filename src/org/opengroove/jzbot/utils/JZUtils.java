package org.opengroove.jzbot.utils;

import java.util.ArrayList;

import org.jibble.pircbot.PircBot;

public class JZUtils
{
    public static String[] delimitedLengthRestricted(String[] items,
            String delimiter, int length)
    {
        ArrayList<String> strings = new ArrayList<String>();
        StringBuffer currentBuffer = new StringBuffer();
        for (int i = 0; i < items.length; i++)
        {
            currentBuffer.append(items[i]).append(delimiter);
            if (currentBuffer.length() > length)
            {
                strings.add(currentBuffer.toString());
                currentBuffer = new StringBuffer();
            }
        }
        if (!currentBuffer.toString().equals(""))
            strings.add(currentBuffer.toString());
        return strings.toArray(new String[0]);
    }
    
    public static String[] ircDelimited(String[] items, String delimiter)
    {
        return delimitedLengthRestricted(items, delimiter, 400);
    }
    
    public static void ircSendDelimited(String[] items, String delimiter,
            PircBot bot, String recipient)
    {
        for (String s : ircDelimited(items, delimiter))
        {
            bot.sendMessage(recipient, s);
        }
    }
}
