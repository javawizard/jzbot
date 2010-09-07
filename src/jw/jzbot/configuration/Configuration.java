package jw.jzbot.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyString;

import jw.jzbot.JZBot;
import jw.jzbot.events.Notify;
import jw.jzbot.events.ScopeListener;
import jw.jzbot.scope.ScopeLevel;
import jw.jzbot.storage.ConfigStorage;
import jw.jzbot.storage.ConfigVariable;
import jw.jzbot.storage.StorageContainer;

/**
 * This class serves as a front-end to JZBot's configuration system. All configuration
 * management should go through here. The idea is that this class is the only one to use
 * the ConfigStorage and ConfigVariable storage classes, and it's important that this
 * happens in order for everything to work.<br/><br/>
 * 
 * The configuration system is similar to GConf (and, to some extent, the Windows
 * Registry), but it has some differences. The most notable one is that it's expected that
 * the primary method of changing configuration variables will be by the user changing
 * them directly, instead of a plugin providing a custom command to change them like GConf
 * and the registry expect.<br/><br/>
 * 
 * Another significant difference is that the schema is fixed. This means that plugins can
 * register configuration variables and folders whenever they want, but users can only
 * view and edit configuration variables currently registered. Users cannot create
 * arbitrarily-named configuration variables, and the type of a variable is fixed at
 * registration time (although it can be feasibly changed to a compatible type by
 * unregistering it and then re-registering it with the new type; unregistering a
 * configuration variable does not cause it to be deleted).<br/><br/>
 * 
 * Configuration variables remember their value even after they have been unregistered if
 * they are currently set at the time of unregistration. They do not show up in the list
 * (unless, of course, they are re-registered), but it's possible to read them by
 * specifying "~config all ..." instead of just "~config ...". They cannot be set; they
 * can only be read and deleted.<br/><br/>
 * 
 * Variables can be registered and unregistered while the bot is live. Indeed, plugins
 * that register configuration variables on all channels (such as wolfbot) will often
 * request to be notified when a new channel is added and register their configuration
 * variables on such a channel as it is added.<br/><br/>
 * 
 * Variables can be registered globally, or they can be registered to a specific server or
 * a specific channel. There is no way for a user to specify a server-wide default for a
 * channel configuration variable; if a plugin wishes to provide such functionality, it
 * could do so by registering a server configuration variable and then checking the
 * channel variable first. If it's unset, the server variable would then be
 * checked.<br/><br/>
 * 
 * When referring to a specific server or a specific channel, the scope of the channel (in
 * the format as used throughout the rest of the bot) is used. Global scope is indicated
 * by the empty string or a string containing a single at sign. Variables are referred to
 * as as "variable", "folder/variable", "parentfolder/childfolder/variable", and so on.
 * 
 * @author Alexander Boyd
 * 
 */
