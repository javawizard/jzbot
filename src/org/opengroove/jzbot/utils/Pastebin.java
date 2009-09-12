package org.opengroove.jzbot.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Contains methods for creating, reading, and deleting posts from <a
 * href="http://pastebin.com">pastebin.com</a>. JZBot uses this to provide error
 * reports (when an exception occurs, JZBot pastebins the stack trace and then
 * sends a message to the source of the command, containing the error). JZBot
 * also uses this to allow editing of logo scripts, by pastebinning a procedure
 * when "proc edit" is run, and then saving the contents of the pastebin when
 * "proc save" is run.
 * 
 * @author Alexander Boyd
 * 
 */
public class Pastebin
{
    public static enum Duration
    {
        DAY, MONTH, YEAR
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
    public String createPost(String poster, String post, Duration duration,
        String parent)
    {
        if (parent == null)
            parent = "";
        try
        {
            URL url = new URL("http://pastebin.com/pastebin.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            con.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();
            out.write(("parent_pid=" + URLEncoder.encode(parent)
                + "&format=text&code2=" + URLEncoder.encode(post) + "&poster="
                + URLEncoder.encode(poster) + "&paste=Send&remember=1&expiry="
                + duration.toString().substring(0, 1).toLowerCase() + "&email=")
                .getBytes());
            out.flush();
            out.close();
            int responseCode = con.getResponseCode();
            if (responseCode != 302)
                throw new RuntimeException("Received response code " + responseCode
                    + " from pastebin: " + con.getResponseMessage());
            String newUrl = con.getHeaderField("Location");
            if (!newUrl.startsWith("http://pastebin.com/"))
                throw new RuntimeException("Invalid url prefix: " + newUrl);
            return newUrl.substring("http://pastebin.com/".length());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getClass().getName() + " " + e.getMessage(), e);
        }
    }
}
