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
            System.out.println("The file is run as a Fact program. See ");
            System.out.println("http://jzbot.googlecode.com for more info.");
            return;
        }
        String filename = args[0];
    }
    
}
