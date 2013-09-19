package jw.jzbot.storage.map;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jw.jzbot.storage.MapContainer;
import jw.jzbot.storage.MapEntry;

import net.sf.opengroove.common.proxystorage.StoredList;


public class StoredMap extends AbstractMap<String, String>
{
    private MapContainer container;
    private StoredList<MapEntry> storedList;
    
    public StoredMap(MapContainer container)
    {
        this.container = container;
        this.storedList = container.getEntries();
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<String, String>>
    {
        
        @Override
        public Iterator<Map.Entry<String, String>> iterator()
        {
            return new EntryIterator();
        }
        
        @Override
        public int size()
        {
            return container.getEntries().size();
        }
        
        @Override
        public boolean add(Map.Entry<String, String> e)
        {
            if (container.getEntry(e.getKey()) != null)
                return false;
            MapEntry entry = container.createEntry();
            entry.setKey(e.getKey());
            entry.setValue(e.getValue());
            storedList.add(entry);
            return true;
        }
        
    }
    
    private class Entry implements Map.Entry<String, String>
    {
        private MapEntry entry;
        
        protected Entry(MapEntry entry)
        {
            this.entry = entry;
        }
        
        @Override
        public String getKey()
        {
            return entry.getKey();
        }
        
        @Override
        public String getValue()
        {
            return entry.getValue();
        }
        
        @Override
        public String setValue(String value)
        {
            String v = entry.getValue();
            entry.setValue(value);
            return v;
        }
        
    }
    
    private class EntryIterator implements Iterator<Map.Entry<String, String>>
    {
        private Iterator<MapEntry> backingIterator;
        
        public EntryIterator()
        {
            backingIterator = storedList.iterator();
        }
        
        @Override
        public boolean hasNext()
        {
            return backingIterator.hasNext();
        }
        
        @Override
        public Map.Entry<String, String> next()
        {
            return new Entry(backingIterator.next());
        }
        
        @Override
        public void remove()
        {
            backingIterator.remove();
        }
        
    }
    
    @Override
    public Set<Map.Entry<String, String>> entrySet()
    {
        return new EntrySet();
    }
    
    @Override
    public boolean containsKey(Object key)
    {
        if (!(key instanceof String))
            return false;
        return container.getEntry((String) key) != null;
    }
    
    @Override
    public boolean containsValue(Object value)
    {
        // TODO Auto-generated method stub
        return super.containsValue(value);
    }
    
    @Override
    public String get(Object key)
    {
        // TODO Auto-generated method stub
        return super.get(key);
    }
    
    @Override
    public String put(String key, String value)
    {
        // TODO Auto-generated method stub
        return super.put(key, value);
    }
    
}
