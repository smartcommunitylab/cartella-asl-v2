package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.NotificheStudente;

@Repository
public interface NotificheStudenteRepository extends JpaRepository<NotificheStudente, Long> {
	
	public NotificheStudente findByStudenteIdAndRegistrationToken(String studenteId, String registrationToken);
	
	public List<NotificheStudente> findByStudenteId(String studenteId);

}
