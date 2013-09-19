package jw.jzbot.fact.debug.wire;

public class AuthRequest
{
    private String instructions;
    private String[] fields;
    private boolean[] passwords;
    
    public String getInstructions()
    {
        return instructions;
    }
    
    public void setInstructions(String instructions)
    {
        this.instructions = instructions;
    }
    
    public String[] getFields()
    {
        return fields;
    }
    
    public void setFields(String[] fields)
    {
        this.fields = fields;
    }
    
    public boolean[] getPasswords()
    {
        return passwords;
    }
    
    public void setPasswords(boolean[] passwords)
    {
        this.passwords = passwords;
    }
}
