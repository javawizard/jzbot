package jw.jzbot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jw.jzbot.ConnectionWrapper;
import jw.jzbot.JZBot;
import jw.jzbot.Connection;
import jw.jzbot.Messenger;

import org.jibble.pircbot.PircBot;

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
            int targetLength =
                    (currentBuffer.length() == 0 ? item.length() : delimiter.length()
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
    
    public static String[] ircDelimited(Messenger messenger, String[] items,
            String delimiter)
    {
        return delimitedLengthRestricted(items, delimiter, messenger
                .getProtocolDelimitedLength());
    }
    
    public static void ircSendDelimited(String[] items, String delimiter,
            Messenger messenger)
    {
        ircSendDelimited("", items, delimiter, messenger);
    }
    
    public static void ircSendDelimited(String prefix, String[] items, String delimiter,
            Messenger messenger)
    {
        if (items.length > 0)
            items[0] = prefix + items[0];
        else
            items = new String[]
            {
                prefix
            };
        for (String s : ircDelimited(messenger, items, delimiter))
        {
            messenger.sendMessage(s);
        }
    }
    
    public static void sendSpaced(Messenger messenger, String string)
    {
        String[] data = spaced(messenger, string);
        for (String s : data)
            messenger.sendMessage(s);
    }
    
    public static String[] spaced(Messenger messenger, String string)
    {
        return spaced(messenger.getProtocolDelimitedLength(), string);
    }
    
    public static String[] spaced(int max, String string)
    {
        return delimitedLengthRestricted(string.split(" "), " ", max);
    }
    
    public static void sinkStream(final InputStream in)
    {
        new Thread("input-to-dev-null")
        {
            public void run()
            {
                try
                {
                    while (in.read() != -1)
                        ;
                }
                catch (IOException e)
                {
                }
                try
                {
                    in.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
}
