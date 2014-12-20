package net.sf.opengroove.common.proxystorage;

class TableColumn
{
    private String name;
    private int type;
    private int size;
    
    public TableColumn()
    {
        super();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime
            * result
            + ((name == null) ? 0 : name.toLowerCase()
                .hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TableColumn other = (TableColumn) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equalsIgnoreCase(other.name))
            return false;
        return true;
    }
    
    public TableColumn(String name, int type, int size)
    {
        super();
        this.name = name;
        this.type = type;
        this.size = size;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getType()
    {
        return type;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
}
