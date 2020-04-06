package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Consenso;

@Repository
public interface ConsensoRepository extends JpaRepository<Consenso, String> {

	@Query("SELECT cs FROM Consenso cs WHERE cs.email = :email")
	public Consenso findByEmail(@Param("email") String email);

	@Query("SELECT cs FROM Consenso cs WHERE cs.cf = :cf")
	public Consenso findByCF(@Param("cf") String cf);
}
