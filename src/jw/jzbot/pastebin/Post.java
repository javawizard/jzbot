package jw.jzbot.pastebin;

import jw.jzbot.utils.Pastebin.Duration;

public class Post
{
    private String name = "";
    private String parent;
    private Duration duration = Duration.DAY;
    private String data;
    private String description = "";
    private String[] tags = new String[0];
    
    public Post()
    {
        super();
    }
    
    public String getName()
    {
        return name;
    }
    
    public Post(String name, String description, Duration duration, String parent,
            String[] tags, String data)
    {
        super();
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.parent = parent;
        this.tags = tags;
        this.data = data;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public Duration getDuration()
    {
        return duration;
    }
    
    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }
    
    public String getData()
    {
        return data;
    }
    
    public void setData(String data)
    {
        this.data = data;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String[] getTags()
    {
        return tags;
    }
    
    public void setTags(String[] tags)
    {
        this.tags = tags;
    }
}
