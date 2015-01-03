package jw.jzbot.fact;

import jw.jzbot.JZBot;
import jw.jzbot.storage.Channel;
import jw.jzbot.storage.StorageContainer;
import jw.jzbot.storage.StoredFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by aboyd on 2014-12-22.
 */
public enum FunctionScope {
    LOCAL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return context.getLocalFunction(name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return context.getLocalFunctions().keySet().stream()
                    .filter((name) -> prefix == null || name.startsWith(prefix))
                    .toArray((size) -> new String[size]);
        }
    },
    CORE {
        @Override
        public Function getFunction(FactContext context, String name) {
            return FactParser.getFunction(name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return Stream.of(FactParser.getFunctionNames())
                    .filter((name) -> prefix == null || name.startsWith(prefix))
                    .toArray((size) -> new String[size]);
        }
    },
    CHANNEL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(context.getDatastoreChannel(), name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return listFunctionNamesFromStorageContainer(context.getDatastoreChannel(), prefix);
        }
    },
    SERVER {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(context.getDatastoreServer(), name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return listFunctionNamesFromStorageContainer(context.getDatastoreServer(), prefix);
        }
    },
    GLOBAL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(JZBot.storage, name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return listFunctionNamesFromStorageContainer(JZBot.storage, prefix);
        }
    };

    public abstract Function getFunction(FactContext context, String name);

    public abstract String[] listFunctionNames(FactContext context, String prefix);

    private static Function getFunctionFromStorageContainer(StorageContainer container, String name) {
        if (container == null)
            return null;
        StoredFunction f = container.getStoredFunction(name);
        if (f == null)
            return null;
        // TODO: Include something in the name indicating the StorageContainer we located this at
        return new DynamicFunction(name, FactParser.parse(f.getValue(), "{" + name + "}"));
    }

    private static String[] listFunctionNamesFromStorageContainer(StorageContainer container, String prefix) {
        // TODO: Optimize to just run a single query, or perhaps consider caching functions in
        // memory like we do regexes (or just make ProxyStorage better at doing this)
        return container.getStoredFunctions().stream()
                .map((f) -> f.getName())
                .filter((name) -> prefix == null || name.startsWith(prefix))
                .toArray((size) -> new String[size]);
    }
}
