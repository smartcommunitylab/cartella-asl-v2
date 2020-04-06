package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.AssociazioneCompetenze;

@Repository
public interface AssociazioneCompetenzeRepository extends JpaRepository<AssociazioneCompetenze, Long> {

	List<AssociazioneCompetenze> findByRisorsaId(String risorsaId);
	List<AssociazioneCompetenze> findByCompetenzaId(Long competenzaId);

}
