package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.ReferenteAlternanza;

@Repository
public interface ReferenteAlternanzaRepository extends JpaRepository<ReferenteAlternanza, String> {

	@Modifying
	@Query("update ReferenteAlternanza ra0 set nome=:#{#ra.nome} where id = :#{#ra.id}")
	public void update(@Param("ra") ReferenteAlternanza ra);
	
	@Query("SELECT ra FROM ReferenteAlternanza ra WHERE ra.extId =:#{#extId}")
	public ReferenteAlternanza findReferenteAlternanzaByExtId(@Param("extId") String extId);
	
	public ReferenteAlternanza findReferenteAlternanzaByCf(String cf);
}
