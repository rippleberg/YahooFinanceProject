package com.mercury.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mercury.daos.*;
import com.mercury.beans.*;

@Service
@Transactional
public class RegisterService {
	@Autowired
	private UserDao userDao;
	
	public void register(User user){
		userDao.addUser(user);
	}
	
	public boolean isUserExist(String username) {
		if(userDao.getUserByUsername(username) == null) {
			return false;
		}
		return true;
	}
	
    private static String md5(String string) {  
        MessageDigest md = null;  
        try {  
            md = MessageDigest.getInstance("md5");  
            md.update(string.getBytes());  
            byte[] md5Bytes = md.digest();  
            return bytes2Hex(md5Bytes);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
          
        return null;  
    }  
      
    private static String bytes2Hex(byte[] byteArray)  
    {  
        StringBuffer strBuf = new StringBuffer();  
        for (int i = 0; i < byteArray.length; i++)  
        {  
            if(byteArray[i] >= 0 && byteArray[i] < 16)  
            {  
                strBuf.append("0");  
            }  
            strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));  
        }  
        return strBuf.toString();  
    }    
	
	public void sendMail(String username, String email) {
		final String fromMail = "m.yahoof201506@gmail.com";
		final String password = "mercury201506";
		
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";  
        Properties prop = System.getProperties();
        prop.setProperty("mail.smtp.host", "smtp.gmail.com");   
        prop.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);   
        prop.setProperty("mail.smtp.socketFactory.fallback", "false");   
        prop.setProperty("mail.smtp.port", "465");   
        prop.setProperty("mail.smtp.socketFactory.port", "465");   
        prop.put("mail.smtp.auth", "true");   
        
        
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() { 
            	return new PasswordAuthentication(fromMail, password);
            	} 
            });
        /*
        MimeMessage message = new MimeMessage(session);  
        try {  
            message.setSubject("Account activation mail");  
            message.setSentDate(new Date());  
            message.setFrom(new InternetAddress(fromMail));  
            message.setRecipient(RecipientType.TO, new InternetAddress(email)); 
            String link = "http://localhost:8080/YahooFinanceProject/activateAccount?username=" + username + "&" + "checkcode" + "=" + md5(username);  
            message.setContent("<a href='" + link +"'>Click to activate your account</a>","text/html;charset=utf-8");  
            Transport.send(message);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } */ 
        
        try {		
        	//create a default MimeMessage object
            Message msg = new MimeMessage(session);
            //set From: header field of the header
            msg.setFrom(new InternetAddress(fromMail));
            //set To: header field of the header
            //msg.setRecipient(Message.RecipientType.TO, new InternetAddress("good.qu@gmail.com"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //msg.addRecipient(Message.RecipientType.CC, "good.qu@gmail.com");
            //set Subject: header field
            msg.setSubject("Greetings from Yahoo Finance Trading System"); msg.setSentDate(new Date());
            //msg.setText("Hello!"); 
            
        
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            //now set the actual message
            //messageBodyPart.setText("Dear " + username +",\n\nWelcome to Yahoo Finance!");
            String link = "http://localhost:8080/YahooFinanceProject/activateAccount.html?username=" + username + "&" + "checkcode" + "=" + md5(username);  
            messageBodyPart.setContent("Dear " + username + "<br><br>Welcome to Yahoo Finance!<br>" + 
            		"<a href='" + link +"'>Please click this link to activate your account</a>","text/html;charset=utf-8");  
            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            //set text message part
            multipart.addBodyPart(messageBodyPart);
            
            //send the complete message parts
            msg.setContent(multipart);
            //send message
            Transport.send(msg);
            System.out.println("Message sent successfully!");
        } catch (Exception e) { 
        	System.out.println(e); 
        }
	}
}
