package it.smartcommunitylab.cartella.asl.manager;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.repository.RegistrationRepository;

@Repository
@Transactional
public class RegistrationManager extends DataEntityManager {
	@Autowired
	private RegistrationRepository registrationRepository;
	
	public Registration findById(String id) {
		Optional<Registration> optional = registrationRepository.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
}
