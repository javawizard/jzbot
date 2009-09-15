package test;

public class Test04
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out
                .println(("Syntax: {{run||<factoid>||<argument1>||...}} -- Runs the specified factoid "
                        + "without actually importing it. This function therefore evaluates "
                        + "to nothing. This is pretty much only useful when the factoid in question "
                        + "has useful side effects, like setting a global variable."
                        + " <factoid> is the name of the factoid, and "
                        + "<argument1>, <argument2>, etc. are the arguments to be passed to the factoid.")
                        .length());
    }
}
