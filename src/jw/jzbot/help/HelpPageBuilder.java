package jw.jzbot.help;

import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * Created by aboyd on 2016-03-28.
 */
public class HelpPageBuilder {
    private String[] path;
    private String content;
    private Map<String, HelpPage> children = new HashMap<>();
    private List<HelpPage> mergedPages = new ArrayList<>();

    public HelpPageBuilder() {
        this(new String[0]);
    }

    public HelpPageBuilder(String[] path) {
        this.path = path;
    }

    public HelpPageBuilder content(String content) {
        this.content = content;
        return this;
    }

    public HelpPageBuilder child(String name, HelpPage page) {
        this.children.put(name, page);
        return this;
    }

    public HelpPageBuilder merge(HelpPage page) {
        this.mergedPages.add(page);
        return this;
    }

    public HelpPage build() {
        List<HelpPage> pages = new ArrayList<HelpPage>();

        if (content != null || children.size() > 0) {
            pages.add(new DefaultHelpPage(this.content, this.children));
        }

        pages.addAll(mergedPages);

        HelpPage page;
        if (pages.size() == 1) {
            page = pages.get(0);
        } else {
            page = new MergedHelpPage(pages);
        }

        for (int i = path.length - 1; i >= 0; i--) {
            page = new DefaultHelpPage(null, Collections.singletonMap(path[i], page));
        }

        return page;
    }

    public HelpPage build(String content) {
        this.content = content;
        return build();
    }
}
