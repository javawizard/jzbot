package jw.jzbot.pastebin;

import jw.jzbot.pastebin.providers.PDPastebin;
import jw.jzbot.pastebin.providers.PastebinDotCa;

public class DefaultPastebinProviders
{
    public static void installDefaultSet()
    {
        PastebinService
                .installProvider(new PDPastebin("pastebin.com", true, "pastebin.php"));
        PastebinService.installProvider(new PDPastebin("p.baf.cc", false, "pastebin.php"));
        PastebinService.installProvider(new PDPastebin("pastebin.flamingspork.com", false,
                "pastebin.php"));
        PastebinService.installProvider(new PDPastebin("paste.amahi.org", false,
                "pastebin.php"));
        PastebinService
                .installProvider(new PDPastebin("ampaste.net", true, "pastebin.php"));
        PastebinService
                .installProvider(new PDPastebin("paste.liquidswords.org", false, "pastebin.php"));
        PastebinService.installProvider(new PDPastebin("pastebin.im", false, "index.php"));
        PastebinService.installProvider(new PDPastebin("paste.uberdragon.net", false,
                "index.php"));
        // pastebin.flamingspork.com
        // paste.amahi.org
        // p.baf.cc
        // paste.uberdragon.net
        // paste.liquidswords.org
        // PastebinService.installProvider(new PastebinDotCa());
    }
}
