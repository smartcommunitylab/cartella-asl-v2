package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.smartcommunitylab.cartella.asl.model.MetaInfo;

public interface MetaInfoRepository extends JpaRepository<MetaInfo, String> {

	@Modifying
	@Query("update MetaInfo meta0 set epocTimestamp=:#{#meta.epocTimestamp}, totalRead=:#{#meta.totalRead}, totalStore=:#{#meta.totalStore}, schoolYears=:#{#meta.schoolYears} where id = :#{#meta.id}")
	public void update(@Param("meta") MetaInfo meta);

}
