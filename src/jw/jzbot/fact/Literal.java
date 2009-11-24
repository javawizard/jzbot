package jw.jzbot.fact;

public class Literal extends FactEntity
{
    private StringBuffer value = new StringBuffer();
    
    private String finalized;
    
    public Literal()
    {
    }
    
    public Literal(String s)
    {
        this();
        append(s);
    }
    
    public void append(String s)
    {
        value.append(s);
    }
    
    public void append(char c)
    {
        value.append(c);
    }
    
    public void finalize()
    {
        finalized = value.toString();
        value = null;
    }
    
    /**
     * Returns the value passed to {@link #Literal(String)}.
     */
    @Override
    public void execute(Sink sink, FactContext context)
    {
        if (finalized == null)
            finalize();
        sink.write(finalized);
    }
    
    @Override
    public void explain(Sink sink, int indentation, int increment)
    {
        sink.write(spaces(indentation));
        sink.write("literal: \"");
        resolve(sink, null);
        sink.write("\"\n");
    }
    
}
