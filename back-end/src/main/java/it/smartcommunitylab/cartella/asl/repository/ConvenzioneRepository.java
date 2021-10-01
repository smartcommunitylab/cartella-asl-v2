package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Convenzione;

@Repository
public interface ConvenzioneRepository extends JpaRepository<Convenzione, Long> {

	public List<Convenzione> findByIstitutoIdAndEnteId(String istitutoId, String enteId, Sort sort);
	
	public Convenzione findByUuid(String uuid);

}
