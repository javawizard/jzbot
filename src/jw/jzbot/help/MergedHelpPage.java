package jw.jzbot.help;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aboyd on 2016-03-28.
 */
public class MergedHelpPage implements HelpPage {
    private List<HelpPage> pages;

    public MergedHelpPage(List<HelpPage> pages) {
        this.pages = pages;
    }

    public String getContent() {
        for (HelpPage page : pages) {
            String content = page.getContent();
            if (content != null) {
                return content;
            }
        }

        return null;
    }

    public Set<String> getChildNames() {
        Set result = new HashSet<>();

        for (HelpPage page : pages) {
            result.addAll(page.getChildNames());
        }

        return result;
    }

    public HelpPage getChild(String name) {
        List<HelpPage> childPages = new ArrayList<HelpPage>();

        for (HelpPage page : pages) {
            HelpPage childPage = page.getChild(name);
            if (childPage != null) {
                childPages.add(childPage);
            }
        }

        if (childPages.size() == 0) {
            return null;
        } else if (childPages.size() == 1) {
            return childPages.get(0);
        } else {
            return new MergedHelpPage(childPages);
        }
    }
}
