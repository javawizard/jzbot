package jw.jzbot.fact.functions.map;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.opengroove.common.utils.StringUtils;
import net.sf.opengroove.common.utils.StringUtils.ToString;

public class MapUtils
{
    // "#" = entry separator, "=" = key/value separator
    public static Map<String, String> decode(String text)
    {
        Map<String, String> map = new HashMap<String, String>();
        for (String entry : text.split("#"))
        {
            if (!entry.trim().equals(""))
            {
                String[] tokens = entry.split("=", 2);
                map.put(URLDecoder.decode(tokens[0]), URLDecoder.decode(tokens[1]));
            }
        }
        return map;
    }
    
    public static String encode(Map<String, String> map)
    {
        return StringUtils.delimited(
                map.entrySet().<Map.Entry<String, String>> toArray(new Map.Entry[0]),
                new ToString<Map.Entry<String, String>>()
                {
                    
                    @Override
                    public String toString(Entry<String, String> entry)
                    {
                        return URLEncoder.encode(entry.getKey()) + "="
                            + URLEncoder.encode(entry.getValue());
                    }
                }, "#");
    }
}
