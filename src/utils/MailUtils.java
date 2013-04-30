/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * 郵件處理
 * @author Sean Chang
 */
public class MailUtils {

	// Email Server
	public static String III_SMPT = "mta1.iii.org.tw";
	public static String CHT_SMPT = "email.cht.com.tw";
	//
	public static String EMAIL_shchang = "shchang@iii.org.tw";
	public static String EMAIL_sean_chang = "sean_chang@cht.com.tw";
    //
	public static String EMAIL_cactus1021 = "cactus1021@yahoo.com.tw";
	public static String EMAIL_sean666666 = "sean666666@gmail.com";
	
	/** 發送郵件 */
	public static void mailTo(final String receiver, final String subject, final String txt){
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.from", EMAIL_sean666666);
		props.put("mail.password", "sher517kin");
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

	public static void main(String[] args) {
		mailTo(EMAIL_sean666666, "中文標題", "中英文abc");
	}
}
