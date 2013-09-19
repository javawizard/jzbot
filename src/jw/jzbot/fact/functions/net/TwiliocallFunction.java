package jw.jzbot.fact.functions.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestResponse;

import net.sf.opengroove.common.utils.StringUtils;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.exceptions.FactoidException;

public class TwiliocallFunction extends Function
{
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String account = StringUtils.readFile(new File("storage/twilio-account")).trim();
        String secret = StringUtils.readFile(new File("storage/twilio-secret")).trim();
        String from = StringUtils.readFile(new File("storage/twilio-from")).trim();
        TwilioRestClient client = new TwilioRestClient(account, secret, null);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Caller", from);
        map.put("Called", arguments.getString(0));
        map.put("Url", arguments.getString(1));
        try
        {
            TwilioRestResponse response = client.request("/2008-08-01/Accounts/"
                    + client.getAccountSid() + "/Calls", "POST", map);
            if (response.isError())
                throw new RuntimeException(response.getHttpStatus()
                        + ": Rest client reported back an error:\n\n"
                        + response.getResponseText());
            sink.write(response.getResponseText());
        }
        catch (Exception e)
        {
            throw new FactoidException("Error while calling from " + from + " to "
                    + arguments.getString(0) + " with callback " + arguments.getString(1),
                    e);
        }
    }
    
    @Override
    public String getHelp(String topic)
    {
        return "Syntax: {twiliocall|<target>|<callback>} -- Uses the Twilio service"
                + " to place a call to the phone number <target>, calling back to <callback> "
                + "when the call has been put through. This function then evaluates to"
                + " an XML document describing the result. Note that the file "
                + "storage/twilio-account must exist, as well as the file \n"
                + "\nstorage/twilio-secret. The file storage/twilio-from must also exist.";
    }
    
}
