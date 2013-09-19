package jw.boardforj;

import java.util.Arrays;
import java.util.List;

/**
 * A post on the 100-hour board.
 * 
 * @author Alexander Boyd
 * 
 */
public class Post
{
    /**
     * The id of this post.
     */
    public int id;
    /**
     * The text of this post.
     */
    public String text;
    /**
     * The time at which this post appeared on the board.
     */
    public NormalDate date;
    /**
     * The categories assigned to this post. This is a List instead of an array so as to
     * make it easier to see if a particular category is in the list (which can be done
     * with {@link List#contains(Object)}).
     */
    public List<String> categories;
    /**
     * The responses to this post. If this post is a comment, this will be an array of
     * length 0.
     */
    public Response[] responses;
    
    public String toString()
    {
        return "Post[id: " + id + ", date: " + date + ", text: " + text + ", categories: "
            + categories + ", responses: " + Arrays.toString(responses) + "]";
    }
}
