package jw.jzbot.pastebin.providers;

import jw.jzbot.pastebin.PastebinProvider;
import jw.jzbot.pastebin.Post;
import net.sf.opengroove.common.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aboyd on 2014-12-21.
 */
public class StaticDirectory implements PastebinProvider {
    private String urlPrefix;
    private File directory;
    private Random random;

    public StaticDirectory(String urlPrefix, File directory) {
        this.urlPrefix = urlPrefix;
        this.directory = directory;
        if (!this.directory.exists())
            this.directory.mkdirs();
        this.random = new Random();
    }

    @Override
    public Feature[] getSendFeatures() {
        return new Feature[]{Feature.forever};
    }

    @Override
    public Feature[] getReceiveFeatures() {
        return new Feature[0];
    }

    @Override
    public String send(Post post) {
        // Poor man's random identifier
        String id = Math.abs(random.nextLong()) + "" + Math.abs(random.nextLong());
        StringUtils.writeFile(post.getData(), new File(directory, id));
        return urlPrefix + id;
    }

    @Override
    public Post read(String url) {
        return null;
    }

    @Override
    public boolean understands(String url) {
        return false;
    }
}
