package it.smartcommunitylab.cartella.asl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte;
import it.smartcommunitylab.cartella.asl.model.RegistrazioneEnte.Stato;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;

@Repository
public interface RegistrazioneEnteRepository extends JpaRepository<RegistrazioneEnte, Long> {
	
	@Modifying
	@Query("update RegistrazioneEnte reg set reg.stato=(:stato) where reg.id=(:id)")
	public void updateStato(Long id, Stato stato);
	
	public Optional<RegistrazioneEnte> findOneByToken(String token);
	
	public Optional<RegistrazioneEnte> findOneByAziendaId(String aziendaId);
	
	public Optional<RegistrazioneEnte> findOneByAziendaIdAndRole(String aziendaId, ASLRole role);
	
}
