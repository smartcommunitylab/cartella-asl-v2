package it.smartcommunitylab.cartella.asl.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.cartella.asl.controller.RegistrazioneEnteController;
import it.smartcommunitylab.cartella.asl.exception.ServiceException;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;

/**
 * Service for sending emails.
 */
@Service
public class MailService {
	private static Log logger = LogFactory.getLog(RegistrazioneEnteController.class);

	@Value("${mail.host}")
	private String mailHost;
	@Value("${mail.port}")
	private Integer mailPort;
	@Value("${mail.user}")
	private String mailUsername;
	@Value("${mail.password}")
	private String mailPassword;
	@Value("${mail.from}")
	private String mailFrom;
	@Value("${mail.url}")
	private String baseUrl;
	
	private Session setMailerSession() {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.host", mailHost);    
		props.put("mail.smtp.socketFactory.port", mailPort);    
		props.put("mail.smtp.port", mailPort);  
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  
		props.put("mail.smtp.localhost", "climb.smartcommunitylab.it");
		Session session = Session.getInstance(props,    
		  new javax.mail.Authenticator() {    
		  	protected PasswordAuthentication getPasswordAuthentication() {    
		  		return new PasswordAuthentication(mailUsername, mailPassword);  
		  	}    
		 	}
		);
		return session;
	}

	private void sendEmail(String to, String subject, String template, Map<String, String> vars) throws Exception {
		try {
			String textMesage = parseTemplate("templates/mail/" + template + ".txt", vars);
			String htmlMessage = parseTemplate("templates/mail/" + template + ".html", vars);
			Multipart multipart = new MimeMultipart();
			// PLAIN TEXT
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(textMesage);
			multipart.addBodyPart(messageBodyPart);
			// HTML TEXT
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlMessage, "text/html");
			multipart.addBodyPart(messageBodyPart);
			
			Session session = setMailerSession();
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailFrom));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));    
      message.setSubject(subject);    
			message.setContent(multipart);
			Transport.send(message);
			logger.info(String.format("sendEmail:%s - %s", to, subject));
		} catch (Exception e) {
			logger.warn(String.format("sendEmail error:%s", e.getMessage()));
			throw new ServiceException("sendEmail error:" + e.getMessage());
		}
	}
	
	private String parseTemplate(String template, Map<String, String> vars) throws Exception {
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(template), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = buf.readLine()) != null) {
			sb.append(line).append("\n");
		}
		String contentString = sb.toString();
		for(String key : vars.keySet()) {
			contentString = contentString.replace("{{" + key + "}}", vars.get(key));
		}
		return contentString; 
	}
	
	public void inviaRichiestaRegistrazione(RegistrazioneEnte reg) throws Exception {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("baseUrl", baseUrl);
		vars.put("token", reg.getToken());
		sendEmail(reg.getEmail(), "Registrazione Ente", "registrazioneEnte", vars);
	}
	
	public void inviaRuoloReferenteAzienda(RegistrazioneEnte reg) throws Exception {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("baseUrl", baseUrl);
		vars.put("token", reg.getToken());
		sendEmail(reg.getEmail(), "Ruolo Referente Azienda", "ruoloReferenteAzienda", vars);
	}

}