public class Configuration
{
    public static enum VarType
    {
        /**
         * A boolean variable. This can be set as either true, false, 0, or 1 when setting
         * with {@link Configuration#setText(String, String, String)}, and it will be
         * converted to 0 or 1.
         */
        bool,
        /**
         * An integer variable. This cannot have a decimal point, but it can be positive
         * or negative. There is no upper bound on the magnitude of the number that can be
         * stored in one of these variables.
         */
        integer,
        /**
         * A decimal variable. This can have arbitrarily-large precision.
         */
        decimal,
        /**
         * A text variable. This can contain any valid string of characters.
         */
        text,
        /**
         * A password variable. This is the same as a text variable except that its value
         * cannot be read with the ~config command (it reads as
         * "&lt;hidden for security reasons&gt;").
         */
        password,
        /**
         * A secret variable. This is the same as a password variable unless there is a
         * guard variable in the same folder as a secret variable, in which case the
         * secret variables in that folder are affected as described in the documentation
         * on the {@link #guard guard variable}.
         */
        secret,
        /**
         * A guard variable. This is the same as a text variable (in that it can be read
         * freely with the ~config command and hence should not be used to store
         * passwords), except that when a value is set for a guard variable, any
         * {@link #secret} variables in the same folder as the guard variable are unset
         * (or reset to their default value if they have one). <br/><br/>
         * 
         * This was originally added for the benefit of the IRC protocol. There needs to
         * be a configuration variable to store the services bot to authenticate to and
         * there needs to be a configuration variable to store the password that should be
         * used to authenticate. However, even superops shouldn't know the bot's password,
         * and if they could configure the services bot to authenticate to they could
         * simply change it to be their own nickname and restart the bot, and the bot
         * would send them its password. Guard variables and secret variables were
         * introduced for this reason; the services bot can be a guard variable and the
         * password can be a secret variable, and if a superop changes the services bot to
         * be their own nickname the password is immediately cleared, thus preventing it
         * from being exposed.
         */
        guard,
        /**
         * A folder. Folders can contain additional variables, and are a way to categorize
         * variables easily. They also play a role in the way {@link #secret} and
         * {@link #guard} variables work.
         */
        folder
    }
    
    private static Map<String, Folder> scopeFolderMap =
            Collections.synchronizedMap(new HashMap<String, Folder>());
    
    /**
     * Initializes the configuration system. The notification system must have been
     * initialized first, and initial notifications must not yet have been sent. The
     * configuration system uses the notification system for listening for changes to
     * which channels and servers exist.
     */
    public static void initialize()
    {
        ScopeListener removed = new ScopeListener()
        {
            
            @Override
            public void notify(ScopeLevel level, String scope, boolean initial)
            {
                scopeFolderMap.remove(scope);
            }
        };
        Notify.channelRemoved.addListener(removed);
        Notify.serverRemoved.addListener(removed);
    }
    
    /**
     * Registers a new variable.
     * 
     * @param scope
     *            The scope to register the variable at
     * @param name
     *            The name of the variable, complete with folders if desired. The folders
     *            must have been previously registered.
     * @param description
     *            The description of the variable
     * @param type
     *            The type of the variable
     * @param defaultValue
     *            The default value. This is ignored for folders.
     */
    public static void register(String scope, String name, String description,
            VarType type, String defaultValue)
    {
        scope = normalizeScope(scope);
        ConfigStorage storage = getConfigStorage(scope, true);
        if (type == VarType.folder)
        {
            registerFolder(scope, name, description);
            return;
        }
        ConfigVariable valueObject = storage.getVariable(name);
        String value;
        if (valueObject == null)
            value = null;
        else
            value = valueObject.getValue();
        Variable var =
                new Variable(extractName(name), description, type, defaultValue, value);
        getScopeFolder(scope).getParent(name).add(var);
    }
    
    public static boolean getBool(String scope, String name)
    {
        return getInt(scope, name) >= 1;
    }
    
    public static BigInteger getInteger(String scope, String name)
    {
        return new BigInteger(getText(scope, name));
    }
    
    public static int getInt(String scope, String name)
    {
        return getInteger(scope, name).intValue();
    }
    
    public static BigDecimal getDecimal(String scope, String name)
    {
        return new BigDecimal(getText(scope, name));
    }
    
    public static double getDouble(String scope, String name)
    {
        return getDecimal(scope, name).doubleValue();
    }
    
    /**
     * Returns the current value of the specified variable in text form. Note that boolean
     * variables will be returned as "1" and "0" instead of "true" and "false". If you
     * need "true" and "false", use {@link #getHumanText(String, String)} instead.
     * 
     * @param scope
     *            The scope to read the variable from
     * @param name
     *            The path of the variable
     * @return The variable's current value, or the default value of the variable if it is
     *         unset. If there is no default, null is returned.
     */
    public static String getText(String scope, String name)
    {
        scope = normalizeScope(scope);
        Variable var = getScopeFolder(scope).getVariable(name);
        if (var == null)
            throw new IllegalArgumentException("The variable " + scope + ":" + name
                + " does not exist.");
        if (var.value == null)
            return var.defaultValue;
        return var.value;
    }
    
