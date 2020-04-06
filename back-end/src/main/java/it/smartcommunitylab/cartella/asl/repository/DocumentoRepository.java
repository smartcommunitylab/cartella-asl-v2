package it.smartcommunitylab.cartella.asl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, String> {

	public List<Documento> findDocumentoByRisorsaId(String risorsaId);
	public Documento findDocumentoByUuid(String uuid);
	
	public List<Documento> findByRisorsaIdIn(List<String> ids);
	
}
