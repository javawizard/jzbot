package jw.jzbot.protocols.slack;

import jw.jzbot.ConnectionContext;
import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.output.DelimitedSink;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;
import jw.jzbot.protocols.Connection;
import net.sf.opengroove.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jibble.pircbot.IrcException;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlackConnection implements Connection {
    private ConnectionContext context;
    private WebSocketClientSubclass webSocket = null;
    private AtomicLong nextMessageId = new AtomicLong(1);

    private User self;

    private Map<String, User> usersById = new HashMap<String, User>();
    private Map<String, User> usersByName = new HashMap<String, User>();
    private Map<String, Channel> channelsById = new HashMap<String, Channel>(); // Channels and groups
    private Map<String, Channel> channelsByName = new HashMap<String, Channel>(); // Channels only, without leading '#'s
    private Map<String, Channel> groupsByName = new HashMap<String, Channel>(); // Groups only
    private Map<String, Bot> botsById = new HashMap<String, Bot>();
    private Map<String, Emoji> emojiByName = new HashMap<String, Emoji>();

    public Channel addChannel(JSONObject object) {
        Channel channel = new Channel(object);
        channelsById.put(channel.id, channel);
        Map<String, Channel> byName = channel.type == ChannelType.CHANNEL ? channelsByName : channel.type == ChannelType.GROUP ? groupsByName : null;
        byName.put(channel.name, channel);

        if (channel.topic != null) {
            context.onTopic(
                    slackTargetToIrc(channel),
                    channel.topic.value,
                    slackTargetToIrc(channel.topic.creator),
                    slackTargetToIrc(channel.topic.creator),
                    channel.topic.creator == null ? null : channel.topic.creator.id,
                    channel.topic.lastSet,
                    false
            );
        }

        return channel;
    }

    public Channel updateChannel(JSONObject object) {
        Channel channel = channelsById.get(object.getString("id"));
        if (channel == null) {
            // Happens if, say, it's a group that we just found out about because we just got invited to it
            return addChannel(object);
        }
        Map<String, Channel> byName = channel.type == ChannelType.CHANNEL ? channelsByName : channel.type == ChannelType.GROUP ? groupsByName : null;
        byName.remove(channel.name);
        channel.load(object);
        byName.put(channel.name, channel);
        return channel;
    }

    public User addUser(JSONObject object) {
        User user = new User(object);
        usersById.put(user.id, user);
        usersByName.put(user.name, user);
        return user;
    }

    public User updateUser(JSONObject object) {
        User user = usersById.get(object.getString("id"));
        usersByName.remove(user.name);
        user.load(object);
        usersByName.put(user.name, user);
        return user;
    }

    public Bot addBot(JSONObject object) {
        Bot bot = new Bot(object);
        botsById.put(bot.id, bot);
        return bot;
    }

    public Bot updateBot(JSONObject object) {
        Bot bot = botsById.get(object.getString("id"));
        bot.load(object);
        return bot;
    }

    private void updateEmoji() {
        System.out.println("Requesting emoji list...");
        JSONObject response = api("emoji.list").call().getJSONObject("emoji");
        System.out.println("Updating emoji...");
        HashMap<String, Emoji> map = new HashMap<String, Emoji>();
        for (String name : JSONObject.getNames(response)) {
            String value = response.getString(name);
            if (value.startsWith("alias:")) {
                map.put(name, new Emoji(name, null, value.substring("alias:".length())));
            } else {
                map.put(name, new Emoji(name, value, null));
            }
        }
        this.emojiByName = map;
        System.out.println("Done. " + this.emojiByName.size() + " emoji found.");
    }

    private class APIRequest {
        private String method;
        private String queryString;

        public APIRequest(String method) {
            this.method = method;
            this.queryString = "token=" + URLEncoder.encode(SlackConnection.this.context.getPassword());
        }

        public APIRequest set(String name, String value) {
            if (name != null && value != null) {
                this.queryString += "&" + URLEncoder.encode(name) + "=" + URLEncoder.encode(value);
            }
            return this;
        }

        public APIRequest setAll(JSONObject object) {
            for (String key : JSONObject.getNames(object)) {
                this.set(key, object.getString(key));
            }
            return this;
        }

        public JSONObject call() {
            try {
                HttpClient client = new DefaultHttpClient();
                client.getParams().setParameter("http.socket.timeout", 15 * 1000);
                HttpPost request = new HttpPost("https://slack.com/api/" + this.method);
                request.addHeader("Content-type", "application/x-www-form-urlencoded");
                request.setEntity(new StringEntity(this.queryString));

                HttpResponse response = client.execute(request);

                int responseCode = response.getStatusLine().getStatusCode();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                String responseContent = new String(baos.toByteArray(), "UTF-8");

                if (responseCode != 200) {
                    throw new RuntimeException("Received response code " + responseCode
                            + "(" + response.getStatusLine().getReasonPhrase() + " while invoking " + this.method
                            + ". Response content: " + responseContent);
                }

                JSONObject responseJson = new JSONObject(responseContent);
                if (!responseJson.getBoolean("ok")) {
                    throw new RuntimeException("Slack API reported that the API call failed: " + responseJson.get("error"));
                }

                return responseJson;
            } catch (Exception e) {
                throw new RuntimeException("Exception while calling API method \"" + this.method + "\"", e);
            }
        }
    }

    private class WebSocketClientSubclass extends WebSocketClient {
        public WebSocketClientSubclass(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {

        }

        @Override
        public void onMessage(String message) {
            JSONObject event = new JSONObject(message);
            System.out.println("GOT A SLACK MESSAGE: " + event);

            String type = event.optString("type", null);
            if (type == null) {
                System.out.println("Warning: Slack message received without a type, probably means a command we " +
                        "just tried failed. We're just ignoring this for now. Contents of the message: " + event);
                return;
            }
            String subtype = event.optString("subtype", null);

            if (type.equals("hello")) {
                // Slack's API documentation is ambiguous about whether they'll send us an emoji_changed event if emoji
                // change between us calling rtm.start and actually being connected, so update again just in case.
                updateEmoji();
            } else if (type.equals("message") && (subtype == null || subtype.equals("me_message"))) {
                String text;
                if (event.optString("text", null) != null) {
                    text = decodeSlackMessageText(event.getString("text"));
                } else {
                    text = null;
                }
                String fromHostname = event.getString("user");
                String fromNick = slackTargetNameToIrc(event.getString("user"));
                String toChannel;
                if (!event.getString("channel").startsWith("D")) {
                    toChannel = slackTargetNameToIrc(event.getString("channel"));
                } else {
                    toChannel = null;
                }

                context.withProtocolEventContext(new SlackEventContext(event), () -> {
                    if (toChannel == null && subtype == null) {
                        context.onPrivateMessage(fromNick, fromNick, fromHostname, text);
                    } else if (toChannel == null && subtype.equals("me_message")) {
                        context.onAction(fromNick, fromNick, fromHostname, self.name, text);
                    } else if (subtype == null) {
                        context.onMessage(toChannel, fromNick, fromNick, fromHostname, text);
                    } else if (subtype.equals("me_message")) {
                        context.onAction(fromNick, fromNick, fromHostname, toChannel, text);
                    }
                });
            } else if (type.equals("message") && subtype.equals("bot_message")) {
                String text;
                if (event.optString("text", null) != null) {
                    text = decodeSlackMessageText(event.getString("text"));
                } else {
                    text = null;
                }
                String fromHostname;
                String fromNick = event.getString("username").replace(" ", "").toLowerCase();
                if (event.optString("bot_id") != null) {
                    fromHostname = event.optString("bot_id");
                } else {
                    fromHostname = fromNick;
                }
                String recipient = slackTargetNameToIrc(event.getString("channel"));

                // Had this as a notification before. Makes things like regexes painful. Changing this to be an ordinary
                // message, but in the future there should really be a way to tack on some additional metadata like the
                // fact that this is from an integration.
                context.withProtocolEventContext(new SlackEventContext(event), () -> {
                    if (event.getString("channel").startsWith("D"))
                        context.onPrivateMessage(fromNick, fromNick, fromHostname, text);
                    else
                        context.onMessage(recipient, fromNick, fromNick, fromHostname, text);
                });
            } else if (type.equals("channel_created") || type.equals("group_created")) {
                addChannel(event.getJSONObject("channel"));
            } else if (type.equals("channel_joined") || type.equals("group_joined")) {
                Channel channel = updateChannel(event.getJSONObject("channel"));
                context.onJoin(slackTargetToIrc(channel), self.name, self.name, self.id);
            } else if (type.equals("channel_left") || type.equals("group_left")) {
                Channel channel = channelsById.get(event.getJSONObject("channel"));
                channel.isMember = false;
                channel.members.remove(self);
                context.onPart(slackTargetToIrc(channel), self.name, self.name, self.id);
            } else if (type.equals("message") && (subtype.equals("channel_join") || subtype.equals("group_join"))) {
                Channel channel = channelsById.get(event.getString("channel"));
                User user = usersById.get(event.getString("user"));
                channel.members.add(user);
                context.onJoin(slackTargetToIrc(channel), slackTargetToIrc(user), slackTargetToIrc(user), user.id);
            } else if (type.equals("message") && (subtype.equals("channel_leave") || subtype.equals("group_leave"))) {
                Channel channel = channelsById.get(event.getString("channel"));
                User user = usersById.get(event.getString("user"));
                channel.members.remove(user);
                context.onPart(slackTargetToIrc(channel), slackTargetToIrc(user), slackTargetToIrc(user), user.id);
            } else if (type.equals("channel_rename") || type.equals("group_rename")) {
                System.out.println("Warning: Slack channel/group renames are not yet supported. We're about to " +
                        "automatically disconnect.");
                disconnect("");
                // Force reconnect immediately
                JZBot.notifyConnectionCycleThread();
            } else if (type.equals("im_created")) {
                usersById.get(event.getString("user")).directMessageId = event.getJSONObject("channel").getString("id");
            } else if (type.equals("bot_added")) {
              addBot(event.getJSONObject("bot"));
            } else if (type.equals("bot_changed")) {
              updateBot(event.getJSONObject("bot"));
            } else if (type.equals("emoji_changed")) {
                updateEmoji();
            } else if (type.equals("message") && (subtype.equals("channel_topic") || subtype.equals("group_topic"))) {
                Channel channel = channelsById.get(event.getString("channel"));
                User user = usersById.get(event.getString("user"));
                long lastSet = System.currentTimeMillis(); // TODO: Can we pull this from the event

                channel.topic.value = event.getString("topic");
                channel.topic.creator = user;
                channel.topic.lastSet = lastSet;

                context.onTopic(
                        slackTargetToIrc(channel),
                        channel.topic.value,
                        slackTargetToIrc(user),
                        slackTargetToIrc(user),
                        event.getString("user"),
                        lastSet,
                        true
                        );
            } else {
                System.out.println("Received unknown Slack event: " + event);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Slack WebSocket error: " + code + " " + reason);
            context.onDisconnect();
        }

        @Override
        public void onError(Exception ex) {
            // Buh?
            System.out.println("Slack onError called... was that supposed to happen?");
            ex.printStackTrace();
            webSocket.close();
            context.onDisconnect();
        }
    }

    private class Emoji {
        public String name;
        public String url;
        public String alias;

        public Emoji(String name, String url, String alias) {
            this.name = name;
            this.url = url;
            this.alias = alias;
        }
    }

    private class MessageTarget {
        public String id;
        public String name;
    }

    private class Bot extends MessageTarget {
      public Bot(JSONObject object) {
        load(object);
      }

      public void load(JSONObject object) {
        System.out.println("LOADING BOT FROM " + object);
        this.id = object.getString("id");
        this.name = object.getString("name");
      }
    }

    private class User extends MessageTarget implements org.jibble.pircbot.User {
        public boolean deleted;
        public boolean isAdmin;
        public boolean isOwner;
        public String firstName;
        public String lastName;
        public String realName;

        public String directMessageId;

        public User(JSONObject object) {
            load(object);
        }

        public void load(JSONObject object) {
            System.out.println("LOADING USER FROM " + object);
            this.id = object.getString("id");
            this.name = object.getString("name");
            this.deleted = object.optBoolean("deleted");

            JSONObject profile = object.optJSONObject("profile");
            if (profile != null) {
              this.isAdmin = profile.optBoolean("is_admin");
              this.isOwner = profile.optBoolean("is_owner");
              this.firstName = profile.optString("first_name");
              this.lastName = profile.optString("last_name");
              this.realName = profile.optString("real_name");
            } else {
              this.isAdmin = false;
              this.isOwner = false;
              this.firstName = null;
              this.lastName = null;
              this.realName = null;
            }
        }

        @Override
        public boolean isOp() {
            // TODO: Not even sure what this is, but better expose it somehow, right?
            return isAdmin;
        }

        @Override
        public boolean isHalfop() {
            return false;
        }

        @Override
        public boolean isAdmin() {
            return false;
        }

        @Override
        public boolean isFounder() {
            return false;
        }

        @Override
        public boolean hasVoice() {
            return false;
        }

        @Override
        public String getNick() {
            return this.name;
        }
    }

    public class Topic {
        public String value;
        public User creator;
        public long lastSet;

        public Topic(JSONObject object) {
            load(object);
        }

        public void load(JSONObject object) {
            this.value = object.getString("value");
            this.creator = usersById.get(object.getString("creator"));
            this.lastSet = object.getLong("last_set");
        }
    }

    private enum ChannelType { CHANNEL, GROUP }

    private class Channel extends MessageTarget {
        public ChannelType type;
        public long created;
        public User creator;
        public boolean isArchived;
        public Set<User> members = new HashSet<User>();
        public Topic topic;
        public Topic purpose;
        public boolean isMember;

        public Channel(JSONObject object) {
            if (object.getString("id").startsWith("G")) {
                this.type = ChannelType.GROUP;
            } else {
                this.type = ChannelType.CHANNEL;
            }
            load(object);
        }

        public void load(JSONObject object) {
            System.out.println("LOADING CHANNEL FROM " + object);
            this.id = object.getString("id");
            this.name = object.getString("name");
            this.created = object.getLong("created");
            this.creator = usersById.get(object.getString("creator"));
            this.isArchived = object.has("is_archived") ? object.getBoolean("is_archived") : false;
            this.members.clear();
            if (object.has("members")) { // Won't have any if we haven't joined etc.
                JSONArray members = object.getJSONArray("members");
                for (int i = 0; i < members.length(); i++) {
                    this.members.add(usersById.get(members.getString(i)));
                }
            }
            if (object.has("topic")) {
                this.topic = new Topic(object.getJSONObject("topic"));
            }
            if (object.has("purpose"))
                this.purpose = new Topic(object.getJSONObject("topic"));

            if (this.type == ChannelType.GROUP) {
                this.isMember = true;
            } else {
                this.isMember = object.optBoolean("is_member", false); // doesn't exist on some objects apparently
            }
        }
    }

    private MessageTarget ircTargetToSlack(String target) {
        if (target.startsWith("##")) {
            // private group
            return groupsByName.get(target.substring(2));
        } else if (target.startsWith("#")) {
            // channel
            return channelsByName.get(target.substring(1));
        } else {
            // user
            return usersByName.get(target);
        }
    }

    private String slackTargetToIrc(MessageTarget target) {
        if (target == null) {
            return null;
        } else if (target instanceof Channel) {
            Channel channel = (Channel) target;
            if (channel.type == ChannelType.GROUP) {
                return "##" + channel.name;
            } else {
                return "#" + channel.name;
            }
        } else {
            return target.name;
        }
    }

    private String slackTargetNameToIrc(String target) {
        if (usersById.containsKey(target)) {
            return slackTargetToIrc(usersById.get(target));
        } else if (channelsById.containsKey(target)) {
            return slackTargetToIrc(channelsById.get(target));
        } else {
            return null;
        }
    }

    private String decodeSlackMessageText(String slackText) {
        StringBuffer s = new StringBuffer();
        Matcher m = Pattern.compile("<(.*?)>").matcher(slackText);
        while (m.find()) {
            String content = m.group(1);
            String replacement;
            if (content.startsWith("@")) {
                String[] split = content.substring(1).split("\\|");
                if (split.length > 1)
                    replacement = "@" + split[1];
                else
                    replacement = "@" + usersById.get(split[0]).name;
            } else if (content.startsWith("#C")) {
                String[] split = content.substring(1).split("\\|");
                if (split.length > 1)
                    replacement = "#" + split[1];
                else
                    replacement = "#" + channelsById.get(split[0]).name;
            } else if (content.startsWith("!")) {
                // The dreaded @channel/@group/@everyone
                replacement = "@" + content.substring(1);
            } else {
                // Just pass it through
                System.out.println("Unknown Slack escape sequence, assuming it's a URL: <" + content + ">");
                replacement = content;
            }
            m.appendReplacement(s, replacement);
        }
        m.appendTail(s);
        String result = s.toString();

        result = result.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");

        System.out.println("Decoded Slack message:\n" + slackText + "\nto message:\n" + result);
        return result;
    }

    private String encodeSlackMessageText(String ircText) {
        // TODO: Implement @user and #channel parsing later... or maybe don't, and just leave it to the
        // (soon-to-be) formatted text system to handle that (so users have to {slackuser|foo} to get @foo)
        String result = ircText.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        System.out.println("Encoded Slack message:\n" + ircText + "\nto message:\n" + result);
        return result;
    }

    private APIRequest api(String method) {
        return new APIRequest(method);
    }

    @Override
    public void init(ConnectionContext context) {
        this.context = context;
    }

    @Override
    public void sendAction(String target, String message) {
        // I'm still waiting to hear back from Slack about how to actually do this given bot API limitations, so for
        // now, do the dirtiest thing ever and emulate it with italicised text.
        sendMessage(target, "_" + message + "_");
    }

    @Override
    public void sendMessage(String target, String message) {
        System.out.println("Asked to send Slack message to target \"" + target + "\"");

        message = encodeSlackMessageText(message);

        // Since Slack doesn't seem to want to respect "parse": "full" from us...
        StringBuffer s = new StringBuffer();
        Matcher m = Pattern.compile("(?<=^| |\\()([@#])([a-z0-9\\-_]+)").matcher(message);
        while (m.find()) {
            String replacement = m.group(1) + m.group(2);
            if (m.group(1).equals("@") && usersByName.containsKey(m.group(2))) {
                replacement = "<@" + usersByName.get(m.group(2)).id + ">";
            } else if (m.group(1).equals("#") && channelsByName.containsKey(m.group(2))) {
                replacement = "<#" + channelsByName.get(m.group(2)).id + ">";
            }
            m.appendReplacement(s, replacement);
        }
        m.appendTail(s);
        message = s.toString();
        System.out.println("Slack message after encoding #channels and @users: " + message);

        MessageTarget slackTarget = ircTargetToSlack(target);
        if (slackTarget == self) {
          System.out.println("Asked to send a message to ourselves - skipping.");
          return;
        }
        String channelId = slackTarget.id;
        if (slackTarget instanceof User) {
            User user = (User) slackTarget;
            channelId = user.directMessageId;
            if (channelId == null) {
                // TODO: Test this out
                user.directMessageId = api("im.open").set("user", slackTarget.id).call().getJSONObject("channel").getString("id");
                channelId = user.directMessageId;
            }
        }

        System.out.println("Sending message to Slack id " + channelId + ": " + message);

        webSocket.send(new JSONObject()
                .put("id", nextMessageId.getAndIncrement())
                .put("type", "message")
                .put("channel", channelId)
                .put("text", message)
                .put("parse", "full")
                .put("link_names", 1)
                .toString());
    }

    @Override
    public void sendNotice(String target, String message) {
        // Not really sure if there's any good Slack equivalent we can map this to...
        sendMessage(target, "[notice] " + message);
    }

    @Override
    public void sendInvite(String nick, String channel) {
        // Not supported by Slack bots
    }

    @Override
    public void setMessageDelay(long ms) {
        // I really need to move implementation of this method up into JZBot itself - protocols shouldn't have to
        // worry about this.
    }

    @Override
    public boolean supportsMessageDelay() {
        return false;
    }

    @Override
    public void setLogin(String nick) {

    }

    @Override
    public void setName(String nick) {

    }

    @Override
    public void setVersion(String string) {

    }

    @Override
    public void setEncoding(String string) throws UnsupportedEncodingException {

    }

    @Override
    public void connect() throws IOException, IrcException {
        try {
            JSONObject rtmInfo = api("rtm.start").call();

            String selfId = rtmInfo.getJSONObject("self").getString("id");

            for (Object userObject : rtmInfo.getJSONArray("users").myArrayList) {
                JSONObject user = (JSONObject) userObject;
                addUser(user);
            }

            for (Object channelObject : rtmInfo.getJSONArray("channels").myArrayList) {
                JSONObject channel = (JSONObject) channelObject;
                addChannel(channel);
            }

            for (Object channelObject : rtmInfo.getJSONArray("groups").myArrayList) {
                JSONObject channel = (JSONObject) channelObject;
                addChannel(channel);
            }

            for (Object imObject : rtmInfo.getJSONArray("ims").myArrayList) {
                JSONObject im = (JSONObject) imObject;
                usersById.get(im.getString("user")).directMessageId = im.getString("id");
            }

            for (Object botObject : rtmInfo.getJSONArray("bots").myArrayList) {
              JSONObject bot = (JSONObject) botObject;
              addBot(bot);
            }

            this.self = this.usersById.get(selfId);

            updateEmoji();

            System.out.println("Connecting to Slack WebSocket " + rtmInfo.getString("url"));
            webSocket = new WebSocketClientSubclass(new URI(rtmInfo.getString("url")));

            // Note to self: write up a pull request to Java-WebSocket to make it do this by default for wss:// URLs
            webSocket.setSocket(SSLSocketFactory.getDefault().createSocket());

            if (!webSocket.connectBlocking()) {
                throw new RuntimeException("Couldn't connect");
            }

            for (Channel channel : channelsById.values()) {
                if (channel.isMember) {
                    context.onJoin(slackTargetToIrc(channel), slackTargetToIrc(self), slackTargetToIrc(self), self.id);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNick() {
        return slackTargetToIrc(self);
    }

    @Override
    public String[] getChannels() {
        List<String> results = new ArrayList<String>();
        for (Channel channel : channelsById.values()) {
            if (channel.isMember) {
                results.add(channel.name);
            }
        }
        return results.toArray(new String[results.size()]);
    }

    @Override
    public void joinChannel(String channel) {

    }

    @Override
    public org.jibble.pircbot.User[] getUsers(String channel) {
        return ((Channel)ircTargetToSlack(channel)).members.toArray(new org.jibble.pircbot.User[]{});
    }

    @Override
    public int getProtocolDelimitedLength() {
        return 3000; // Really 4000, but allow for JSON overhead
    }

    @Override
    public void kick(String channel, String user, String reason) {

    }

    @Override
    public void setMode(String channel, String mode) {

    }

    @Override
    public boolean isConnected() {
        return this.webSocket != null && this.webSocket.getConnection().isOpen();
    }

    @Override
    public void partChannel(String channel, String reason) {

    }

    @Override
    public void disconnect(String message) {
        if (webSocket != null) {
            webSocket.close();
        }
    }

    @Override
    public int getOutgoingQueueSize() {
        return 0;
    }

    @Override
    public void setTopic(String channelName, String topic) {
        Channel channel = (Channel) ircTargetToSlack(channelName);
        api("channels.setTopic").set("channel", channel.id).set("topic", topic).call();
    }

    @Override
    public void changeNick(String newnick) {

    }

    @Override
    public void processProtocolFunction(Sink sink, ArgumentList arguments, FactContext context) {
      String name = arguments.resolveString(0);
      arguments = arguments.subList(1);

      if (name.equals("irc->slack")) {
        MessageTarget t = ircTargetToSlack(arguments.resolveString(0));
        if (t != null)
          sink.write(t.id);
      } else if (name.equals("slack->irc")) {
        String s = slackTargetNameToIrc(arguments.resolveString(0));
        if (s != null)
          sink.write(s);
      } else if (name.equals("users")) {
        DelimitedSink s = new DelimitedSink(sink, "\n");
        for (User u : usersById.values()) {
          s.next();
          s.write(slackTargetToIrc(u));
        }
      } else if (name.equals("channels")) {
        DelimitedSink s = new DelimitedSink(sink, "\n");
        for (Channel c : channelsById.values()) {
          s.next();
          s.write(slackTargetToIrc(c));
        }
      } else if (name.equals("bots")) {
        DelimitedSink s = new DelimitedSink(sink, "\n");
        for (Bot b : botsById.values()) {
          s.next();
          s.write(b.id);
        }
      } else if (name.equals("botid->name")) {
        Bot b = botsById.get(arguments.resolveString(0));
        if (b != null) {
          sink.write(b.name);
        }
      } else if (name.equals("user->dmid")) {

      } else if (name.equals("post") && new File("storage/allow-slack-send-raw-data").exists()) {
          // TODO: Copied nearly wholesale from sendMessage. Make this into a common method.
          MessageTarget slackTarget = ircTargetToSlack(arguments.getString(0));
          String channelId = slackTarget.id;
          if (slackTarget instanceof User) {
              User user = (User) slackTarget;
              channelId = user.directMessageId;
              if (channelId == null) {
                  // TODO: Test this out
                  user.directMessageId = api("im.open").set("user", slackTarget.id).call().getJSONObject("channel").getString("id");
                  channelId = user.directMessageId;
              }
          }

          System.out.println("Sending raw Slack message with {p|post} to " + arguments.getString(0) +
                  " (resolved as " + channelId + "): " + arguments.getString(1));

          JSONObject data;
          if (arguments.length() > 2) {
              data = new JSONObject(arguments.resolveString(2));
          } else {
              data = new JSONObject();
          }

          data.put("channel", channelId);
          data.put("text", arguments.getString(1));

          api("chat.postMessage")
                  .setAll(data)
                  .call();
      } else if (name.equals("emoji.list")) {
          List<String> list = new ArrayList<String>();
          list.addAll(emojiByName.keySet());
          Collections.sort(list);
          sink.write(StringUtils.delimited(list.toArray(new String[0]), " "));
      } else if (name.equals("emoji.exists")) {
          String emoji = arguments.resolveString(0);
          sink.write(emojiByName.containsKey(emoji) ? "1" : "0");
      } else if (name.equals("message")) {
          SlackEventContext eventContext = (SlackEventContext) context.getProtocolEventContext();
          if (eventContext != null && eventContext.getMessage() != null) {
              sink.write(eventContext.getMessage().toString());
          }
      } else if (name.equals("reactions.add") || name.equals("react")) {
          addOrRemoveReaction("reactions.add", context, arguments);
      } else if (name.equals("reactions.get")) {
          JSONObject event = new JSONObject(arguments.resolveString(0));
          JSONObject result = api("reactions.get")
                  .set("channel", event.getString("channel"))
                  .set("timestamp", event.has("ts") ? event.getString("ts") : event.getString("timestamp"))
                  .set("full", "true")
                  .call();
          sink.write(result.getJSONObject("message").getJSONArray("reactions").toString());
      } else if (name.equals("reactions.remove")) {
          addOrRemoveReaction("reactions.remove", context, arguments);
      } else if (name.equals("reactions.list")) {
          String user = arguments.length() > 0 ? arguments.resolveString(0) : "";
          if (user.equals("")) {
              user = null;
          } else {
              user = ircTargetToSlack(user).id;
          }
          String pageNumber = arguments.length() > 1 ? arguments.resolveString(1) : "1";
          String count = arguments.length() > 2 ? arguments.resolveString(2) : "10";
          JSONObject response = api("reactions.list")
                  .set("user", user)
                  .set("full", "true")
                  .set("page", pageNumber)
                  .set("count", count)
                  .call();
          sink.write(response.getJSONArray("items").toString());
      } else {
        throw new RuntimeException("Invalid Slack protocol-specific function: " + name);
      }
    }

    @Override
    public void discard() {

    }

    @Override
    public boolean likesPastebin() {
        return true;
    }

    private void addOrRemoveReaction(String apiCall, FactContext context, ArgumentList arguments) {
        String reaction;
        JSONObject event;
        if (arguments.length() == 1) {
            reaction = arguments.resolveString(0);
            SlackEventContext eventContext = (SlackEventContext) context.getProtocolEventContext();
            if (eventContext == null || eventContext.getMessage() == null) {
                throw new RuntimeException("{p|reactions.add} called by a factoid not being run in response to " +
                        "a Slack message. You need to explicitly specify which message to add a reaction to.");
            }
            event = eventContext.getMessage();
        } else {
            event = new JSONObject(arguments.resolveString(0));
            reaction = arguments.resolveString(1);
        }
        api(apiCall)
                .set("name", reaction)
                .set("channel", event.getString("channel"))
                .set("timestamp", event.has("ts") ? event.getString("ts") : event.getString("timestamp"))
                .call();
    }
}
