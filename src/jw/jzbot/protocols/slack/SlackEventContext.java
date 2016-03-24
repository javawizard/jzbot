package jw.jzbot.protocols.slack;

import jw.jzbot.ProtocolEventContext;
import org.json.JSONObject;

/**
 * Created by aboyd on 2016-03-23.
 */
public class SlackEventContext extends ProtocolEventContext {
    private JSONObject message;

    public SlackEventContext(JSONObject message) {
        this.message = message;
    }

    public JSONObject getMessage() {
        return message;
    }
}
