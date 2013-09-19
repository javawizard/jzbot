package jw.jzbot.pastebin;

import jw.jzbot.utils.Pastebin.Duration;

public interface PastebinProvider
{
    public static enum Feature
    {
        /**
         * Indicates that posts can be created which are "updates" of other posts, in the
         * sense that the pastebin provides controls from within the child post to view
         * the parent post or a comparison of the two. If this feature is not present,
         * specifying a parent for a post will have no effect.
         */
        update,
        /**
         * Indicates that posts can have a duration of {@link Duration#FOREVER}, and that
         * the pastebin will honor this and persist the post forever.
         */
        forever,
        /**
         * Indicates that posts can have an author or name included with them, which will
         * be attached to the post.
         */
        author,
        /**
         * Indicates that posts can have additional information beyond the post contents
         * and name. Pastebin.ca's description/question field is an example of this.
         */
        info,
        /**
         * Indicates that posts can have tags associated with them.
         */
        tags,
        /**
         * Indicates that lines within a post that start with two at-signs will be
         * highlighted by the pastebin service, and removed when reading posts.
         */
        highlight
    }
    
    /**
     * Returns the list of features that this provider supports for creating posts.
     * 
     * @return
     */
    public Feature[] getSendFeatures();
    
    /**
     * Returns the list of features that this provider supports for reading posts.
     * 
     * @return
     */
    public Feature[] getReceiveFeatures();
    
    /**
     * Instructs the pastebin to create a new post.
     * 
     * @param post
     *            The post to create
     * @return A url that the new post can be retrieved from
     */
    public String send(Post post);
    
    /**
     * Instructs the pastebin to retrieve the post at the specified URL. If there is no
     * such post, null should be returned. This method will only be called for a
     * particular URL if {@link #understands(String)} returns true for the URL.
     * 
     * @param url
     *            The URL to read
     * @return The post, or null if there is no such post
     */
    public Post read(String url);
    
    /**
     * Returns true if this pastebin provider can read URLs of the specified format. When
     * the pastebin service is asked to read a pastebin post, it will call this method on
     * all providers it knows about until it finds a provider that can read the post in
     * question.
     * 
     * @param url
     *            The URL to read
     * @return True if this provider knows how to read the post
     */
    public boolean understands(String url);
}
