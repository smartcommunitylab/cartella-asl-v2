package it.smartcommunitylab.cartella.asl.manager;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.TeachingUnit;
import it.smartcommunitylab.cartella.asl.repository.TeachingUnitRepository;

@Repository
@Transactional
public class TeachingUnitManager {
	@Autowired
	private TeachingUnitRepository tuRepository;
	
	public TeachingUnit findById(String id) {
		Optional<TeachingUnit> optional = tuRepository.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
}
