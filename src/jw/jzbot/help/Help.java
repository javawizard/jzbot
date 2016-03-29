package jw.jzbot.help;

/**
 * Created by aboyd on 2016-03-28.
 */
public class Help {
    public HelpPageBuilder at(String... path) {
        return new HelpPageBuilder(path);
    }

    public HelpPageBuilder content(String content) {
        return new HelpPageBuilder().content(content);
    }

    public HelpPageBuilder child(String name, HelpPage child) {
        return new HelpPageBuilder().child(name, child);
    }

    public HelpPageBuilder merge(HelpPage page) {
        return new HelpPageBuilder().merge(page);
    }

    public HelpPage build(String content) {
        return new HelpPageBuilder().build(content);
    }
}
