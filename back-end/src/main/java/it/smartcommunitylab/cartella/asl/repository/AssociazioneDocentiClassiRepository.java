package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.AssociazioneDocentiClassi;

@Repository
public interface AssociazioneDocentiClassiRepository extends JpaRepository<AssociazioneDocentiClassi, Long> {
  List<AssociazioneDocentiClassi> findByRegistrazioneDocenteId(Long registrazioneDocenteId);
  Optional<AssociazioneDocentiClassi> findOneByRegistrazioneDocenteIdAndProfessoriClassiId(Long registrazioneDocenteId, String professoriClassiId);
}
