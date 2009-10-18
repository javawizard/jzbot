package org.opengroove.jzbot.utils;

import java.util.ArrayList;
import org.opengroove.jzbot.JZBot;

import org.jibble.pircbot.PircBot;
import org.opengroove.jzbot.Protocol;

public class JZUtils
{
    public static String[] delimitedLengthRestricted(String[] items, String delimiter,
            int length)
    {
        ArrayList<String> strings = new ArrayList<String>();
        StringBuffer currentBuffer = new StringBuffer();
        for (int i = 0; i < items.length; i++)
        {
            String item = items[i];
            int targetLength = (currentBuffer.length() == 0 ? item.length() : delimiter
                    .length()
                    + item.length());
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
        return delimitedLengthRestricted(items, delimiter, JZBot.bot
                .getProtocolDelimitedLength());
    }
    
    public static void ircSendDelimited(String[] items, String delimiter, Protocol bot,
            String recipient)
    {
        ircSendDelimited("", items, delimiter, bot, recipient);
    }
    
    public static void ircSendDelimited(String prefix, String[] items, String delimiter,
            Protocol bot, String recipient)
    {
        if (items.length > 0)
            items[0] = prefix + items[0];
        else
            items = new String[]
            {
                prefix
            };
        for (String s : ircDelimited(items, delimiter))
        {
            bot.sendMessage(recipient, s);
        }
    }
}
