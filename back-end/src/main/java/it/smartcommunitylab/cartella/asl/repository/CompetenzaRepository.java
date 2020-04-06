package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Competenza;

@Repository
public interface CompetenzaRepository extends JpaRepository<Competenza, Long>, PagingAndSortingRepository<Competenza, Long> {

	public Page<Competenza> findCompetenzaByOwnerId(String ownerId, Pageable pageRequest);
	@Query("SELECT c.ownerId FROM Competenza c WHERE c.id = (:id)")
	public String findCompetenzaOwnerId(@Param("id") Long id);
	
}
