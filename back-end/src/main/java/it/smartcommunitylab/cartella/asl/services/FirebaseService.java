package it.smartcommunitylab.cartella.asl.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import it.smartcommunitylab.cartella.asl.model.NotificheStudente;
import it.smartcommunitylab.cartella.asl.repository.NotificheStudenteRepository;

@Component
public class FirebaseService {
	private static Log logger = LogFactory.getLog(FirebaseService.class);
	
	@Autowired
	private NotificheStudenteRepository notificheStudenteRepository;
	
	@PostConstruct
	public void init() {
		//FirebaseApp.initializeApp();
	}
	
	public void sendNotification(String title, String msg, List<String> studenti) {
		List<String> registrationTokens = new ArrayList<String>();
		for(String studenteId : studenti) {
			List<NotificheStudente> list = notificheStudenteRepository.findByStudenteId(studenteId);
			for(NotificheStudente notifica : list) {
				if(!registrationTokens.contains(notifica.getRegistrationToken())) {
					registrationTokens.add(notifica.getRegistrationToken());
				}
			}
		}
		if(!registrationTokens.isEmpty()) {
			Notification notification = Notification.builder()
					.setTitle(title)
					.setBody(msg)
					.build();
			MulticastMessage message = MulticastMessage.builder()
					.setNotification(notification)
					.addAllTokens(registrationTokens)
					.build();
			try {
				BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
				if(logger.isInfoEnabled()) {
					logger.info(String.format("sendNotification:%s / %s", registrationTokens.size(), response.getSuccessCount()));
				}
			} catch (FirebaseMessagingException e) {
				logger.warn(String.format("sendNotification:%s", e.getMessage()));
			}			
		}
	}
}
