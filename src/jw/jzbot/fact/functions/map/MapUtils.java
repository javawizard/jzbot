package jw.jzbot.fact.functions.map;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    
    @SuppressWarnings("unchecked")
    public static String encode(Map<String, String> map)
    {
        List<Map.Entry<String, String>> entries =
                new ArrayList<Map.Entry<String, String>>(map.entrySet());
        String[] results = new String[entries.size()];
        for (int i = 0; i < results.length; i++)
        {
            results[i] =
                    URLEncoder.encode(entries.get(i).getKey()) + "="
                        + URLEncoder.encode(entries.get(i).getValue());
        }
        return StringUtils.delimited(results, "#");
    }
}
