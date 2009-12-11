package jw.jzbot.fact.standalone;

public class FactInterpreter
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Usage: fact <filename> -- Runs the specified file.");
            System.out.println("If the file starts with \"#!\", the first line of");
            System.out.println("the file will be ignored when running the program.");
            return;
        }
        String filename = args[0];
    }
    
}
