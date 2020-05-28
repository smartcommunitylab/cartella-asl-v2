package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.AziendaEstera;

@Repository
public interface AziendaEsteraRepository extends JpaRepository<AziendaEstera, Long> {
	
	@Query("SELECT az0 FROM AziendaEstera az0 WHERE az0.partita_iva = (:partitaIva)")
	public AziendaEstera findByPartitaIva(String partitaIva);

}
