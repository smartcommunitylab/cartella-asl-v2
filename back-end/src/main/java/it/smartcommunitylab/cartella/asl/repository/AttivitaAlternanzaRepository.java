package it.smartcommunitylab.cartella.asl.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza.Stati;

@Repository
public interface AttivitaAlternanzaRepository extends JpaRepository<AttivitaAlternanza, Long> {
	@Modifying
	@Query("update AttivitaAlternanza aa0 set titolo=:#{#aa.titolo}, descrizione=:#{#aa.descrizione}, "
			+ "stato=:#{#aa.stato}, dataInizio=:#{#aa.dataInizio}, dataFine=:#{#aa.dataFine}, oraInizio=:#{#aa.oraInizio}, "
			+ "oraFine=:#{#aa.oraFine}, ore=:#{#aa.ore}, offertaId=:#{#aa.offertaId}, titoloOfferta=:#{#aa.titoloOfferta}, "
			+ "enteId=:#{#aa.enteId}, nomeEnte=:#{#aa.nomeEnte}, referenteScuola=:#{#aa.referenteScuola}, "
			+ "referenteScuolaCF=:#{#aa.referenteScuolaCF}, referenteEsterno=:#{#aa.referenteEsterno}, "
			+ "referenteEsternoCF=:#{#aa.referenteEsternoCF}, formatore=:#{#aa.formatore}, formatoreCF=:#{#aa.formatoreCF}, "
			+ "luogoSvolgimento=:#{#aa.luogoSvolgimento}, latitude=:#{#aa.latitude}, longitude=:#{#aa.longitude} where id = :#{#aa.id}")
	public void update(@Param("aa") AttivitaAlternanza aa);

	@Query("SELECT COUNT(aa) FROM AttivitaAlternanza aa WHERE aa.uuid IN (:ids)")
	public int getCountOfAttivitaAlternanza(String[] ids);

	@Modifying
	@Query("update AttivitaAlternanza aa set aa.stato=(:stato) where aa.id=(:id)")
	public void updateStato(Long id, Stati stato);

	@Modifying
	@Query("update AttivitaAlternanza aa set aa.dataArchiviazione=(:dataArchiviazione) where aa.id=(:id)")
	public void updateDataArchiviazione(Long id, LocalDate dataArchiviazione);
	
	@Query("SELECT COUNT(aa) FROM AttivitaAlternanza aa WHERE aa.offertaId=(:offertaId)")
	public int countByOffertaId(Long offertaId);
	
	public List<AttivitaAlternanza> findAttivitaAlternanzaByEnteId(String enteId);
	
	public AttivitaAlternanza findByUuid(String uuid);
}
