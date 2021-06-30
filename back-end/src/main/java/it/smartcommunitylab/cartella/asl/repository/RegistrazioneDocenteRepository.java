package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.RegistrazioneDocente;

@Repository
public interface RegistrazioneDocenteRepository extends JpaRepository<RegistrazioneDocente, Long> {
  public List<RegistrazioneDocente> findByIstitutoId(String istitutoId, Sort sort);

  public RegistrazioneDocente findOneByCfDocente(String cfDocente);

  public RegistrazioneDocente findOneByIstitutoIdAndCfDocente(String istitutoId, String cfDocente);
}
