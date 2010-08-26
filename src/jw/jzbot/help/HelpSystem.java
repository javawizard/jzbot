package jw.jzbot.help;

import java.util.ArrayList;


public class HelpSystem
{
    private static final ArrayList<HelpProvider> providers = new ArrayList<HelpProvider>();
    
    public static synchronized void installProvider(HelpProvider provider)
    {
        providers.add(provider);
    }

    public static synchronized HelpProvider[] getProviders()
    {
        return providers.toArray(new HelpProvider[0]);
    }

}
