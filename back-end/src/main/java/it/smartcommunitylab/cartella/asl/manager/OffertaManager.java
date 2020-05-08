package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.Offerta.Stati;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.storage.LocalDocumentManager;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class OffertaManager extends DataEntityManager {
	@Autowired
	OffertaRepository offertaRepository;
	@Autowired
	CompetenzaManager competenzaManager;
	@Autowired
	LocalDocumentManager documentManager;
	@Autowired
	ErrorLabelManager errorLabelManager;

	public void rimuoviPostiEsperienze(Long offertaId, int posti) {
		if(posti > 0) {
			Optional<Offerta> optional = offertaRepository.findById(offertaId);
			if(optional.isPresent()) {
				Offerta offerta = optional.get();
				offertaRepository.updatePostiRimanenti(offertaId, offerta.getPostiRimanenti() + posti);
			}			
		}
	}

	public void aggiungiPostiEsperienze(Long offertaId, int posti) throws BadRequestException {
		if(posti > 0) {
			Optional<Offerta> optional = offertaRepository.findById(offertaId);
			if(optional.isPresent()) {
				Offerta offerta = optional.get();
				if((offerta.getPostiRimanenti() - posti) >= 0) {
					offertaRepository.updatePostiRimanenti(offertaId, offerta.getPostiRimanenti() - posti);
				} else {
					throw new BadRequestException(errorLabelManager.get("offerta.postiRimanenti"));
				}
			}					
		}
	}
	
	public Page<Offerta> findOfferta(String istitutoId, String text, int tipologia,	Boolean ownerIstituto, String stato, 
			Pageable pageRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT off FROM Offerta off WHERE");
		if(ownerIstituto == null) {
			sb.append(" (off.istitutoId=(:istitutoId) OR off.istitutoId IS NULL)");
		} else {
			if(ownerIstituto) {
				sb.append(" off.istitutoId=(:istitutoId)");
			} else {
				sb.append(" off.istitutoId IS NULL");
			}
		}
		if(tipologia > 0) {
			sb.append(" AND off.tipologia=(:tipologia)");
		}
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND UPPER(off.titolo) LIKE (:text)");
		}
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.disponibile) {
				sb.append(" AND off.postiRimanenti > 0 AND off.dataFine >= (:date)");
			} else {
				sb.append(" AND (off.postiRimanenti <= 0 OR off.dataFine < (:date))");
			}
		}
		sb.append(" ORDER BY off.dataInizio DESC");
		String q = sb.toString();
		
		TypedQuery<Offerta> query = em.createQuery(q, Offerta.class);
		if((ownerIstituto == null) || ownerIstituto) {
			query.setParameter("istitutoId", istitutoId);
		}
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		if(tipologia > 0) {
			query.setParameter("tipologia", tipologia);
		}
		if(Utils.isNotEmpty(stato)) {
			LocalDate localDate = LocalDate.now();
			query.setParameter("date", localDate);
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Offerta> rows = query.getResultList();
		rows.forEach(o -> {
			o.setStato(getStato(o));
		});
		
		Query cQuery = queryToCountQuery(q, query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<Offerta> page = new PageImpl<Offerta>(rows, pageRequest, total);
		return page;
	}
	
	private Stati getStato(Offerta offerta) {
		if(offerta.getPostiRimanenti() <= 0) {
			return Stati.scaduta;
		}
		LocalDate localDate = LocalDate.now(); 
		if(localDate.isAfter(offerta.getDataFine())) {
			return Stati.scaduta;
		}
		return Stati.disponibile;
	}
	
	public Offerta getOfferta(Long id) {
		if(id != null) {
			Optional<Offerta> optional = offertaRepository.findById(id);
			if(optional.isPresent()) {
				Offerta offerta = optional.get();
				offerta.setStato(getStato(offerta));
				offerta.setNumeroAttivita(countAttivitaAlternanzaByOfferta(id));
				return offerta;
			}			
		}
		return null;
	}
	
	public Offerta saveOffertaIstituto(Offerta offerta, String istitutoId) throws Exception {
		Offerta offertaDb = getOfferta(offerta.getId());
		if(offertaDb == null) {
			offerta.setIstitutoId(istitutoId);
			offerta.setUuid(Utils.getUUID());
			offerta.setPostiRimanenti(offerta.getPostiDisponibili());
			return offertaRepository.save(offerta);
		} else {
			if(!istitutoId.equals(offertaDb.getIstitutoId())) {
				throw new BadRequestException(errorLabelManager.get("offerta.owner"));
			}
			int postiOccupati = offertaDb.getPostiDisponibili() - offertaDb.getPostiRimanenti();
			if(offerta.getPostiDisponibili() < postiOccupati) {
				throw new BadRequestException(errorLabelManager.get("offerta.postiDisponibili"));
			}
			offerta.setPostiRimanenti(offerta.getPostiDisponibili() - postiOccupati);
			offertaRepository.update(offerta);
			return offerta;
		}
	}
	
	public void deleteOfferta(Offerta offerta) throws Exception {
		int count = countAttivitaAlternanzaByOfferta(offerta.getId());
		if(count > 0) {
			throw new BadRequestException(errorLabelManager.get("offerta.used"));
		}
		documentManager.deleteDocumentsByRisorsaId(offerta.getUuid());
		competenzaManager.deleteAssociatedCompetenzeByRisorsaId(offerta.getUuid());
		offertaRepository.deleteById(offerta.getId());
	}

}
