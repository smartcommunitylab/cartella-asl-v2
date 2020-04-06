package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvoltaAllineamento;

@Repository
public interface EsperienzaAllineamentoRepository extends JpaRepository<EsperienzaSvoltaAllineamento, Long> {

	public EsperienzaSvoltaAllineamento findByEspSvoltaId(Long espSvoltaId);
	public List<EsperienzaSvoltaAllineamento> findByDaAllineare(Boolean daAllineare);

}
