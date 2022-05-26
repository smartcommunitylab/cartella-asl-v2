package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.smartcommunitylab.cartella.asl.model.TeachingUnit;

public interface TeachingUnitRepository extends JpaRepository<TeachingUnit, String> {
	TeachingUnit findByExtIdAndOrigin(String extId, String origin);
}
