package jw.boardforj;

public class Response
{
    /**
     * The text of this response
     */
    public String text;
    /**
     * The alias of the writer that wrote this response
     */
    public String alias;
    
    public String toString()
    {
        return "Response[text: " + text + ", alias: " + alias + "]";
    }
}
