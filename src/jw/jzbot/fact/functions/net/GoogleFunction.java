package jw.jzbot.fact.functions.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactoidException;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;

import net.sf.opengroove.common.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        try
        {
            ArrayList<String> resultList = new ArrayList<String>();
            URL url = new URL(
                    "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q="
                            + URLEncoder.encode(arguments.resolveString(0)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            URLConnection con = url.openConnection();
            con.addRequestProperty("Referer", "http://jzbot.opengroove.org/");
            InputStream in = con.getInputStream();
            StringUtils.copy(in, baos);
            in.close();
            System.out.println(new String(baos.toByteArray()));
            JSONObject j = new JSONObject(new String(baos.toByteArray()));
            JSONObject responseDataObject = j.getJSONObject("responseData");
            JSONArray resultsObject = responseDataObject.getJSONArray("results");
            for (int i = 0; i < resultsObject.length() && i < 5; i++)
            {
                JSONObject result = resultsObject.getJSONObject(i);
                String resultText = result.getString("url")
                        + " "
                        + result.getString("title").replace("<b>", "").replace("</b>", "")
                                .replace("|", "").replace("\n", "");
                resultList.add(resultText);
            }
            DelimitedSink result = new DelimitedSink(sink, "|");
            for (String s : resultList)
            {
                result.next();
                result.write(s);
            }
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while reading from Google: " + e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {google|<search>} -- Uses the Google search engine to search "
                + "for <search>, and returns the results with each result separated with "
                + "a \"|\" character. Each result is made up of the URL for that result, "
                + "a space, and a summary of that result.";
    }
    
    public String getName()
    {
        return "google";
    }
    
}
