package net.sf.opengroove.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A class for getting and setting java bean properties dynamically on a given
 * object. For example, if a class declares a method called getExample(), then a
 * call to listProperties() on this class would return "example" as one of it's
 * members. The same would be true if the class declares a method called
 * isExample(), regardless of it's return type (although convention dictates
 * that an isXxx method only return boolean types and that all other types use a
 * getXxx method).<br/><br/>
 * 
 * This class also provides methods for getting a list of properties where
 * either the getter or the setter is annotated with a specified annotation
 * class.
 * 
 * @author Alexander Boyd
 * 
 */
public class BeanEditor
{
    private class PropertyInfo
    {
        public String name;
        public Method getter;
        public Method setter;
        public ArrayList<Annotation> annotations = new ArrayList<Annotation>();
    }
    
    private Object object;
    private HashMap<String, PropertyInfo> infos = new HashMap<String, PropertyInfo>();
    
    /**
     * Creates a new bean editor for accessing properties of the object
     * specified.
     * 
     * @param object
     */
    public BeanEditor(Object object)
    {
        this.object = object;
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods)
        {
            String methodName = method.getName();
            if (methodName.length() < 4)
                continue;
            if (!(methodName.startsWith("get") || methodName
                .startsWith("set")))
                continue;
            if (!Character
                .isUpperCase(methodName.charAt(4)))
                continue;
            /*
             * The method is a getter or a setter at this point. We now need to
             * check to see if the property info already exists.
             */
            String name = methodName.substring(3, 4)
                .toLowerCase()
                + methodName.substring(4, methodName
                    .length());
            PropertyInfo info = infos.get(name);
            if (info == null)
            {
                info = new PropertyInfo();
                info.name = name;
                infos.put(name, info);
            }
            if (methodName.startsWith("get"))
            {
                info.getter = method;
            }
            else
            {
                assert (methodName.startsWith("set"));
                info.setter = method;
            }
            Annotation[] annotations = method
                .getAnnotations();
            ArrayList<Annotation> newAnnotations = new ArrayList<Annotation>(
                Arrays.asList(annotations));
            /*
             * Now add all annotations to the property info that aren't already
             * present
             */
            newAnnotations.removeAll(info.annotations);
            info.annotations.addAll(newAnnotations);
        }
    }
    
    /**
     * Gets a list of all properties, both readable and writable.
     * 
     * @return
     */
    public String[] getProperties()
    {
        return infos.keySet().toArray(new String[0]);
    }
    
    /**
     * Gets a list of all properties that are readable, IE they have a getter
     * method declared.
     * 
     * @return
     */
    public String[] getReadableProperties()
    {
        ArrayList<String> props = new ArrayList<String>();
        for (PropertyInfo info : infos.values())
        {
            if (info.getter != null)
                props.add(info.name);
        }
        return props.toArray(new String[0]);
    }
    
    /**
     * Gets a list of all properties that are writable, IE they have a setter
     * method declared.
     * 
     * @return
     */
    public String[] getWritableProperties()
    {
        ArrayList<String> props = new ArrayList<String>();
        for (PropertyInfo info : infos.values())
        {
            if (info.setter != null)
                props.add(info.name);
        }
        return props.toArray(new String[0]);
    }
}
