package jw.jzbot.help;

/**
 * Created by aboyd on 2016-03-28.
 */
public class Help {
    public static HelpPageBuilder at(String... path) {
        return new HelpPageBuilder(path);
    }

    public static HelpPageBuilder content(String content) {
        return new HelpPageBuilder().content(content);
    }

    public static HelpPageBuilder child(String name, HelpPage child) {
        return new HelpPageBuilder().child(name, child);
    }

    public static HelpPageBuilder merge(HelpPage page) {
        return new HelpPageBuilder().merge(page);
    }

    public static HelpPage build(String content) {
        return new HelpPageBuilder().build(content);
    }
}
