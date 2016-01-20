package jw.jzbot.fact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aboyd on 2016-01-19.
 */
public class LocalVarSavepoint {
    private FactContext context;
    private String[] varNames;
    private Map<String, String> old;
    private List<String> nonexistent;

    public LocalVarSavepoint(FactContext context, String[] varNames) {
        this.context = context;
        this.varNames = varNames;
    }

    public LocalVarSavepoint(FactContext context, String varName) {
        this(context, new String[]{varName});
    }

    public void save() {
        this.old = new HashMap<String, String>();
        this.nonexistent = new ArrayList<String>();
        for (String name : this.varNames) {
            if (context.getLocalVars().containsKey(name))
                this.old.put(name, context.getLocalVars().get(name));
            else
                this.nonexistent.add(name);
        }
    }

    public void restore() {
        for (Map.Entry<String, String> e : this.old.entrySet()) {
            this.context.getLocalVars().put(e.getKey(), e.getValue());
        }
        for (String name : this.nonexistent) {
            this.context.getLocalVars().remove(name);
        }
    }
}
