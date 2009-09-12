package org.opengroove.jw.jmlogo.utils;

public class Math2
{
    /**
     * Rounds the value specified.
     * 
     * @param value
     * @return
     */
    public static double round(double value)
    {
       double floor = Math.floor(value);
       double diff = value - floor;
       if(diff >= 0.5d)
           return Math.ceil(value);
       else
           return floor;
    }
    
    /**
     * Rounds the value specified, and returns it as an int.
     * 
     * @param value
     * @return
     */
    public static int ri(double value)
    {
        return (int) round(value);
    }
}
