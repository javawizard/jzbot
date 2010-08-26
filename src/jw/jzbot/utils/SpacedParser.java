package jw.jzbot.utils;

import java.util.ArrayList;
import java.util.Arrays;

import net.sf.opengroove.common.utils.StringUtils;

public class SpacedParser
{
    private int index;
    private ArrayList<String> previous = new ArrayList<String>();
    private ArrayList<String> tokens = new ArrayList<String>();
    
    public SpacedParser(String input)
    {
        tokens.addAll(Arrays.asList(input.split(" ")));
        if (tokens.size() == 1 && tokens.get(0).equals(""))
            tokens.clear();
    }
    
    public String next()
    {
        if (tokens.size() == 0)
            return null;
        String result = tokens.get(0);
        previous.add(result);
        return result;
    }
    
    public String observe()
    {
        if (tokens.size() == 0)
            return null;
        return tokens.get(0);
    }
    
    public void back()
    {
        tokens.add(0, previous.remove(previous.size() - 1));
    }
    
    public String remaining()
    {
        return StringUtils.delimited(tokens, " ");
    }
    
    public boolean empty()
    {
        return tokens.size() == 0;
    }
    
    public boolean more()
    {
        return !empty();
    }
}
