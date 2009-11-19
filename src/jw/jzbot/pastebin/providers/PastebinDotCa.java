package jw.jzbot.pastebin.providers;

import jw.jzbot.pastebin.PastebinProvider;
import jw.jzbot.pastebin.Post;

public class PastebinDotCa implements PastebinProvider
{
    
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
                Feature.author, Feature.forever, Feature.info, Feature.tags
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
        return url.startsWith("http://pastebin.ca");
    }
    
}
