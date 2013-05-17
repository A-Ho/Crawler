/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * @author A-Ho
 */
public class MailUtils {

	// Email Server
	public static String SMPT_GOOGLE = "smtp.gmail.com";
	public static String SMPT_PORT_GOOGLE = "465";
	//
	public static String EMAIL_sean666666 = "sean666666@gmail.com";
	
	public static void mailTo(final String receiver, final String subject, final String txt){
		Properties props = new Properties();
		props.put("mail.smtp.host", SMPT_GOOGLE);
		props.put("mail.smtp.port", SMPT_PORT_GOOGLE);
		props.put("mail.from", EMAIL_sean666666);
		props.put("mail.password", "");
		Session session = Session.getInstance(props, null);
		
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom();
			msg.setRecipients(Message.RecipientType.TO, receiver);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(txt);
			Transport.send(msg);
		} catch (MessagingException mex) {
			System.out.println("send failed, exception: " + mex);
		}
	}

}