    /**
     * Same as {@link #getText(String, String)}, but the variable is translated for human
     * viewing. If the variable is a boolean, it is returned as "true" or "false" instead
     * of "1" or "0", respectively. If the variable is a password variable or a secret
     * variable, the return value of this method is "&lt;hidden for security reasons&gt;".
     * 
     * @param scope
     * @param name
     * @return
     */
    public static String getHumanText(String scope, String name)
    {
        String value = getText(scope, name);
        Variable var = getScopeFolder(scope).getVariable(name);
        if (var.type == VarType.bool)
        {
            if (value.equals("1"))
                value = "true";
            else if (value.equals("0"))
                value = "false";
        }
        else if (var.type == VarType.password || var.type == VarType.secret)
        {
            value = "<hidden for security reasons>";
        }
        return value;
    }
    
    public static void setBool(String scope, String name, boolean value)
    {
        setInt(scope, name, value ? 1 : 0);
    }
    
    public static void setInteger(String scope, String name, BigInteger integer)
    {
        setText(scope, name, integer.toString());
    }
    
    public static void setInt(String scope, String name, int value)
    {
        setText(scope, name, "" + value);
    }
    
    public static void setDecimal(String scope, String name, BigDecimal value)
    {
        setText(scope, name, value.toString());
    }
    
    public static void setDouble(String scope, String name, double value)
    {
        setText(scope, name, "" + value);
    }
    
    public static void setText(String scope, String name, String text)
    {
        scope = normalizeScope(scope);
        Variable var = getScopeFolder(scope).getVariable(name);
        if (var.type == VarType.bool)
        {
            if (text.equals("true"))
                text = "1";
            else if (text.equals("false"))
                text = "0";
        }
        var.validateConformantValue(text);
        if(!var.fireFilters(scope, name, text))return;
        if (var.type == VarType.guard)
        {
            /*
             * We need to iterate over all the secret variables in the same folder and
             * unset them.
             */
            for (Setting setting : var.folder.getSettings())
            {
                if ((setting instanceof Variable)
                    && ((Variable) setting).type == VarType.secret)
                {
                    Variable secretVar = (Variable) setting;
                    String secretName;
                    if (name.contains("/"))
                        secretName =
                                name.substring(0, name.lastIndexOf("/")) + "/"
                                    + secretVar.name;
                    else
                        secretName = secretVar.name;
                    setText(scope, secretName, null);
                }
            }
        }
        ConfigStorage storage = getConfigStorage(scope, true);
        ConfigVariable storedVar = storage.getVariable(name);
        if (storedVar == null)
        {
            storedVar = storage.createVariable();
            storedVar.setName(name);
            storage.getVariables().add(storedVar);
        }
        storedVar.setValue(text);
        var.value = text;
        var.fireListeners(scope, name);
    }
    
    /**
     * Gets the type of the specified variable. An exception will be thrown if the
     * specified variable does not exist.
     * 
     * @param scope
     * @param name
     * @return
     */
    public static VarType getType(String scope, String name)
    {
        scope = normalizeScope(scope);
        return getScopeFolder(scope).getSetting(name).type;
    }
    
    /**
     * Gets the description of the specified variable.
     * 
     * @param scope
     * @param name
     * @return
     */
    public static String getDescription(String scope, String name)
    {
        scope = normalizeScope(scope);
        return getScopeFolder(scope).getSetting(name).description;
    }
    
    /**
     * Gets the names of all of the child variables of the specified variable.
     * 
     * @param scope
     * @param name
     * @return
     */
    public static String[] getChildNames(String scope, String name)
    {
        scope = normalizeScope(scope);
        Folder folder = getScopeFolder(scope).getFolder(name);
        return folder.getSettingNames();
    }
    
