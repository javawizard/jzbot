package jw.jzbot.pastebin;

import jw.jzbot.pastebin.providers.PDPastebin;
import jw.jzbot.pastebin.providers.PastebinDotCa;

public class DefaultPastebinProviders
{
    public static void installDefaultSet()
    {
        PastebinService.installProvider(new PDPastebin("pastebin.com", true, "pastebin.php"));
        PastebinService.installProvider(new PDPastebin("ampaste.net", true, "pastebin.php"));
        PastebinService.installProvider(new PDPastebin("pastebin.im", false, "index.php"));
//        PastebinService.installProvider(new PastebinDotCa());
    }
}
