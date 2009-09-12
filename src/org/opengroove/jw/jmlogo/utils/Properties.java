package org.opengroove.jw.jmlogo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Properties extends Hashtable
{
    public Properties()
    {
        super();
    }
    
    public String getString(String name)
    {
        return (String) get(name);
    }
    
    public void setString(String name, String value)
    {
        put(name, value);
    }
    
    public int getInt(String name)
    {
        return Integer.parseInt(getString(name));
    }
    
    public void setInt(String name, int value)
    {
        setString(name, Integer.toString(value));
    }
    
    public String[] listProperties()
    {
        Vector props = new Vector();
        Enumeration keys = super.keys();
        while (keys.hasMoreElements())
            props.addElement(keys.nextElement());
        String[] result = new String[props.size()];
        props.copyInto(result);
        return result;
    }
    
    public byte[] write()
    {
        int length = 0;
        String[] properties = listProperties();
        for (int i = 0; i < properties.length; i++)
        {
            length += properties[i].length() + getString(properties[i]).length() + 5;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
        DataOutputStream dos = new DataOutputStream(baos);
        for (int i = 0; i < properties.length; i++)
        {
            try
            {
                dos.writeUTF(properties[i]);
                dos.writeUTF(getString(properties[i]));
            }
            catch (IOException e)
            {
                throw new RuntimeException(
                    "IOException thrown while writing out properties");
            }
        }
        return baos.toByteArray();
    }
    
    public void read(byte[] bytes)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        while (bais.available() > 0)
        {
            try
            {
                setString(dis.readUTF(), dis.readUTF());
            }
            catch (IOException e)
            {
                throw new RuntimeException(
                    "IOException occured while reading in a properties file");
            }
        }
    }
}
