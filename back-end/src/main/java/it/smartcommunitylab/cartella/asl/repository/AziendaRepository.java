package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Azienda;

@Repository
public interface AziendaRepository extends JpaRepository<Azienda, String> {

	@Modifying
	@Query("update Azienda az0 set nome=:#{#az.nome}, description=:#{#az.description}, idTipoAzienda=:#{#az.idTipoAzienda}, "
			+ "address=:#{#az.address}, partita_iva=:#{#az.partita_iva}, email=:#{#az.email}, pec=:#{#az.pec}, phone=:#{#az.phone}, "
			+ "geocode=:#{#az.geocode}, businessName=:#{#az.businessName}, latitude=:#{#az.latitude}, longitude=:#{#az.longitude}, "
			+ "atecoCode=:#{#az.atecoCode}, atecoDesc=:#{#az.atecoDesc}, legaleRappresentante=:#{#az.legaleRappresentante}, "
			+ "medicoCompetente=:#{#az.medicoCompetente}, responsabileSicurezza=:#{#az.responsabileSicurezza}, "
			+ "viaPiazza=:#{#az.viaPiazza}, cap=:#{#az.cap}, comune=:#{#az.comune}, provincia=:#{#az.provincia} "
			+ "where id = :#{#az.id}")
	public void update(@Param("az") Azienda az);

	@Query("SELECT a FROM Azienda a WHERE a.extId =:#{#extId}")
	public Azienda findAziendaByExtId(@Param("extId") String extId);
	
	public List<Azienda> findByOrigin(String origin);
	
	@Query("SELECT az0 FROM Azienda az0 WHERE az0.partita_iva = (:partitaIva)")
	public List<Azienda> findByPartitaIva(String partitaIva);
	
	@Query("SELECT az0 FROM Azienda az0 WHERE az0.partita_iva = (:partitaIva) AND az0.origin = (:origin)")
	public Azienda findByPartitaIvaAndOrigin(@Param("partitaIva") String partitaIva, @Param("origin") String origin);

}
