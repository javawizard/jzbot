package jw.boardforj;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The entry point into the BoardForJ API. An instance of this class represents access to
 * the 100-hour board.
 * 
 * @author Alexander Boyd
 * 
 */
public class TheBoard
{
    public static final String DEFAULT_URL = "http://theboard.byu.edu/";
    private static final String ACCESS_URL = "content.php?area=posts";
    private String baseUrl;
    
    /**
     * Creates a new TheBoard object that connects to the default 100-hour board,
     * <tt>http://theboard.byu.edu</tt>. This is the same as
     * <tt>new TheBoard(TheBoard.DEFAULT_URL)</tt>.
     */
    public TheBoard()
    {
        this(DEFAULT_URL);
    }
    
    /**
     * Creates a new TheBoard object that connects to the 100-hour board at the URL
     * specified. Most people will generally want to use {@link #TheBoard()} instead; this
     * constructor primarily exists to allow for testing of this class while not connected
     * to the internet.
     * 
     * @param url
     *            The url of the board
     */
    public TheBoard(String url)
    {
        if (!url.endsWith("/"))
            url = url + "/";
        this.baseUrl = url;
    }
    
    /**
     * Returns the most recent date that the board has posts for.
     * 
     * @return
     */
    public NormalDate getToday()
    {
        JSONObject object = query("today=1");
        NormalDate date = new NormalDate();
        date.setNormalYear(object.getInt("year"));
        date.setNormalMonth(object.getInt("month"));
        date.setDate(object.getInt("date"));
        return date;
    }
    
    /**
     * Returns a list of all years that have posts.
     * 
     * @return
     */
    public NormalDate[] getYears()
    {
        JSONArray list = query("findDates=1").getJSONArray("years");
        NormalDate[] dates = new NormalDate[list.length()];
        for (int i = 0; i < dates.length; i++)
        {
            NormalDate date = new NormalDate();
            date.setNormalYear(list.getInt(i));
            dates[i] = date;
        }
        return dates;
    }
    
    /**
     * Returns a list of all months within the specified year that have posts.
     * 
     * @return
     */
    public NormalDate[] getMonths(NormalDate year)
    {
        JSONArray list =
                query("findDates=1&year=" + year.getNormalYear()).getJSONArray("months");
        NormalDate[] dates = new NormalDate[list.length()];
        for (int i = 0; i < dates.length; i++)
        {
            NormalDate date = new NormalDate();
            date.setNormalYear(year.getNormalYear());
            date.setNormalMonth(list.getInt(i));
            dates[i] = date;
        }
        return dates;
    }
    
    /**
     * Returns a list of all days within the specified month that have posts.
     * 
     * @return
     */
    public NormalDate[] getDays(NormalDate month)
    {
        JSONArray list =
                query(
                        "findDates=1&year=" + month.getNormalYear() + "&month="
                            + month.getNormalMonth()).getJSONArray("days");
        NormalDate[] dates = new NormalDate[list.length()];
        for (int i = 0; i < dates.length; i++)
        {
            NormalDate date = new NormalDate();
            date.setNormalYear(month.getNormalYear());
            date.setNormalMonth(month.getNormalMonth());
            date.setDate(list.getInt(i));
            dates[i] = date;
        }
        return dates;
    }
    
    /**
     * Returns a list of the ids of all of the posts made on the specified day. The ids
     * are not in any particular order.
     * 
     * @param day
     * @return
     */
    public int[] getPostIds(NormalDate day)
    {
        JSONArray list =
                (JSONArray) queryForArray("date=" + day.getNormalYear() + "-"
                    + day.getNormalMonth() + "-" + day.getDate());
        int[] ids = new int[list.length()];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = list.getInt(i);
        }
        return ids;
    }
    
    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Returns the post with the specified id.
     * 
     * @param id
     * @return
     */
    public Post getPost(int id)
    {
        JSONObject object = query("id=" + id);
        JSONObject question = object.getJSONObject("question");
        Post post = new Post();
        post.id = question.getInt("id");
        post.text = question.getString("text");
        try
        {
            post.date = new NormalDate(dateFormat.parse(question.getString("postTime")));
        }
        catch (ParseException e)
        {
            // Again with the stupid checked exceptions...
            throw new RuntimeException("Exception occurred while parsing date "
                + question.getString("postTime"), e);
        }
        post.categories = new ArrayList<String>();
        for (int i = 1; i < 100; i++)// in case they add more categories than just the
        // three that can be present right now
        {
            String key = "cat" + i;
            if (!question.has(key))
                break;// continue? I'm assuming they won't use non-contiguous ids, which
            // is why we're breaking instead of continuing, but...
            String value = question.getString(key);
            if (!value.trim().equals(""))
                post.categories.add(value);
        }
        if (object.has("responses"))
        {
            JSONArray responseList = object.getJSONArray("responses");
            post.responses = new Response[responseList.length()];
            for (int i = 0; i < post.responses.length; i++)
            {
                Response response = new Response();
                post.responses[i] = response;
                JSONObject responseObject = responseList.getJSONObject(i);
                response.text = responseObject.getString("text");
                response.alias = responseObject.getString("alias");
            }
        }
        else
        {
            post.responses = new Response[0];
        }
        return post;
    }
    
    private JSONArray queryForArray(String params)
    {
        try
        {
            return new JSONArray(queryForString(params));
        }
        catch (Exception e)// IOException and JSONException, and I don't really care if it
        // catches everything else, things will still work
        {
            throw new RuntimeException("Exception occurred while calling parameter set "
                + params, e);
        }
    }
    
    private JSONObject query(String params)
    {
        try
        {
            return new JSONObject(queryForString(params));
        }
        catch (Exception e)// IOException and JSONException, and I don't really care if it
        // catches everything else, things will still work
        {
            throw new RuntimeException("Exception occurred while calling parameter set "
                + params, e);
        }
    }
    
    private String queryForString(String params) throws Exception
    {
        String urlString = baseUrl + ACCESS_URL;
        if (params != null && !params.equals(""))
            urlString += "&" + params;
        URL url = new URL(urlString);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        byte[] buffer = new byte[256];
        int amount;
        while ((amount = in.read(buffer)) != -1)
            baos.write(buffer, 0, amount);
        in.close();
        return new String(baos.toByteArray());
    }
    
    private String encode(String text)
    {
        return URLEncoder.encode(text);
    }
    
}