package jw.jzbot.pastebin;

import java.io.File;
import java.io.IOException;

import jw.jzbot.pastebin.providers.PDPastebin;
import jw.jzbot.pastebin.providers.PastebinDotCa;
import jw.jzbot.pastebin.providers.PastebinDotCom;
import jw.jzbot.pastebin.providers.StaticDirectory;
import net.sf.opengroove.common.utils.StringUtils;

public class DefaultPastebinProviders {
    public static void installDefaultSet() {
        File prefixPath = new File("storage/static-directory-pastebin-prefix");
        if (prefixPath.exists()) {
            String prefix = StringUtils.readFile(prefixPath).trim();
            PastebinService.installProvider(new StaticDirectory(prefix, new File("storage/static-directory-pastebin-contents")));
        }

        PastebinService.installProvider(new PastebinDotCom());
    }
}
