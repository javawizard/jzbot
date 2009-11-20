package jw.jzbot.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import jw.jzbot.pastebin.PastebinService;
import jw.jzbot.pastebin.Post;
import jw.jzbot.pastebin.PastebinProvider.Feature;

import net.sf.opengroove.common.utils.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Contains methods for creating, reading, and deleting posts from <a
 * href="http://pastebin.com">pastebin.com</a>. JZBot uses this to provide error reports
 * (when an exception occurs, JZBot pastebins the stack trace and then sends a message to
 * the source of the command, containing the error), among various other things.
 * 
 * @deprecated This class has been replaced by {@link PastebinService}, and all methods in
 *             this class delegate to PastebinService methods. This class only allowed
 *             sending posts to pastebin.com, but PastebinService allows for different
 *             pastebin providers to be registered.
 * @author Alexander Boyd
 * 
 */
public class Pastebin
{
    private static final int MAX_READ_LENGTH = 1024 * 800;
    
    public static enum Duration
    {
        DAY, MONTH, FOREVER
    }
    
    public static String createPost(String poster, String data, Duration duration,
            String parent, Feature[] features)
    {
        Post post = new Post();
        post.setName(poster);
        post.setData(data);
        post.setDuration(duration);
        post.setParent(parent);
        return PastebinService.createPost(post, features);
    }
    
    /*
     * Creates a new pastebin post.
     * 
     * @param poster The poster name to use
     * 
     * @param post The message of the post. Any lines that start with @@ will have this
     * removed and will be highlighted in the final paste.
     * 
     * @param duration How long the post should last for
     * 
     * @param parent The id of the post that this one is in reply to, or null or the empty
     * string if this is a new post, not a reply
     * 
     * @return The id of the post. This can be appended to "http://pastebin.com/" to
     * obtain a url that can be used to view the post.
     * 
     * public static String createPost(String poster, String post, Duration duration,
     * String parent) { System.out.println("Creating pastebin post with text:");
     * System.out.println(post); System.out.println("---------------------------------");
     * if (post.equals("")) throw new IllegalArgumentException(
     * "You can't create a pastebin post with no text in it"); if (parent == null) parent
     * = ""; try { HttpClient client = new DefaultHttpClient();
     * client.getParams().setParameter("http.socket.timeout", 8000); HttpPost request =
     * new HttpPost("http://pastebin.com/pastebin.php"); request.addHeader("Content-type",
     * "application/x-www-form-urlencoded"); request.setEntity(new
     * StringEntity("parent_pid=" + URLEncoder.encode(parent) + "&format=text&code2=" +
     * URLEncoder.encode(post, "US-ASCII") + "&poster=" + URLEncoder.encode(poster) +
     * "&paste=Send&remember=1&expiry=" + duration.toString().substring(0,
     * 1).toLowerCase() + "&email=")); HttpResponse response = client.execute(request);
     * int responseCode = response.getStatusLine().getStatusCode(); if (responseCode !=
     * 302) throw new RuntimeException("Received response code " + responseCode +
     * " from pastebin (302 should have been sent instead): " +
     * response.getStatusLine().getReasonPhrase() + " with content " +
     * readContent(response)); String newUrl = getResponseHeader(response, "Location"); if
     * (!newUrl.startsWith("http://pastebin.com/")) throw new
     * RuntimeException("Invalid url prefix: " + newUrl); return
     * newUrl.substring("http://pastebin.com/".length()); } catch (Exception e) { throw
     * new RuntimeException(e.getClass().getName() + " " + e.getMessage(), e); } }
     */

    public static String getResponseHeader(HttpResponse response, String string)
    {
        for (Header header : response.getAllHeaders())
            if (header.getName().equals(string))
                return header.getValue();
        return null;
    }
    
    public static String readContent(HttpResponse response)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            response.getEntity().writeTo(baos);
            return "\"" + new String(baos.toByteArray()) + "\"";
        }
        catch (Exception e)
        {
            return "Content error: " + e.getClass().getName() + ": " + e.getMessage();
        }
    }
    
    /**
     * Returns the content of the post at the specified url. The post is handed back from
     * pastebin.com with html entities and such to prevent the code from messing up the
     * page; this method properly resolves these back into actual characters so that using
     * {@link #createPost(String, String, Duration, String)} with the exact content
     * returned from this method would result in two posts that are identical (ignoring
     * the sequences of 2 at signs that can be used for highlighting).
     * 
     * @param postUrl
     *            The url of the post
     * @return The text of the specified post
     */
    public static String readPost(String postUrl)
    {
        try
        {
            if (!postUrl.startsWith("http://pastebin.com/"))
                throw new RuntimeException(
                        "Invalid url, needs to start with \"http://pastebin.com/\": "
                                + postUrl);
            postUrl = postUrl.substring("http://pastebin.com/".length());
            postUrl = "http://pastebin.com/pastebin.php?dl=" + postUrl;
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.socket.timeout", 8000);
            HttpGet request = new HttpGet(postUrl);
            HttpResponse response = client.execute(request);
            InputStream stream = response.getEntity().getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int read = 0;
            while ((read = stream.read(buffer)) != -1)
            {
                if (read > MAX_READ_LENGTH)
                    throw new RuntimeException("Too many characters read (max is "
                            + MAX_READ_LENGTH + ")");
                out.write(buffer, 0, read);
            }
            stream.close();
            out.flush();
            out.close();
            String result = new String(out.toByteArray(), "US-ASCII");
            return result;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static String readUrlForOkAndGetResponse(String url, String method, String entity)
    {
        try
        {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.socket.timeout", 8000);
            HttpRequestBase request = (method.equalsIgnoreCase("get") ? new HttpGet(url)
                    : new HttpPost(url));
            if (method.equals("post") && entity != null)
                ((HttpPost) request).setEntity(new StringEntity(entity));
            if (method.equals("post"))
                request.addHeader("Content-type", "application/x-www-form-urlencoded");
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException("Response code other than 200: "
                        + response.getStatusLine().getStatusCode() + " "
                        + response.getStatusLine().getReasonPhrase());
            InputStream stream = response.getEntity().getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StringUtils.copy(stream, out);
            String result = new String(out.toByteArray(), "US-ASCII");
            return result;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
