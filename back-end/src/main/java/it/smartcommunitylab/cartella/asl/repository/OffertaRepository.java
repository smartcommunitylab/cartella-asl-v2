package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Offerta;

@Repository
public interface OffertaRepository extends JpaRepository<Offerta, Long> {
	
	@Modifying
	@Query("UPDATE Offerta off0 SET titolo=:#{#off.titolo}, descrizione=:#{#off.descrizione}, dataInizio=:#{#off.dataInizio}, dataFine=:#{#off.dataFine},"
			+ " oraInizio=:#{#off.oraInizio}, oraFine=:#{#off.oraFine}, annoScolastico=:#{#off.annoScolastico},"
			+ " referenteScuola=:#{#off.referenteScuola}, referenteScuolaCF=:#{#off.referenteScuolaCF}, referenteScuolaTelefono=:#{#off.referenteScuolaTelefono},"
			+ " referenteEsterno=:#{#off.referenteEsterno}, referenteEsternoCF=:#{#off.referenteEsternoCF}, referenteEsternoTelefono=:#{#off.referenteEsternoTelefono},"
			+ " formatore=:#{#off.formatore}, formatoreCF=:#{#off.formatoreCF},"
			+ " luogoSvolgimento=:#{#off.luogoSvolgimento}, latitude=:#{#off.latitude}, longitude=:#{#off.longitude}, prerequisiti=:#{#off.prerequisiti},"
			+ " postiDisponibili=:#{#off.postiDisponibili}, postiRimanenti=:#{#off.postiRimanenti},"
			+ " enteId=:#{#off.enteId}, nomeEnte=:#{#off.nomeEnte}"
			+ " WHERE off0.id=:#{#off.id}")
	public void update(@Param("off") Offerta off);
	
	List<Offerta> findOffertaByEnteId(String enteId);

	@Query("SELECT COUNT(off) FROM Offerta off WHERE off.uuid IN (:ids)")
	int getCountOfOfferta(String[] ids);
	
	@Modifying
	@Query("update Offerta o set o.postiRimanenti=(:posti) where o.id=(:id)")
	public void updatePostiRimanenti(Long id, int posti);

		
}
