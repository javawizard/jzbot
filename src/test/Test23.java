package test;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.swing.JOptionPane;

import com.sun.mail.imap.IMAPFolder;

public class Test23
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String password = JOptionPane.showInputDialog("Enter the magic word. "
				+ "(and no, it's not \"please\".)");
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try
		{
			System.out.println("test");
			Session session = Session.getDefaultInstance(props, null);
			System.out.println("test");
			Store store = session.getStore("imaps");
			System.out.println("test");
			store.connect("imap.gmail.com", "javawizard@trivergia.com", password);
			System.out.println("test");
			IMAPFolder inbox = (IMAPFolder) store.getFolder("Inbox");
			System.out.println("test");
			inbox.addMessageCountListener(new MessageCountListener()
			{
				public void messagesAdded(MessageCountEvent messageCountEvent)
				{
					System.out.println("New message added");
					Message[] messages = messageCountEvent.getMessages();
					for (Message message : messages)
					{
						System.out.println(message);
					}
				}
				
				public void messagesRemoved(MessageCountEvent arg0)
				{
					System.out.println("Message removed");
				}
			});
			System.out.println("test");
			inbox.open(Folder.READ_ONLY);
			System.out.println("About to idle");
			inbox.idle();
			System.out.println("Dead.");
		}
		catch (NoSuchProviderException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
			System.exit(2);
		}
	}
	
}
