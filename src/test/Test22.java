package test;

import java.security.SecureRandom;
import java.util.Random;

public class Test22
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Random random = new SecureRandom();
        int max = 7;
        int[] counts = new int[max];
        for (int i = 0; i < 900000; i++)
        {
            counts[random.nextInt(7)]++;
        }
        for (int i = 0; i < max; i++)
        {
            System.out.println("" + i + ": " + counts[i]);
        }
    }
    
}
