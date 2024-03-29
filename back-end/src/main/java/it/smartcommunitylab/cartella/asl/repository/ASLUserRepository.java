package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@Repository
public interface ASLUserRepository extends JpaRepository<ASLUser, Long> {
	
	@Query("SELECT u FROM ASLUser u WHERE UPPER(u.cf)=UPPER(:cf)")
	public ASLUser findByCf(String cf);
	
	@Query("SELECT u FROM ASLUser u WHERE LOWER(u.email)=LOWER(:email)")
	public ASLUser findByEmail(String email);
	
	@Query("SELECT u FROM ASLUser u WHERE LOWER(u.email)=LOWER(:email) OR UPPER(u.cf)=UPPER(:cf)")
	public ASLUser findByCfOrEmail(String cf, String email);
	
	@Query("SELECT u FROM ASLUser u WHERE LOWER(u.email)=LOWER(:email) AND UPPER(u.cf)=UPPER(:cf)")
	public ASLUser findByCfAndEmail(String cf, String email);
	
	public ASLUser findByUsername(String username);
	
	@Query("SELECT DISTINCT(u) FROM ASLUser u, ASLUserRole r WHERE r.userId=u.id AND r.role=(:role)")
	public List<ASLUser> findByRole(ASLRole role);

	@Modifying
	@Query("update ASLUser u0 set name=:#{#u.name},surname=:#{#u.surname},cf=:#{#u.cf},username=:#{#u.username} where id = :#{#u.id}")
	public void update(@Param("u") ASLUser u);	
	
}
