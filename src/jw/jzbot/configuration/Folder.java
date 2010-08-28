package jw.jzbot.configuration;

import java.util.HashMap;
import java.util.Map;

import jw.jzbot.configuration.Configuration.VarType;

public class Folder extends Setting
{
    private Map<String, Setting> settings = new HashMap<String, Setting>();
    
    public Folder(String name, String description, VarType type)
    {
        super(name, description, type);
    }
    
    public void add(Setting setting)
    {
        if (settings.containsKey(setting.name))
            throw new IllegalStateException("There's "
                + "already a setting with the same name.");
        settings.put(setting.name, setting);
        setting.folder = this;
    }
    
    public void remove(String name)
    {
        if (name.contains("/"))
            getSetting(name).remove();
        settings.remove(name);
    }
    
    public Setting getSetting(String name)
    {
        if (name.equals(""))
            return this;
        String[] components = name.split("\\/");
        Folder folder = this;
        for (int i = 0; i < components.length - 1; i++)
        {
            folder = folder.getFolder(components[i]);
        }
        if (folder == this)
            return settings.get(components[components.length - 1]);
        else if (folder == null)
            throw new IllegalArgumentException("The parent folder of the setting " + name
                + " does not exist.");
        else
            return folder.getSetting(components[components.length - 1]);
    }
    
    public Folder getFolder(String name)
    {
        try
        {
            return (Folder) getSetting(name);
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("The setting " + name
                + " is a variable, not a folder.", e);
        }
    }
    
    public Variable getVariable(String name)
    {
        try
        {
            return (Variable) getSetting(name);
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("The setting " + name
                + " is a folder, not a variable.", e);
        }
    }
    
    /**
     * Gets the parent folder of the specified setting. The setting doesn't need to
     * actually exist as long as its would-be parent exists. Indeed, this is used to get
     * the folder to add new settings to when registering settings. If the specified name
     * does not contain a forward slash, <tt>this</tt> (the parent of the would-be
     * setting) is returned.
     * 
     * @param name
     *            The full path of the setting
     * @return The setting's parent folder
     */
    public Folder getParent(String name)
    {
        if (!name.contains("/"))
            return this;
        return getFolder(name.substring(0, name.lastIndexOf("/")));
    }
    
    public Setting[] getSettings()
    {
        return settings.values().toArray(new Setting[0]);
    }
    
    public String[] getSettingNames()
    {
        return settings.keySet().toArray(new String[0]);
    }
    
}