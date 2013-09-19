package jw.jzbot.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class RegexFileFilter implements FilenameFilter
{
    private String regex;
    
    public RegexFileFilter(String regex)
    {
        super();
        this.regex = regex;
    }
    
    @Override
    public boolean accept(File dir, String name)
    {
        return name.matches(regex);
    }
    
}
