package jw.jzbot.help;

/**
 * A help provider. Help providers provide help content to the ~help command.
 * Pages are addressed hierarchically with the space character being the
 * hierarchy delimiter.
 * 
 * @author Alexander Boyd
 * 
 */
public interface HelpProvider
{
    /**
     * Lists pages that are children of the specified page.
     * 
     * @param page
     * @return
     */
    public String[] listPages(String page);
    
    /**
     * Gets the text of the help page by the specified name. If this help
     * provider doesn't know about the specified page, or if the specified page
     * doesn't exist, null should be returned.
     * 
     * @param page
     * @return
     */
    public String getPage(String page);
}
