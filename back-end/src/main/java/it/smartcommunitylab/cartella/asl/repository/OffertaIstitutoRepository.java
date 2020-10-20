package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.OffertaIstituto;

@Repository
public interface OffertaIstitutoRepository extends JpaRepository<OffertaIstituto, Long> {
	List<OffertaIstituto> findByOffertaId(Long offertaId);
	OffertaIstituto findByOffertaIdAndIstitutoId(Long offertaId, String istitutoId);
}
