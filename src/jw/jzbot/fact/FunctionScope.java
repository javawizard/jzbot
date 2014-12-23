package jw.jzbot.fact;

import jw.jzbot.JZBot;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.StorageContainer;
import jw.jzbot.storage.StoredFunction;

/**
 * Created by aboyd on 2014-12-22.
 */
public enum FunctionScope {
    CORE {
        @Override
        public Function getFunction(FactContext context, String name) {
            return FactParser.getFunction(name);
        }
    },
    LOCAL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return context.getLocalFunction(name);
        }
    },
    CHANNEL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(context.getDatastoreChannel(), name);
        }
    },
    SERVER {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(context.getDatastoreServer(), name);
        }
    },
    GLOBAL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(JZBot.storage, name);
        }
    };

    public abstract Function getFunction(FactContext context, String name);

    private static Function getFunctionFromStorageContainer(StorageContainer container, String name) {
        if (container == null)
            return null;
        StoredFunction f = container.getStoredFunction(name);
        if (f == null)
            return null;
        // TODO: Include something in the name indicating the StorageContainer we located this at
        return new DynamicFunction(name, FactParser.parse(f.getValue(), "{" + name + "}"));
    }
}
