package test;

import org.opengroove.jzbot.fact.FactEntity;
import org.opengroove.jzbot.fact.FactParser;

public class Test05
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out
                .println(FactParser
                        .explain("Hello %1%. You are {{ifeq||%0%||jcp||my creator.||not my creator.}} {{ifneq||%0%||jcp||Since you are not my creator, I will have to remind you in %1% seconds. {{future||remind-%0%||%1%||remindfact||%0%}}}}"));
    }
    
}