    public static boolean isSet(String scope, String name)
    {
        scope = normalizeScope(scope);
        Variable var = getScopeFolder(scope).getVariable(name);
        return var.value != null;
    }
    
    public static boolean hasDefault(String scope, String name)
    {
        scope = normalizeScope(scope);
        Variable var = getScopeFolder(scope).getVariable(name);
        return var.defaultValue != null;
    }
    
    /**
     * Adds a listener that will be called when the specified var changes value. The var
     * must be registered; if it is ever deregistered in the future, this will clear all
     * of its listeners, and they must be added again if the var is re-registered.
     * 
     * @param scope
     * @param name
     * @param listener
     */
    public static void addListener(String scope, String name, VarListener listener)
    {
        scope = normalizeScope(scope);
        Variable var = getScopeFolder(scope).getVariable(name);
        synchronized (var.listeners)
        {
            var.listeners.add(listener);
        }
    }
    
    /**
     * Adds a filter that will be called when the specified var is about to change value.
     * Filters can be used to validate that the new value for a variable satisfies some
     * criteria. If the new value does not, the filter can throw an exception and the var
     * will not be changed. The var must be registered; if it is ever deregistered in the
     * future, this will clear all of its filters, and they must be added again if the var
     * is re-registered.
     * 
     * @param scope
     * @param name
     * @param filter
     */
    public static void addFilter(String scope, String name, VarFilter filter)
    {
        scope = normalizeScope(scope);
        Variable var = getScopeFolder(scope).getVariable(name);
        synchronized (var.filters)
        {
            var.filters.add(filter);
        }
    }
    
    /**
     * Returns true if this variable exists in the database, regardless of whether or not
     * it is set. This method exists solely for the code in {@link ConfigVars} to migrate
     * variables from the old system over to this class, and generally shouldn't be used
     * by anything else.
     * 
     * @param scope
     * @param name
     * @return
     */
    public static boolean hasDatastoreVar(String scope, String name)
    {
        scope = normalizeScope(scope);
        ConfigStorage storage = getConfigStorage(scope, false);
        if (storage == null)
            return false;
        if (storage.getVariable(name) == null)
            return false;
        return true;
    }
    
    private static ConfigStorage getConfigStorage(String scope, boolean create)
    {
        scope = normalizeScope(scope);
        StorageContainer container = JZBot.getCheckedStorageContainer(scope);
        ConfigStorage storage = container.getConfiguration();
        if (storage == null && create)
        {
            storage = container.createConfiguration();
            container.setConfiguration(storage);
        }
        return storage;
    }
    
    private static void registerFolder(String scope, String name, String description)
    {
        Folder folder = new Folder(extractName(name), description, VarType.folder);
        Folder scopeFolder = getScopeFolder(scope);
        scopeFolder.getParent(name).add(folder);
    }
    
    /**
     * Gets the folder representing the configuration variables for a particular scope. If
     * the folder does not exist, it will be created.
     * 
     * @param scope
     * @return
     */
    private static Folder getScopeFolder(String scope)
    {
        scope = normalizeScope(scope);
        Folder folder = scopeFolderMap.get(scope);
        if (folder == null)
        {
            folder = new Folder(scope, "(no description)", null);
            scopeFolderMap.put(scope, folder);
        }
        return folder;
    }
    
    private static String normalizeScope(String scope)
    {
        if (scope == null || scope.equals("@"))
            scope = "";
        return scope;
    }
    
    static String extractName(String path)
    {
        if (path.contains("/"))
            return path.substring(path.lastIndexOf("/") + 1);
        return path;
    }
    
    public static String child(String parent, String child)
    {
        if (parent.equals(""))
            return child;
        return parent + "/" + child;
    }
    
    public static boolean exists(String scope, String name)
    {
        try
        {
            getType(scope, name);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
