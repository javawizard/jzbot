package jw.jzbot.protocols.imap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.smtp.SMTPTransport;

import jw.jzbot.ConnectionContext;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Sink;
import jw.jzbot.protocols.Connection;
import jw.jzbot.protocols.xmpp.XmppConnection;

public class ImapConnection implements Connection
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
		// FIXME: This doesn't do anything to messages sent to us while we're
		// not connected. We need to support this in the future.
		try
		{
			imapStore = session.getStore("imaps");
			imapStore.connect(targetImapServer, targetUsername, targetPassword);
			imapInbox = (IMAPFolder) imapStore.getFolder("Inbox");
			imapInbox.addMessageCountListener(new MessageCountListener()
			{
				public void messagesAdded(MessageCountEvent messageCountEvent)
				{
					System.out.println("New messages added");
					new Thread()
					{
						public void run()
						{
							readNewMessages();
						}
					}.start();
				}
				
				public void messagesRemoved(MessageCountEvent arg0)
				{
					System.out.println("Messages removed");
				}
			});
			imapInbox.open(Folder.READ_WRITE);
			new Thread()
			{
				public void run()
				{
					try
					{
						while (imapStore.isConnected())
						{
							System.out.println("Starting mail IDLE thread");
							imapInbox.idle();
							System.out.println("Mail idle thread died, restarting");
						}
						System.out.println("MAIL IDLE THREAD SHUTTING DOWN");
						disconnect(null);
						context.onDisconnect();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						disconnect(null);
					}
				}
			}.start();
			new Thread()
			{
				public void run()
				{
					readNewMessages();
				}
			}.start();
		}
		catch (Exception e)
		{
			if (imapStore != null)
			{
				try
				{
					imapStore.close();
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
	
	protected synchronized void readNewMessages()
	{
		try
		{
			Message[] messages = imapInbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
			for (Message message : messages)
			{
				processArrivingMessage(message);
				message.setFlag(Flag.SEEN, true);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void processArrivingMessage(Message message)
	{
		try
		{
			String messageContent = null;
			Object contentObject = message.getContent();
			if (contentObject instanceof String)
				messageContent = (String) contentObject;
			else if (contentObject instanceof MimeMultipart)
			{
				MimeMultipart multipart = (MimeMultipart) contentObject;
				messageContent = extractMultipartContent(multipart, false);
				if (messageContent == null)
					messageContent = extractMultipartContent(multipart, true);
			}
			if (messageContent == null)
			{
				System.out.println("Message rejected: content not recognized");
				return;
			}
			String messageFrom = ((InternetAddress) message.getFrom()[0]).getAddress();
			String encodedFrom = XmppConnection.escape(messageFrom);
			// The split thing makes it so we only get the first line of the
			// message, which is what we want to nix any signature that might
			// be on the message and stuff
			if (!messageFrom.equalsIgnoreCase(targetUsername))// prevent against
				// the bot emailing itself to stop potential infinite loops
				context.onPrivateMessage(encodedFrom, "user", encodedFrom, messageContent
						.split("\n")[0]);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	private String extractMultipartContent(MimeMultipart multipart, boolean html)
			throws MessagingException, IOException
	{
		for (int i = 0; i < multipart.getCount(); i++)
		{
			BodyPart part = multipart.getBodyPart(i);
			if (part.getDisposition() == null
					|| !part.getDisposition().equals(BodyPart.ATTACHMENT))
			{
				System.out.println("Content type is " + part.getContentType());
				System.out.println("Content is " + part.getContent());
				if ((!html) && part.getContentType().toLowerCase().startsWith("text/plain"))
				{
					return part.getContent().toString();
				}
				else if (html && part.getContentType().toLowerCase().startsWith("text/html"))
				{
					String text = part.getContent().toString().trim();
					System.out.println("Initial HTML: " + text);
					text = text.replaceAll("<title>[^<]*</title>", "");
					text = text.replaceAll("<[^>]*>", "");
					text = text.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;",
							">").replace("&quot;", "\"").replace("&amp;", "&");
					text = text.trim();
					System.out.println("Filtered HTML: " + text);
					return text;
				}
				else if (part.getContentType().toLowerCase().startsWith("multipart/")
						&& part.getContent() instanceof MimeMultipart)
				{
					System.out.println("Got a multipart, " + part.getContentType());
					String result = extractMultipartContent((MimeMultipart) part
							.getContent(), html);
					if (result != null)
						return result;
				}
			}
		}
		return null;
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
		return XmppConnection.escape(targetUsername);
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
		String serverSpec = context.getServer();
		String[] serverSplit = serverSpec.split("\\:");
		targetImapServer = serverSplit[0];
		targetSmtpServer = serverSplit[1];
		targetUsername = context.getNick();
		targetPassword = context.getPassword();
	}
	
	@Override
	public boolean isConnected()
	{
		if (imapStore == null)
			return false;
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
		return false;// TODO: change this in the future so that servers intended
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
		try
		{
			String recipient = XmppConnection.unescape(target);
			System.out.println("Sending email to " + recipient);
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtps.auth", "true");
			props.put("mail.smtps.host", targetSmtpServer);
			Session session = Session.getInstance(props, null);
			Message m = new MimeMessage(session);
			m.setFrom(new InternetAddress(targetUsername));
			m.setRecipient(RecipientType.TO, new InternetAddress(recipient));
			m.setText(message);
			m.setSentDate(new Date());
			SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
			try
			{
			    System.out.println("SMTP Connecting");
				t.connect(targetSmtpServer, targetUsername, targetPassword);
				System.out.println("SMTP Sending");
				t.sendMessage(m, m.getAllRecipients());
				System.out.println("SMTP Done");
			}
			finally
			{
				try
				{
					t.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
		    System.out.println("SMTP Exception");
			e.printStackTrace();
		}
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
