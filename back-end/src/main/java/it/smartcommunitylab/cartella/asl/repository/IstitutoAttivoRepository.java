package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.IstitutoAttivo;

@Repository
public interface IstitutoAttivoRepository extends JpaRepository<IstitutoAttivo, Long> {
  
  @Query("SELECT ia.extId FROM IstitutoAttivo ia")
  List<String> getExtIds();
}
