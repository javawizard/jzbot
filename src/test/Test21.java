package test;

import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.pastebin.Post;
import jw.jzbot.pastebin.providers.PDPastebin;

public class Test21
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        PastebinService.installProvider(new PDPastebin("pastebin.im", false, "index.php"));
         PastebinService
         .installProvider(new PDPastebin("pastebin.com", true, "pastebin.php"));
        Post post = PastebinService.readPost("http://pastebin.im/461");
        System.out.println(post.getData());
    }
    
}
