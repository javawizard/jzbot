package org.opengroove.jzbot.fact.functions;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import net.sf.opengroove.common.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opengroove.jzbot.fact.ArgumentList;
import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactoidException;
import org.opengroove.jzbot.fact.Function;

public class GoogleFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        try
        {
            ArrayList<String> resultList = new ArrayList<String>();
            URL url = new URL(
                    "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q="
                            + URLEncoder.encode(arguments.get(0)));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            URLConnection con = url.openConnection();
            con.addRequestProperty("Referer", "http://jzbot.opengroove.org/");
            InputStream in = con.getInputStream();
            StringUtils.copy(in, baos);
            in.close();
            System.out.println(new String(baos.toByteArray()));
            JSONObject j = new JSONObject(new String(baos.toByteArray()));
            JSONObject responseDataObject = j.getJSONObject("responseData");
            JSONArray resultsObject = responseDataObject
                    .getJSONArray("results");
            for (int i = 0; i < resultsObject.length() && i < 5; i++)
            {
                JSONObject result = resultsObject.getJSONObject(i);
                String resultText = result.getString("url")
                        + " "
                        + result.getString("title").replace("<b>", "").replace(
                                "</b>", "").replace("|", "").replace("\n", "");
                resultList.add(resultText);
            }
            return StringUtils
                    .delimited(resultList.toArray(new String[0]), "|");
        }
        catch (Exception e)
        {
            throw new FactoidException("Exception while reading from Google: "
                    + e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {{google||<search>}} -- Uses the Google search engine to search "
                + "for <search>, and returns the results with each result separated with "
                + "a \"|\" character. Each result is made up of the URL for that result, "
                + "a space, and a summary of that result.";
    }
    
    @Override
    public String getName()
    {
        return "google";
    }
    
}
