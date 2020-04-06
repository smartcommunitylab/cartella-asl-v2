package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;

@Repository
public interface ASLUserRoleRepository extends JpaRepository<ASLUserRole, Long> {
	
	@Query("SELECT r FROM ASLUserRole r WHERE r.userId=(:userId)")
	public List<ASLUserRole> findByUserId(Long userId);

}
