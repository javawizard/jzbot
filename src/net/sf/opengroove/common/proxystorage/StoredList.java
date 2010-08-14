package net.sf.opengroove.common.proxystorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A StoredList is a list that is stored in the ProxyStorage system. Read and
 * write operations all delegate to the database.<br/><br/>
 * 
 * It's generally not a good idea to iterate over a stored list, unless it is
 * known for certain that the list (or any other instances of it that correspond
 * to the same database object) will not be modified elsewhere. The list's
 * iterators are <b>not</b> fail-fast: The results are undefined if the list is
 * modified while an iteration is in progress. In particular, if an iteration is
 * in progress, and the list's size is reduced below the index of the current
 * object that is being iterated over, the iterator will never return false from
 * hasNext() again. <i>If the list needs to be iterated over</i>, the
 * {@link #isolate()} method can be used to ensure iteration safety.
 * 
 * @author Alexander Boyd
 * 
 * @param <T>
 */
public class StoredList<T> extends AbstractList<T>
{
    @Override
    public Object[] toArray()
    {
        synchronized (storage.lock)
        {
            return super.toArray();
        }
    }
    
    @Override
    public <N> N[] toArray(N[] a)
    {
        synchronized (storage.lock)
        {
            return super.toArray(a);
        }
    }
    
    /**
     * The class of the objects that are members of this list.
     * {@link Class#isAnnotationPresent(Class) targetClass.isAnnotationPresent}
     * should always return true.
     */
    private Class targetClass;
    /**
     * The id of this list, which is used to get and set it's items in the
     * proxystorage_collections table.
     */
    private long id;
    private ProxyStorage storage;
    
    StoredList(ProxyStorage storage, Class targetClass,
        long id)
    {
        this.targetClass = targetClass;
        this.id = id;
        this.storage = storage;
    }
    
    @Override
    public T get(int index)
    {
        synchronized (storage.lock)
        {
            try
            {
                PreparedStatement st = storage.connection
                    .prepareStatement("select value from proxystorage_collections where id = ? and index = ?");
                st.setLong(1, id);
                st.setInt(2, index);
                storage.opcount++;
                ResultSet rs = st.executeQuery();
                if (!rs.next())
                {
                    rs.close();
                    st.close();
                    throw new IndexOutOfBoundsException(
                        "The index "
                            + index
                            + " is not within the allowed bounds for this "
                            + "list. The list's id is "
                            + id);
                }
                long ref = rs.getLong("value");
                rs.close();
                st.close();
                Object result = storage.getById(ref,
                    targetClass);
                if (result == null)
                {
                    throw new IllegalStateException(
                        "The object at index "
                            + index
                            + " with id "
                            + ref
                            + " has been removed from the "
                            + "database. This list's id is "
                            + id);
                }
                return (T) result;
            }
            catch (Exception e)
            {
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                throw new IllegalStateException(
                    "An exception was encountered while performing the "
                        + "requested operation.", e);
            }
        }
    }
    
    @Override
    public void add(int index, T element)
    {
        synchronized (storage.lock)
        {
            if (!(element instanceof ProxyObject))
                throw new ClassCastException(
                    "The element specified (an instance of "
                        + element.getClass().getName()
                        + ") is not a ProxyObject.");
            ProxyObject object = (ProxyObject) element;
            try
            {
                /*
                 * Similar to remove(index), adding an element is a complex
                 * operation, because all elements with their index greater or
                 * equal to the index of the element to insert need to have
                 * their indexes shifted up by one.
                 */
                int size = size();
                if (index < 0 || index > size)
                    throw new IndexOutOfBoundsException(
                        "The index "
                            + index
                            + " is out of bounds for the list with id "
                            + id + " and size " + size);
                /*
                 * Ok, we've checked the index, and it is a valid index. Now
                 * we'll shift all elements greater than or equal to the index
                 * up by one. Then we'll do the actual adding.
                 * 
                 * TODO: this should probably all be done in one transaction to
                 * avoid a stored list having a dangling index if the vm that
                 * the proxy storage instance is running under crashes while
                 * performing this operation.
                 */
                PreparedStatement ust = storage.connection
                    .prepareStatement("update proxystorage_collections "
                        + "set index = index + 1 "
                        + "where id = ? and index >= ?");
                ust.setLong(1, id);
                ust.setInt(2, index);
                storage.opcount++;
                ust.execute();
                ust.close();
                /*
                 * All elements after the one that we are going to insert have
                 * now been shifted up the list by one index. Now we'll do the
                 * actual inserting.
                 */
                PreparedStatement st = storage.connection
                    .prepareStatement("insert into proxystorage_collections "
                        + "(id,index,value) values (?,?,?)");
                st.setLong(1, id);
                st.setInt(2, index);
                st.setLong(3, object.getProxyStorageId());
                storage.opcount++;
                st.execute();
                st.close();
                /*
                 * The object has now been added.
                 */
            }
            catch (Exception e)
            {
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                throw new IllegalStateException(
                    "An exception was encountered while performing the "
                        + "requested operation.", e);
            }
        }
    }
    
    /**
     * Returns a new ArrayList that contains a snapshot of the contents of this
     * stored list at this exact point in time. This should be used in
     * preference to creating a new ArrayList with this StoredList passed in as
     * a constructor since this StoredList could be modified while the new
     * ArrayList is in the process of copying it's data.<br/><br/>
     * 
     * Whenever the contents of this list need to be iterated over, the
     * iteration should be performed on a new list returned from this method,
     * instead of this stored list itself. See
     * {@link StoredList the class documentation} for more information.
     * 
     * @return
     */
    public ArrayList<T> isolate()
    {
        synchronized (storage.lock)
        {
            return new ArrayList<T>(this);
        }
    }
    
    @Override
    public T remove(int index)
    {
        synchronized (storage.lock)
        {
            /*
             * Remove is a somewhat more complex operation than set, size, and
             * get, because it has to execute a statement for each object after
             * this one to decrement it's id by one.
             */
            try
            {
                T previous = get(index);
                /*
                 * the call to get(index) will take care of throwing an
                 * IndexOutOfBoundsException if the index specified does not
                 * reference a valid element of this list
                 */
                PreparedStatement rst = storage.connection
                    .prepareStatement("delete from proxystorage_collections "
                        + "where id = ? and index = ?");
                rst.setLong(1, id);
                rst.setInt(2, index);
                storage.opcount++;
                rst.execute();
                rst.close();
                /*
                 * The element has been removed. Now we need to shift all
                 * elements with an index greater than the one removed down one
                 * position.
                 */
                PreparedStatement dst = storage.connection
                    .prepareStatement("update proxystorage_collections"
                        + " set index = index - 1 "
                        + "where id = ? and index > ?");
                dst.setLong(1, id);
                dst.setInt(2, index);
                storage.opcount++;
                dst.execute();
                dst.close();
                return previous;
            }
            catch (Exception e)
            {
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                throw new IllegalStateException(
                    "An exception was encountered while performing the "
                        + "requested operation.", e);
            }
        }
    }
    
    @Override
    public T set(int index, T element)
    {
        synchronized (storage.lock)
        {
            if (!(element instanceof ProxyObject))
                throw new ClassCastException(
                    "The element specified (an instance of "
                        + element.getClass().getName()
                        + " is not a ProxyObject.");
            ProxyObject object = (ProxyObject) element;
            try
            {
                PreparedStatement cst = storage.connection
                    .prepareStatement("select count(*) from proxystorage_collections where id = ? and index = ?");
                cst.setLong(1, id);
                cst.setInt(2, index);
                storage.opcount++;
                ResultSet crs = cst.executeQuery();
                int existingCount = 0;
                if (crs.next())
                    existingCount = crs.getInt(1);
                crs.close();
                cst.close();
                if (existingCount == 0)
                    throw new IndexOutOfBoundsException(
                        "There is no element at index "
                            + index
                            + " in the list with id " + id);
                T previous = get(index);
                PreparedStatement st = storage.connection
                    .prepareStatement("update proxystorage_collections set value = ? where id = ? and index = ?");
                long insertId = object.getProxyStorageId();
                st.setLong(1, insertId);
                st.setLong(2, id);
                st.setInt(3, index);
                storage.opcount++;
                st.execute();
                st.close();
                return previous;
            }
            catch (Exception e)
            {
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                throw new IllegalStateException(
                    "An exception was encountered while performing the "
                        + "requested operation.", e);
            }
        }
    }
    
    @Override
    public int size()
    {
        synchronized (storage.lock)
        {
            try
            {
                PreparedStatement st = storage.connection
                    .prepareStatement("select count(*) from proxystorage_collections where id = ?");
                st.setLong(1, id);
                storage.opcount++;
                ResultSet rs = st.executeQuery();
                boolean hasNext = rs.next();
                int count = 0;
                if (hasNext)
                    count = rs.getInt(1);
                rs.close();
                st.close();
                return count;
            }
            catch (Exception e)
            {
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                throw new IllegalStateException(
                    "An exception was encountered while performing the "
                        + "requested operation.", e);
            }
        }
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final StoredList other = (StoredList) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    @Override
    public boolean add(T e)
    {
        synchronized (storage.lock)
        {
            return super.add(e);
        }
    }
    
    @Override
    public boolean addAll(int index,
        Collection<? extends T> c)
    {
        synchronized (storage.lock)
        {
            return super.addAll(index, c);
        }
    }
    
    @Override
    public void clear()
    {
        synchronized (storage.lock)
        {
            super.clear();
        }
    }
    
    @Override
    public int indexOf(Object o)
    {
        synchronized (storage.lock)
        {
            return super.indexOf(o);
        }
    }
    
    @Override
    public int lastIndexOf(Object o)
    {
        synchronized (storage.lock)
        {
            return super.lastIndexOf(o);
        }
    }
    
    @Override
    protected void removeRange(int fromIndex, int toIndex)
    {
        /*
         * This method, in particular, would benefit from an implementation that
         * performs the delete in 2 queries instead of leaving it up to the
         * superclass, which ends up using 2 queries per index to be deleted
         */
        synchronized (storage.lock)
        {
            super.removeRange(fromIndex, toIndex);
        }
    }
    
    long getProxyStorageId()
    {
        return id;
    }
    
    @Override
    public boolean remove(Object o)
    {
        synchronized (storage.lock)
        {
            return super.remove(o);
        }
    }
    
}
