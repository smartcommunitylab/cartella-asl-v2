package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Convenzione;
import it.smartcommunitylab.cartella.asl.model.Convenzione.Stato;
import it.smartcommunitylab.cartella.asl.model.Documento;
import it.smartcommunitylab.cartella.asl.repository.ConvenzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.DocumentoRepository;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class ConvenzioneManager extends DataEntityManager {
	@Autowired
	ConvenzioneRepository convenzioneRepository;
	@Autowired
	DocumentoRepository documentoRepository;
	
	public LocalDate getDataConvenzioneAttiva(String istitutoId, String enteId) {
		String q = "SELECT MAX(c.dataFine) FROM Convenzione c WHERE c.enteId=(:enteId) AND c.istitutoId=(:istitutoId)";
		TypedQuery<LocalDate> query = em.createQuery(q, LocalDate.class);
		query.setParameter("enteId", enteId);
		query.setParameter("istitutoId", istitutoId);
		LocalDate date = query.getSingleResult();
		return date;
	}
	
	public void checkAttivitaByEnte(AttivitaAlternanza aa, String enteId) throws Exception {
		if(aa == null) {
			throw new BadRequestException("attività non trovata");
		}
		if(!enteId.equals(aa.getEnteId())) {
			throw new BadRequestException("attività non visibile");
		}
		if((aa.getTipologia() != 7) && (aa.getTipologia() != 10)) {
			throw new BadRequestException("tipologia non corretta");
		}
		LocalDate today = LocalDate.now();
		LocalDate unAnnoFa = today.minusYears(1);
		LocalDate dataConvenzioneAttiva = getDataConvenzioneAttiva(aa.getIstitutoId(), enteId);
		if((dataConvenzioneAttiva == null) || today.isAfter(dataConvenzioneAttiva)) {
			throw new BadRequestException("convenzione scaduta");
		}
		if(unAnnoFa.isAfter(aa.getDataFine())) {
			throw new BadRequestException("attività scaduta");
		}
	}
	
	private void completaConvenzione(Convenzione c) {
		LocalDate today = LocalDate.now();
		if(today.isBefore(c.getDataInizio()) || today.isAfter(c.getDataFine())) {
			c.setStato(Stato.non_attiva);
		} else {
			c.setStato(Stato.attiva);
		}
		List<Documento> list = documentoRepository.findDocumentoByRisorsaId(c.getUuid());
		if(!list.isEmpty()) {
			Documento doc = list.get(0);
			c.setNomeFile(doc.getNomeFile());
			c.setUuidFile(doc.getUuid());
		}
	}
	
	public Convenzione getConvenzione(Long convenzioneId) {
		Convenzione c = convenzioneRepository.findById(convenzioneId).orElse(null);
		if(c != null) {
			completaConvenzione(c);
		}
		return c;
	}
	
	public Convenzione getConvenzioneByUuid(String uuid) {
		Convenzione c = convenzioneRepository.findByUuid(uuid);
		if(c != null) {
			completaConvenzione(c);
		}
		return c;
	}
	
	public Convenzione getUltimaConvenzione(String istitutoId, String enteId) {
		List<Convenzione> convenzioni = getConvenzioni(istitutoId, enteId);
		for(Convenzione c: convenzioni) {
			if(Stato.attiva.equals(c.getStato())) {
				return c;
			}
		}
		if(convenzioni.size() > 0) {
			return convenzioni.get(0);
		}
		return null;
	}
	
	public List<Convenzione> getConvenzioni(String istitutoId, String enteId) {
		List<Convenzione> list = convenzioneRepository.findByIstitutoIdAndEnteId(istitutoId, enteId, Sort.by(Sort.Direction.DESC, "dataFine"));
		for(Convenzione c : list) {
			completaConvenzione(c);
		}
		return list;
	}

	public Convenzione saveConvenzione(Convenzione c) throws BadRequestException {
		if(!c.getDataFine().isAfter(c.getDataInizio())) {
			throw new BadRequestException("intervallo date errato");
		}
		Convenzione cDb =  null; 
		if(c.getId() != null) {
			cDb = convenzioneRepository.findById(c.getId()).orElse(null);
		}
		if(cDb == null) {
			c.setUuid(Utils.getUUID());
			c.setId(null);
			return convenzioneRepository.save(c);
		} else {
			cDb.setNome(c.getNome());
			cDb.setDataInizio(c.getDataInizio());
			cDb.setDataFine(c.getDataFine());
			return convenzioneRepository.save(cDb);
		}	
	}
	
	public Convenzione deleteConvenzione(String istitutoId, Long convenzioneId) throws BadRequestException {
		Convenzione c = convenzioneRepository.findById(convenzioneId).orElse(null);
		if(c == null) {
			throw new BadRequestException("convenzione non trovata");
		}
		if(!istitutoId.equals(c.getIstitutoId())) {
			throw new BadRequestException("convenzione errata");
		}
		List<Documento> docs = documentoRepository.findDocumentoByRisorsaId(c.getUuid());
		for(Documento doc : docs) {
			documentoRepository.delete(doc);
		}
		convenzioneRepository.delete(c);
		return c;
	}
}
