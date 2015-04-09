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
            return getFunctionFromStorageContainer(this, context.getDatastoreChannel(), name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return listFunctionNamesFromStorageContainer(context.getDatastoreChannel(), prefix);
        }
    },
    SERVER {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(this, context.getDatastoreServer(), name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return listFunctionNamesFromStorageContainer(context.getDatastoreServer(), prefix);
        }
    },
    GLOBAL {
        @Override
        public Function getFunction(FactContext context, String name) {
            return getFunctionFromStorageContainer(this, JZBot.storage, name);
        }

        @Override
        public String[] listFunctionNames(FactContext context, String prefix) {
            return listFunctionNamesFromStorageContainer(JZBot.storage, prefix);
        }
    };

    public abstract Function getFunction(FactContext context, String name);

    public abstract String[] listFunctionNames(FactContext context, String prefix);

    private static Function getFunctionFromStorageContainer(FunctionScope scope, StorageContainer container, String name) {
        if (container == null)
            return null;
        StoredFunction f = container.getStoredFunction(name);
        if (f == null)
            return null;
        // Only include the version if we're a global function to disallow server-specific and channel-specific
        // functions from accessing vaults. I'm sure there's a better way to do this, but this'll do for now.
        // (Rationale being that someone could shadow a global function with a somewhat more nefarious channel function
        // and go on the probability that the person maintaining the vault used by the global function won't notice
        // that, at a particular channel, all of their precious code will be shadowed by code someone else wrote.
        // There really ought to be a better way to do this, though - perhaps force global function calls to always
        // run against other global functions, and require a specific function to be used to invoke something at a
        // different scope? That's probably a better long-term solution. TODO: Do that)
        // Also, TODO: Include something in the name indicating the StorageContainer we located this at
        return new DynamicFunction(name, scope == GLOBAL ? f.getVersionNumber() : null, FactParser.parse(f.getValue(), "{" + name + "}"));
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
