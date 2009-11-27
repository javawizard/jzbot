package jw.jzbot.fact.functions.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.JZBot;
import jw.jzbot.ResponseException;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

public class WeatherFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        Map<String, String> map = new HashMap<String, String>();
        String zipcode = arguments.resolveString(0);
        if (zipcode.length() != 5)
            throw new FactoidException("The zipcode you specified is \"" + zipcode
                    + "\". This isn't exactly 5 characters. Specify a zipcode "
                    + "that's exactly 5 characters. Canadians/Britans: "
                    + "sorry, but international weather isn't supported "
                    + "yet. If you'd like to modify the bot to support "
                    + "international weather, feel free to contact jcp on "
                    + "irc.freenode.net channel #bztraining.");
        String prefix = arguments.length() > 1 ? arguments.resolveString(1) : "";
        String weatherbugResultString = null;
        String yahooResultString = null;
        try
        {
            URL weatherbugUrl = new URL(
                    "http://a7686974884.isapi.wxbug.net/WxDataISAPI/WxDataISAPI.dll?Magic=10991&RegNum=0&ZipCode="
                            + zipcode.replace("&", "")
                            + "&Units=0&Version=7&Fore=0&t="
                            + Math.random());
            InputStream stream = weatherbugUrl.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringUtils.copy(stream, baos);
            stream.close();
            weatherbugResultString = new String(baos.toByteArray());
            String[] tokens = weatherbugResultString.split("\\|");
            map.put("date", tokens[2]);
            map.put("time", tokens[1]);
            map.put("temp", tokens[3]);
            map.put("winddir", tokens[4]);
            map.put("winddirhuman", windDegreesToReadable(tokens[4]));
            map.put("windspeed", tokens[5]);
            map.put("gustwdir", tokens[6]);
            map.put("gustwdirhuman", windDegreesToReadable(tokens[6]));
            map.put("gustwspeed", tokens[7]);
            map.put("raintoday", tokens[8]);
            map.put("rainrate", tokens[9]);
            String pressure = tokens[10];
            String pressureDir;
            if (pressure.endsWith("f"))
                pressureDir = "f";
            else if (pressure.endsWith("s"))
                pressureDir = "s";
            else
                pressureDir = "r";
            pressure = pressure.substring(0, pressure.length() - 1);
            map.put("pressure", pressure);
            map.put("pressuredir", pressureDir);
            map.put("humid", tokens[11]);
            map.put("hightemp", tokens[12]);
            map.put("lowtemp", tokens[13]);
            map.put("dewpoint", tokens[14]);
            map.put("windchill", tokens[15]);
            map.put("monthlyrain", tokens[16]);
            map.put("yearlyrain", tokens[31]);
            map.put("gusttime", tokens[25]);
            map.put("station", tokens[35]);
            map.put("citystate", tokens[36]);
            URL yahooUrl = new URL("http://weather.yahooapis.com/forecastrss?p="
                    + zipcode.replace("&", ""));
            stream = yahooUrl.openStream();
            baos = new ByteArrayOutputStream();
            StringUtils.copy(stream, baos);
            stream.close();
            String yahooResult = new String(baos.toByteArray());
            yahooResultString = yahooResult;
            String conditionStart = "yweather:condition  text=\"";
            int conditionsIndex = yahooResult.indexOf(conditionStart);
            String conditions = "";
            System.out.println("result:" + yahooResult + ",idx:" + conditionsIndex);
            if (conditionsIndex != -1)
            {
                int endIndex = yahooResult.indexOf("\"", conditionsIndex
                        + conditionStart.length() + 1);
                conditions = yahooResult.substring(conditionsIndex
                        + conditionStart.length(), endIndex);
            }
            map.put("conditions", conditions);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new FactoidException("Exception while parsing weather data: "
                    + e.getClass().getName() + ": " + e.getMessage()
                    + "\nWeatherbug data: " + weatherbugResultString + "\nYahoo data: "
                    + yahooResultString, e);
        }
        for (String s : map.keySet())
        {
            context.getLocalVars().put(prefix + s, map.get(s));
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{weather||<zipcode>||<prefix>}} -- Gets information on the current "
                + "weather conditions for <zipcode> by contacting the WeatherBug and Yahoo "
                + "servers. Weather information is then placed in a number of different local "
                + "variables, each prefixed with <prefix> to avoid conflicting variable names. "
                + "<prefix> is optional, and will default to nothing (IE no prefix) if not present.";
    }
    
    private static final String[] WIND_DIRECTIONS = new String[]
    {
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W",
            "WNW", "NW", "NNW"
    };
    
    private String windDegreesToReadable(String string)
    {
        double windDegrees = Double.parseDouble(string);
        double sliceSize = 360.0 / (WIND_DIRECTIONS.length * 1.0d);
        double halfSlice = sliceSize / 2.0d;
        windDegrees += halfSlice;
        for (int i = 0; i < WIND_DIRECTIONS.length; i++)
        {
            if (windDegrees >= (i * sliceSize) && windDegrees <= ((i + 1) * sliceSize))
                return WIND_DIRECTIONS[i];
            
        }
        return ("*winddirhuman-" + windDegrees + "-" + sliceSize + "-" + halfSlice);
    }
    
}
