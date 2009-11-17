package jw.jzbot.fact.functions;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.functions.text.PadFunction;
import net.sf.opengroove.common.security.Hash;


public class HashFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        return doHash(arguments.get(0));
    }
    
    public static String doHash(String s)
    {
        String hash = Hash.hash(s);
        hash = hash.replace("-", "").replace(".", "");
        hash = PadFunction.pad(32, "0", hash);
        hash = hash.substring(0, 32);
        return hash;
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{hash||<text>}} -- Computes a 32-character hash of the text "
                + "specified. Specifically, the hash is the first 32 characters of a "
                + "signed base-16 conversion of the bytes of the SHA-512 hash of <text>. "
                + "The resulting text is guaranteed to be exactly 32 characters in length.";
    }
    
}
