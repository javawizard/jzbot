package org.opengroove.jzbot.commands;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sf.opengroove.common.utils.StringUtils;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.opengroove.jzbot.Command;
import org.opengroove.jzbot.JZBot;
import org.opengroove.jzbot.ResponseException;
import org.opengroove.jzbot.storage.Factoid;

public class WeatherCommand implements Command
{
    // yahoo: http://weather.yahooapis.com/forecastrss?p=94089
    // weatherbug: A7686974884
    
    public String getName()
    {
        return "weather";
    }
    
    public void run(String channel, boolean pm, String sender, String hostname,
            String arguments)
    {
        if (arguments.equals(""))
            throw new ResponseException(
                    "You need to specify a zip code. For example, ~weather 12345. Developers: you can run ~weather 12345 props");
        boolean propsShown = false;
        if (arguments.endsWith("props"))
        {
            JZBot.bot
                    .sendMessage(
                            pm ? sender : channel,
                            "http://code.google.com/p/jwutils/source/browse/trunk/"
                                    + "projects/jzbot/src/org/opengroove/jzbot/commands/"
                                    + "WeatherCommand.java for command list");
            propsShown = true;
            arguments = arguments.substring(0, arguments.length() - 5);
        }
        arguments = arguments.trim();
        Factoid weatherFactoid = null;
        if (channel != null)
            weatherFactoid = JZBot.storage.getChannel(channel).getFactoid(
                    "weathertemplate");
        if (weatherFactoid == null)
            weatherFactoid = JZBot.storage.getFactoid("weathertemplate");
        if (weatherFactoid == null)
            throw new ResponseException(
                    "The weathertemplate factoid does not exist.");
        Map<String, String> map = new TreeMap<String, String>();
        try
        {
            URL weatherbugUrl = new URL(
                    "http://a7686974884.isapi.wxbug.net/WxDataISAPI/WxDataISAPI.dll?Magic=10991&RegNum=0&ZipCode="
                            + arguments.replace("&", "")
                            + "&Units=0&Version=7&Fore=0&t=" + Math.random());
            InputStream stream = weatherbugUrl.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringUtils.copy(stream, baos);
            stream.close();
            String[] tokens = new String(baos.toByteArray()).split("\\|");
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
            URL yahooUrl = new URL(
                    "http://weather.yahooapis.com/forecastrss?p="
                            + arguments.replace("&", ""));
            stream = yahooUrl.openStream();
            baos = new ByteArrayOutputStream();
            StringUtils.copy(stream, baos);
            stream.close();
            String yahooResult = new String(baos.toByteArray());
            String conditionStart = "yweather:condition  text=\"";
            int conditionsIndex = yahooResult.indexOf(conditionStart);
            String conditions = "";
            System.out.println("result:" + yahooResult + ",idx:"
                    + conditionsIndex);
            if (conditionsIndex != -1)
            {
                int endIndex = yahooResult.indexOf("\"", conditionsIndex
                        + conditionStart.length() + 1);
                conditions = yahooResult.substring(conditionsIndex
                        + conditionStart.length(), endIndex);
            }
            map.put("conditions", conditions);
            if (propsShown)
            {
                String currentList = "";
                for (String name : map.keySet())
                {
                    currentList += name + "=" + map.get(name) + "          ";
                    if (currentList.length() > 200)
                    {
                        JZBot.bot.sendMessage(pm ? sender : channel,
                                currentList);
                        currentList = "";
                    }
                }
                if (!currentList.equals(""))
                    JZBot.bot.sendMessage(pm ? sender : channel, currentList);
                JZBot.bot.sendMessage(pm ? sender : channel,
                        "End weather properties list");
            }
            String result = JZBot.runFactoid(weatherFactoid, channel, sender,
                    new String[0], map, true);
            JZBot.bot.sendMessage(pm ? sender : channel, result);
        }
        catch (Exception e)
        {
            if (e instanceof ResponseException)
                throw (ResponseException) e;
            e.printStackTrace();
            throw new RuntimeException(e.getClass().getName() + ": "
                    + e.getMessage(), e);
        }
    }
    
    private static final String[] WIND_DIRECTIONS = new String[]
    {
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW",
            "WSW", "W", "WNW", "NW", "NNW"
    };
    
    private String windDegreesToReadable(String string)
    {
        double windDegrees = Double.parseDouble(string);
        double sliceSize = 360.0 / (WIND_DIRECTIONS.length * 1.0d);
        double halfSlice = sliceSize / 2.0d;
        windDegrees += halfSlice;
        for (int i = 0; i < WIND_DIRECTIONS.length; i++)
        {
            if (windDegrees >= (i * sliceSize)
                    && windDegrees <= ((i + 1) * sliceSize))
                return WIND_DIRECTIONS[i];
            
        }
        throw new RuntimeException("slice size:" + sliceSize + ",degrees:"
                + windDegrees + ",halfSlice:" + halfSlice);
    }
}
