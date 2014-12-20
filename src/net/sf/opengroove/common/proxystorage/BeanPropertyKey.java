package net.sf.opengroove.common.proxystorage;

public class BeanPropertyKey
{
    public long id;
    public String property;
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime
            * result
            + ((property == null) ? 0 : property.hashCode());
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
        final BeanPropertyKey other = (BeanPropertyKey) obj;
        if (id != other.id)
            return false;
        if (property == null)
        {
            if (other.property != null)
                return false;
        }
        else if (!property.equals(other.property))
            return false;
        return true;
    }
}
