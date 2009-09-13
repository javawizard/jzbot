package test;

import org.cheffo.jeplite.JEP;

public class Test02
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        System.out.println("starting");
        for (int i = 0; i < 10000; i++)
        {
            JEP jep = new JEP();
            jep.addStandardConstants();
            jep.addStandardFunctions();
            jep.parseExpression("2+(3*5)");
            double value = jep.getValue();
        }
        System.out.println("done");
    }
}
