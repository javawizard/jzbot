package org.opengroove.jzbot.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Contains methods for creating, reading, and deleting posts from <a
 * href="http://pastebin.com">pastebin.com</a>. JZBot uses this to provide error
 * reports (when an exception occurs, JZBot pastebins the stack trace and then
 * sends a message to the source of the command, containing the error), among
 * various other things.
 * 
 * @author Alexander Boyd
 * 
 */
public class Pastebin
{
    private static final int MAX_READ_LENGTH = 1024 * 200;
    
    public static enum Duration
    {
        DAY, MONTH, FOREVER
    }
    
    /**
     * Creates a new pastebin post.
     * 
     * @param poster
     *            The poster name to use
     * @param post
     *            The message of the post. Any lines that start with @@ will
     *            have this removed and will be highlighted in the final paste.
     * @param duration
     *            How long the post should last for
     * @param parent
     *            The id of the post that this one is in reply to, or null or
     *            the empty string if this is a new post, not a reply
     * @return The id of the post. This can be appended to
     *         "http://pastebin.com/" to obtain a url that can be used to view
     *         the post.
     */
    public static String createPost(String poster, String post,
            Duration duration, String parent)
    {
        if (parent == null)
            parent = "";
        try
        {
            URL url = new URL("http://pastebin.com/pastebin.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            con.addRequestProperty("Content-type",
                    "application/x-www-form-urlencoded");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();
            out
                    .write(("parent_pid=" + URLEncoder.encode(parent)
                            + "&format=text&code2="
                            + URLEncoder.encode(post, "US-ASCII") + "&poster="
                            + URLEncoder.encode(poster)
                            + "&paste=Send&remember=1&expiry="
                            + duration.toString().substring(0, 1).toLowerCase() + "&email=")
                            .getBytes());
            out.flush();
            out.close();
            int responseCode = con.getResponseCode();
            if (responseCode != 302)
                throw new RuntimeException("Received response code "
                        + responseCode + " from pastebin: "
                        + con.getResponseMessage());
            String newUrl = con.getHeaderField("Location");
            if (!newUrl.startsWith("http://pastebin.com/"))
                throw new RuntimeException("Invalid url prefix: " + newUrl);
            return newUrl.substring("http://pastebin.com/".length());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getClass().getName() + " "
                    + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the content of the post at the specified url. The post is handed
     * back from pastebin.com with html entities and such to prevent the code
     * from messing up the page; this method properly resolves these back into
     * actual characters so that using
     * {@link #createPost(String, String, Duration, String)} with the exact
     * content returned from this method would result in two posts that are
     * identical (ignoring the sequences of 2 at signs that can be used for
     * highlighting).
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
            URL url = new URL(postUrl);
            InputStream stream = url.openStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int read = 0;
            while ((read = stream.read(buffer)) != -1)
            {
                if (read > MAX_READ_LENGTH)
                    throw new RuntimeException(
                            "Too many characters read (max is "
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
    
}
