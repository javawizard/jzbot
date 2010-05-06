package jw.jzbot.protocols.imap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

import com.sun.mail.imap.IMAPFolder;

import jw.jzbot.Connection;
import jw.jzbot.ConnectionContext;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;
import jw.jzbot.protocols.xmpp.XmppProtocol;

public class ImapProtocol implements Connection
{
	private Store imapStore;
	private IMAPFolder imapInbox;
	private ConnectionContext context;
	String targetImapServer;
	String targetSmtpServer;
	String targetUsername;
	String targetPassword;
	
	@Override
	public void changeNick(String newnick)
	{
	}
	
	@Override
	public void connect() throws IOException, IrcException
	{
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		Store store = null;
		// FIXME: This doesn't do anything to messages sent to us while we're
		// not connected. We need to support this in the future.
		try
		{
			store = session.getStore("imaps");
			store.connect(targetImapServer, targetUsername, targetPassword);
			imapInbox = (IMAPFolder) store.getFolder("Inbox");
			imapInbox.addMessageCountListener(new MessageCountListener()
			{
				public void messagesAdded(MessageCountEvent messageCountEvent)
				{
					System.out.println("New message added");
					Message[] messages = messageCountEvent.getMessages();
					for (Message message : messages)
					{
						processArrivingMessage(message);
					}
				}
				
				public void messagesRemoved(MessageCountEvent arg0)
				{
					System.out.println("Message removed");
				}
			});
			imapInbox.open(Folder.READ_ONLY);
			new Thread()
			{
				public void run()
				{
					try
					{
						imapInbox.idle();
					}
					catch (MessagingException e)
					{
						e.printStackTrace();
					}
				}
			}.start();
		}
		catch (Exception e)
		{
			if (store != null)
			{
				try
				{
					store.close();
				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	protected void processArrivingMessage(Message message)
	{
		try
		{
			String messageContent = message.getContent().toString();
			String messageFrom = ((InternetAddress) message.getFrom()[0]).getAddress();
			String encodedFrom = XmppProtocol.escape(messageFrom);
			// The split thing makes it so we only get the first line of the
			// message, which is what we want to nix any signature that might
			// be on the message and stuff
			context.onPrivateMessage(encodedFrom, "user", encodedFrom, messageContent
					.split("\n")[0]);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void discard()
	{
		disconnect(null);
	}
	
	@Override
	public void disconnect(String message)
	{
		try
		{
			imapStore.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getChannels()
	{
		return new String[0];
	}
	
	@Override
	public String getNick()
	{
		return XmppProtocol.escape(targetUsername);
	}
	
	@Override
	public int getOutgoingQueueSize()
	{
		return 0;
	}
	
	@Override
	public int getProtocolDelimitedLength()
	{
		return 900;// Essentially arbitrary, but we want it short enough that
		// if the user's communicating with us via MMS, things will
		// still work out
	}
	
	@Override
	public User[] getUsers(String channel)
	{
		return new User[0];
	}
	
	@Override
	public void init(ConnectionContext context)
	{
		this.context = context;
	}
	
	@Override
	public boolean isConnected()
	{
		return imapStore.isConnected();
	}
	
	@Override
	public void joinChannel(String channel)
	{
	}
	
	@Override
	public void kick(String channel, String user, String reason)
	{
	}
	
	@Override
	public boolean likesPastebin()
	{
		return true;// TODO: change this in the future so that servers intended
		// to be used by mms can set this to false
	}
	
	@Override
	public void partChannel(String channel, String reason)
	{
	}
	
	@Override
	public void processProtocolFunction(Sink sink, ArgumentList arguments,
			FactContext context)
	{
		// TODO: add a function for getting the full text/subject/recipients/etc
		// of a received email message, and perhaps functions for sending emails
		// (although this would have to be thought out so that spammers can't
		// use it to their advantage)
	}
	
	@Override
	public void sendAction(String target, String message)
	{
		sendMessage(target, "/me " + message);
	}
	
	@Override
	public void sendInvite(String nick, String channel)
	{
	}
	
	@Override
	public void sendMessage(String target, String message)
	{
		// FIXME: implement this, connect to the specified SMTP server and send
		// the message
		System.out.println("Sending message to " + target + ": " + message);
	}
	
	@Override
	public void sendNotice(String target, String message)
	{
		sendMessage(target, "(notice) " + message);
	}
	
	@Override
	public void setEncoding(String string) throws UnsupportedEncodingException
	{
	}
	
	@Override
	public void setLogin(String nick)
	{
	}
	
	@Override
	public void setMessageDelay(long ms)
	{
	}
	
	@Override
	public void setMode(String channel, String mode)
	{
	}
	
	@Override
	public void setName(String nick)
	{
	}
	
	@Override
	public void setTopic(String channel, String topic)
	{
	}
	
	@Override
	public void setVersion(String string)
	{
	}
	
	@Override
	public boolean supportsMessageDelay()
	{
		return false;
	}
	
}
