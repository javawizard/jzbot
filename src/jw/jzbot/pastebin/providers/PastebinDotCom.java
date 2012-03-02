package jw.jzbot.pastebin.providers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;

import net.sf.opengroove.common.utils.StringUtils;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import jw.jzbot.pastebin.PastebinProvider;
import jw.jzbot.pastebin.Post;
import jw.jzbot.utils.Pastebin;

public class PastebinDotCom implements PastebinProvider {
    private static final File apiKeyFile = new File(
            "storage/pastebin.com-api-key.txt");
    
    @Override
    public Feature[] getSendFeatures() {
        return new Feature[] { Feature.forever, Feature.author,
                Feature.highlight };
    }
    
    @Override
    public Feature[] getReceiveFeatures() {
        return new Feature[] {};
    }
    
    @Override
    public String send(Post post) {
        try {
            if (!apiKeyFile.exists())
                throw new RuntimeException(
                        "The API key file ("
                                + apiKeyFile.getPath()
                                + ") does not exist. You need to copy your pastebin.com "
                                + "API key into that file, then try again.");
            String apiKey = FileUtils.readFileToString(apiKeyFile)
                    .replace("\n", "").replace("\r", "").replace(" ", "");
            String data = post.getData();
            data = data.replaceAll("(?m:^@@)", "@h@");
            // FIXME: Change this to take into account the paste duration
            String result = Pastebin.readUrlForOkAndGetResponse(
                    "http://pastebin.com/api/api_post.php",
                    "post",
                    "api_dev_key=" + apiKey
                            + "&api_option=paste&api_paste_code="
                            + URLEncoder.encode(data, "UTF-8")
                            + "&api_paste_name="
                            + URLEncoder.encode(post.getName(), "UTF-8")
                            + "&api_paste_expire_date=1D");
            if (result.startsWith("http"))
                return result;
            throw new RuntimeException("pastebin.com reported an error: "
                    + result);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception while trying to post to pastebin.com", e);
        }
    }
    
    @Override
    public Post read(String url) {
        url = url.replaceFirst("https?://(www.)?pastebin.com/", "");
        return new Post(null, null, null, null, null,
                Pastebin.readUrlForOkAndGetResponse(
                        "http://pastebin.com/raw.php?i=" + url, "get", null));
    }
    
    @Override
    public boolean understands(String url) {
        return url.matches("^https?://(www.)?pastebin.com/.+$");
    }
    
}
