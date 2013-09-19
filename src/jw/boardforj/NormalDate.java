package jw.boardforj;

import java.util.Date;

public class NormalDate extends Date
{
    /**
     * Creates a NormalDate from an existing Date object.
     * 
     * @param date
     */
    public NormalDate(Date date)
    {
        this(date.getTime());
    }
    
    public NormalDate()
    {
        super();
    }
    
    public NormalDate(long date)
    {
        super(date);
    }
    
    public NormalDate(int year, int month, int date, int hrs, int min, int sec)
    {
        super(year - 1900, month - 1, date, hrs, min, sec);
    }
    
    public NormalDate(int year, int month, int date, int hrs, int min)
    {
        super(year - 1900, month - 1, date, hrs, min);
    }
    
    public NormalDate(int year, int month, int date)
    {
        super(year - 1900, month - 1, date);
    }
    
    public NormalDate(String s)
    {
        super(s);
    }
    
    public int getNormalYear()
    {
        return getYear() + 1900;
    }
    
    public void setNormalYear(int year)
    {
        setYear(year - 1900);
    }
    
    public int getNormalMonth()
    {
        return getMonth() + 1;
    }
    
    public void setNormalMonth(int month)
    {
        setMonth(month - 1);
    }
}
