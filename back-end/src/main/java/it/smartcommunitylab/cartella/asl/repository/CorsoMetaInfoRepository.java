package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.CorsoMetaInfo;

@Repository
public interface CorsoMetaInfoRepository extends JpaRepository<CorsoMetaInfo, String> {
  @Query("SELECT cmi FROM CorsoMetaInfo cmi WHERE cmi.extId = (:extId) AND cmi.origin = (:origin)")
  public CorsoMetaInfo findByExtId(String extId, String origin);  
}