package jw.jzbot.vault;

/**
 * Created by aboyd on 2015-04-08.
 */
public enum AllowanceType {
    FACTOID, FUNCTION;

    public String getType() {
        if (this == FACTOID) {
            return "factoid";
        } else {
            return "function";
        }
    }

    public static AllowanceType fromType(String type) {
        if (type.equals("factoid")) {
            return FACTOID;
        } else if (type.equals("function")) {
            return FUNCTION;
        } else {
            return null;
        }
    }
}
