package jw.jzbot.pastebin.providers;

import jw.jzbot.pastebin.PastebinProvider;
import jw.jzbot.pastebin.Post;

public class PastebinDotCa implements PastebinProvider
{
    public static final String API_KEY = "Tfp0JjFZhgr7VN81MvcVhVLEr3sI7nlT";
    
    @Override
    public Feature[] getReceiveFeatures()
    {
        return new Feature[]
        {};
    }
    
    @Override
    public Feature[] getSendFeatures()
    {
        return new Feature[]
        {
                Feature.author, Feature.forever, Feature.info
        };
    }
    
    @Override
    public Post read(String url)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String send(Post post)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean understands(String url)
    {
        return url.startsWith("http://pastebin.ca/");
    }
    
}
