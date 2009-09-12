package org.opengroove.jw.jmlogo.lang;

public class ListToken extends Token
{
    private static int hashCode(Object[] array)
    {
        final int prime = 31;
        if (array == null)
            return 0;
        int result = 1;
        for (int index = 0; index < array.length; index++)
        {
            result =
                prime * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }
    
    private Token[] members;
    
    public ListToken(Token[] members)
    {
        super();
        this.members = members;
    }
    
    public Token[] getMembers()
    {
        return members;
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ListToken.hashCode(members);
        return result;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ListToken other = (ListToken) obj;
        if (members.length != other.members.length)
            return false;
        for (int i = 0; i < members.length; i++)
        {
            if (members[i] == null && other.members[i] != null)
                return false;
            if (members[i] != null && other.members[i] == null)
                return false;
            if (!members[i].equals(other.members[i]))
                return false;
        }
        return true;
    }
    
    public ListToken fput(Token token)
    {
        ListToken source = this;
        Token[] sourceTokens = source.getMembers();
        Token[] tokens = new Token[sourceTokens.length + 1];
        tokens[0] = token;
        System.arraycopy(sourceTokens, 0, tokens, 1, sourceTokens.length);
        return new ListToken(tokens);
    }
    
    public ListToken lput(Token token)
    {
        ListToken source = this;
        Token[] sourceTokens = source.getMembers();
        Token[] tokens = new Token[sourceTokens.length + 1];
        tokens[tokens.length - 1] = token;
        System.arraycopy(sourceTokens, 0, tokens, 0, sourceTokens.length);
        return new ListToken(tokens);
    }
    
    public ListToken butFirst()
    {
        if (members.length < 1)
            throw new InterpreterException("You can't get the butFirst of a list "
                + "that doesn't have any elements");
        Token[] tokens = new Token[members.length - 1];
        System.arraycopy(members, 1, tokens, 0, tokens.length);
        return new ListToken(tokens);
    }
    
    public ListToken butLast()
    {
        if (members.length < 1)
            throw new InterpreterException("You can't get the butLast of a list "
                + "that doesn't have any elements");
        Token[] tokens = new Token[members.length - 1];
        System.arraycopy(members, 0, tokens, 0, tokens.length);
        return new ListToken(tokens);
    }
}
