package test;

public class Test04
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out
                .println(("For example, {{numberlist||1||5}} evaluates to \"1 2 3 4 5\", "
                        + "{{numberlist||5||1}} evaluates to \"5 4 3 2 1\", {{numberlist||3||3}} "
                        + "evaluates to \"3\", {{numberlist||45||8||10}} evaluates to \"45 35 25 15\", "
                        + "{{numberlist||8||45||10}} evaluates to \"8 18 28 38\", and {{numberlist||"
                        + "8||45||10}} evaluates to \"8 18 28 38\". This can be used with {{split}} "
                        + "to create a for loop.")
                        .length());
    }
}
