package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.RegistrazioneDocente;

@Repository
public interface RegistrazioneDocenteRepository extends JpaRepository<RegistrazioneDocente, Long> {
  
}
