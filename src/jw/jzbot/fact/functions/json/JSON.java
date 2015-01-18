package jw.jzbot.fact.functions.json;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.Sink;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by aboyd on 2015-01-18.
 */
public class JSON {
    public static Object parse(String data) {
        JSONTokener t = new JSONTokener(data.trim());
        Object result = t.nextValue();
        if (t.more())
            throw new RuntimeException("Extra text found after a valid piece of JSON data");
        return result;
    }

    public static Integer parseInt(String data) {
        // TODO: This returns null if we're not given an integer. Should we instead consider erroring out?
        Object value = new JSONTokener(data).nextValue();
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            double v = ((Double) value).doubleValue();
            if (Math.floor(v) != v)
                return null;
            // TODO: Possible truncation here
            return (int) v;
        } else {
            return null;
        }
    }

    public static String encode(Object value) {
        return JSONObject.valueToString(value);
    }

    public static class Nested {
        private Object parent;
        private Object keyOfCurrent;
        private Object toplevel;
        private boolean invalidated = false;
        private boolean create;

        public Nested(String data, boolean create) {
            if (data == null || data.trim().equals(""))
                toplevel = null;
            else
                toplevel = JSON.parse(data);
            this.create = create;
        }

        public void down(String key, boolean encoded) {
            if (invalidated)
                return;

            Object current = genericGetCurrent();

            if (current instanceof JSONObject) {
                Object actualKey = encoded ? (String) JSON.parse(key) : key;
                if (actualKey instanceof String) {
                    parent = current;
                    keyOfCurrent = actualKey;
                } else {
                    throw new RuntimeException("Trying to look up a value in an object using " + actualKey + ", which is not a string");
                }
            } else if (current instanceof JSONArray) {
                Integer index = JSON.parseInt(key);
                if (index != null) {
                    parent = current;
                    keyOfCurrent = index;
                } else {
                    throw new RuntimeException("Trying to look up a value in an array using " + key + ", which is not an integer");
                }
            } else if (create) {
                if (current != null)
                    throw new RuntimeException("Hit a primitive value (" + current + ") while trying to walk down to where we're " +
                            "supposed to be setting a value");
                Object actualKey = encoded ? (JSON.parseInt(key) != null ? JSON.parseInt(key) : JSON.parse(key)) : key;
                if (actualKey instanceof Integer) {
                    current = new JSONArray();
                } else if (actualKey instanceof String) {
                    current = new JSONObject();
                } else {
                    throw new RuntimeException("Trying to use a non-string, non-integer key (" + actualKey + ") while setting a value in " +
                            "an object or array that does not yet exist");
                }
                genericSetCurrent(current);
                parent = current;
                keyOfCurrent = actualKey;
            } else {
                invalidated = true;
            }
        }

        public void down(ArgumentList list, boolean encoded) {
            for (String k : list.evalToArray()) {
                down(k, encoded);
            }
        }

        public void set(String data, boolean encoded) {
            if (invalidated) {
                throw new RuntimeException("BUG: Nested#set used without create = true. This is an internal JZBot " +
                        "bug with whatever code created this Nested instance.");
            }

            genericSetCurrent(encoded ? JSON.parse(data) : data);
        }

        public String get(boolean encode, boolean allowNonString) {
            if (invalidated)
                return "";

            Object result = genericGetCurrent();
            if (result == null)
                return "";
            if (encode || (allowNonString && !(result instanceof String)))
                return JSON.encode(result);
            else if (!(result instanceof String))
                throw new RuntimeException("You're trying to use {json.mod} or {json.emod}, which automatically decode and " +
                        "re-encode the value to be modified as a string, but the value you're trying to modify isn't actually " +
                        "a string");
            else
                return ((String) result);
        }

        public String get(boolean encode) {
            return get(encode, true);
        }

        public String encodedToplevel() {
            if (toplevel == null)
                return "";
            else
                return JSON.encode(toplevel);
        }

        private static Object genericGet(Object container, Object key) {
            if (container instanceof JSONObject) {
                if (key instanceof String)
                    return ((JSONObject) container).opt((String) key);
                else
                    return null;
            } else if (container instanceof JSONArray) {
                if (key instanceof Integer)
                    return ((JSONArray) container).opt((Integer) key);
                else
                    return null;
            } else {
                // TODO: Sure about this?
                return null;
            }
        }

        private static void genericSet(Object container, Object key, Object value) {
            if (container instanceof JSONObject) {
                ((JSONObject) container).put((String) key, value);
            } else if (container instanceof JSONArray) {
                ((JSONArray) container).put((Integer) key, value);
            } else {
                throw new RuntimeException("Not a container");
            }
        }

        private Object genericGetCurrent() {
            if (parent != null)
                return genericGet(parent, keyOfCurrent);
            else
                return toplevel;
        }

        private void genericSetCurrent(Object value) {
            if (parent != null)
                genericSet(parent, keyOfCurrent, value);
            else
                toplevel = value;
        }
    }

}
