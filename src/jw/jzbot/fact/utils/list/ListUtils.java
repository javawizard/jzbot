package jw.jzbot.fact.utils.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtils {
    /*
     * The empty string is a list of length 0. The list format is a series of
     * digits representing a number which is the number of items in the list,
     * followed by a hyphen, followed by the encoding of each item. Each item is
     * encoded by placing the length of that item, as a series of digits,
     * followed by a hyphen, followed by that many characters.
     * 
     * Dictionaries are encoded using the list encoding algorithm, but every
     * even-numbered item (items start at 0) is a key and every odd-numbered
     * item is the corresponding value.
     */
    public static ArrayList<String> parseList(String text) {
        ArrayList<String> list = new ArrayList<String>();
        if (text.equals(""))
            return list;
        if (text.charAt(0) != 'l')
            throw new RuntimeException("First char of a list must always be l");
        text = text.substring(1);
        int hyphen = text.indexOf("-");
        int length = Integer.parseInt(text.substring(0, hyphen));
        text = text.substring(hyphen + 1);
        for (int i = 0; i < length; i++) {
            hyphen = text.indexOf("-");
            int itemLength = Integer.parseInt(text.substring(0, hyphen));
            String itemText = text.substring(hyphen + 1, hyphen + 1
                    + itemLength);
            text = text.substring(hyphen + 1 + itemLength);
            list.add(itemText);
        }
        return list;
    }
    
    public static String formatList(List<String> list) {
        StringBuilder result = new StringBuilder();
        result.append("l");
        result.append(list.size());
        result.append("-");
        for (String s : list) {
            result.append(s.length());
            result.append("-");
            result.append(s);
        }
        return result.toString();
    }
    
    public static HashMap<String, String> parseMap(String text) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (text.equals(""))
            return map;
        if (text.charAt(0) != 'd' && text.charAt(0) != 'm')
            throw new RuntimeException(
                    "First char of a dictionary must be d or m");
        List<String> list = parseList(text.substring(1));
        for (int i = 0; i < list.size(); i += 2) {
            map.put(list.get(i), list.get(i + 1));
        }
        return map;
    }
    
    public static String formatMap(Map<String, String> map) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            list.add(e.getKey());
            list.add(e.getValue());
        }
        return "d" + formatList(list);
    }
}
