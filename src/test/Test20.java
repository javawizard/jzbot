package test;

import jw.jzbot.pastebin.providers.PDPastebin;

public class Test20
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        PDPastebin pastebin = new PDPastebin("pastebin.com", true, "", true);
        System.out.println(pastebin.getPostPrefixRegex());
        String url = "http://pastebin.com/d83j83h2";
        System.out.println(pastebin.understands(url));
        System.out.println(pastebin.extractId(url));
    }
    
}
