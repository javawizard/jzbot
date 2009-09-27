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
            String item = items[i];
            int targetLength = (currentBuffer.length() == 0 ? item.length()
                    : delimiter.length() + item.length());
            if ((currentBuffer.length() + targetLength) > length
                    && currentBuffer.length() > 0)
            {
                strings.add(currentBuffer.toString());
                currentBuffer = new StringBuffer();
            }
            if (currentBuffer.length() != 0)
                currentBuffer.append(delimiter);
            currentBuffer.append(item);
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
