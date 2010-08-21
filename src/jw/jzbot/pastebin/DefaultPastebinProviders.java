package jw.jzbot.pastebin;

import jw.jzbot.pastebin.providers.PDPastebin;
import jw.jzbot.pastebin.providers.PastebinDotCa;

public class DefaultPastebinProviders
{
    public static void installDefaultSet()
    {
        // FIXME: add paste.icefyre.org
        // PastebinService.installProvider(new PDPastebin("pastebin.com", true,
        // "pastebin.php", true));
        // p.baf.cc added a captcha, so removed for now.
        // PastebinService.installProvider(new PDPastebin("p.baf.cc", false,
        // "pastebin.php"));
        // PastebinService.installProvider(new PDPastebin("pastebin.flamingspork.com",
        // false,
        // "pastebin.php", true));
        // This one is commented out at the request of Amahi
        // PastebinService.installProvider(new PDPastebin("paste.amahi.org", false,
        // "pastebin.php", false));
        PastebinService.installProvider(new PDPastebin("pastebin.hu", false,
                "pastebin.php", true));
        PastebinService.installProvider(new PDPastebin("ampaste.net", true, "pastebin.php",
                true));
        // PastebinService.installProvider(new PDPastebin("paste.liquidswords.org", false,
        // "pastebin.php", true));
        // I haven't checked pastebin.im to see if it supports highlights.
        // PastebinService.installProvider(new PDPastebin("pastebin.im", false,
        // "index.php",
        // false));
        // PastebinService.installProvider(new PDPastebin("ospaste.com", false,
        // "index.php",
        // true));
        PastebinService.installProvider(new PDPastebin("p.jcs.me.uk", false,
                "pastebin.php", true));
        // PastebinService.installProvider(new PDPastebin("paste.uberdragon.net", false,
        // "index.php"));
        
        // pastebin.flamingspork.com
        // paste.amahi.org
        // p.baf.cc
        // paste.uberdragon.net
        // paste.liquidswords.org
        // pastebin.hu
        // PastebinService.installProvider(new PastebinDotCa());
    }
}
