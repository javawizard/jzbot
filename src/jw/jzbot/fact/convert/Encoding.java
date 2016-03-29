package jw.jzbot.fact.convert;

import jw.jzbot.help.HelpPage;

public interface Encoding
{
    public HelpPage getHelp();
    
    public Object decode(String data);
    
    public String encode(Object data);
}
