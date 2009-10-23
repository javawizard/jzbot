package test;

import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opengroove.jzbot.utils.Pastebin;

public class Test13
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.socket.timeout", 8000);
        HttpPost post = new HttpPost("http://localhost:3432/pastebin.php");
        post.addHeader("Content-type", "application/x-www-form-urlencoded");
        post.setEntity(new StringEntity("parent_pid=" + URLEncoder.encode("")
                + "&format=text&code2="
                + URLEncoder.encode("Hello, this is a test post.", "US-ASCII") + "&poster="
                + URLEncoder.encode("testpost") + "&paste=Send&remember=1&expiry="
                + Pastebin.Duration.DAY.toString().substring(0, 1).toLowerCase()
                + "&email="));
        HttpResponse response = client.execute(post);
        System.out.println(response.getStatusLine().toString());
        for (Header header : response.getAllHeaders())
        {
            System.out.println(header.getName() + ": " + header.getValue());
        }
        System.out.println("-----");
        response.getEntity().writeTo(System.out);
    }
    
}
