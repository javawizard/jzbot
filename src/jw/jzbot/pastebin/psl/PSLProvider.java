package jw.jzbot.pastebin.psl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import jw.jzbot.pastebin.PastebinProvider;
import jw.jzbot.pastebin.Post;

public class PSLProvider implements PastebinProvider
{
    public static void parse(InputStream stream)
    {
        Scanner scanner = new Scanner(stream);
        HashMap<String, String[]> templateMap = new HashMap<String, String[]>();
        boolean hasMorePastebins = true;
        while (hasMorePastebins)
        {
            String header = next(scanner);
            if (header.equals(""))
                break;
            if (header.startsWith("!"))
            {
                /*
                 * This is an implementation of a definition. We'll read the next line to
                 * figure out the parameters, then parse the implementation. Then, if
                 * there is a line after, and if that line is "<-", then we continue.
                 * Otherwise, we break.
                 */
            }
            else
            {
                /*
                 * This is a definition. We'll read lines until we get to one that equals
                 * "<-" and store that into the template map.
                 */
                String next;
                ArrayList<String> lines = new ArrayList<String>();
                while (!(next = next(scanner)).equals("<-"))
                {
                    lines.add(next);
                }
                templateMap.put(header, lines.toArray(new String[0]));
            }
        }
    }
    
    private static String next(Scanner scanner)
    {
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.startsWith("#"))
                continue;
            return line;
        }
        return null;
    }
    
    private PSLProvider(PastebinEntry template, String[] parameters)
    {
        
    }
    
    @Override
    public Feature[] getReceiveFeatures()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Feature[] getSendFeatures()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Post read(String url)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String send(Post post)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean understands(String url)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
