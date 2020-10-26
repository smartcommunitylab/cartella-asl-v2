package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
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

import it.smartcommunitylab.cartella.asl.beans.OffertaIstitutoStub;
import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.Offerta.Stati;
import it.smartcommunitylab.cartella.asl.model.OffertaIstituto;
import it.smartcommunitylab.cartella.asl.repository.OffertaIstitutoRepository;
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
	OffertaIstitutoRepository offertaIstitutoRepository;
	@Autowired
	CompetenzaManager competenzaManager;
	@Autowired
	LocalDocumentManager documentManager;
	@Autowired
	AziendaManager aziendaManager;
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
	
	public void updatePostiRimanenti(Long offertaId, int posti) {
		offertaRepository.updatePostiRimanenti(offertaId, posti);
	}
	
	public Page<Offerta> findOfferta(String istitutoId, String text, int tipologia,	Boolean ownerIstituto, String stato, 
			Pageable pageRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT off FROM Offerta off, OffertaIstituto oi WHERE off.id=oi.offertaId AND oi.istitutoId=(:istitutoId)");
		/*if(ownerIstituto == null) {
			sb.append(" (off.istitutoId=(:istitutoId) OR off.istitutoId IS NULL)");
		} else {
			if(ownerIstituto) {
				sb.append(" off.istitutoId=(:istitutoId)");
			} else {
				sb.append(" off.istitutoId IS NULL");
			}
		}*/
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
		query.setParameter("istitutoId", istitutoId);
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
		
		Query cQuery = queryToCount(q.replace("DISTINCT off", "COUNT(DISTINCT off)"), query);
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
		if(offerta.getIstitutiAssociati().size() == 0) {
			return Stati.bozza;
		}
		return Stati.disponibile;
	}
	
	public Offerta getOfferta(Long id) {
		if(id != null) {
			Optional<Offerta> optional = offertaRepository.findById(id);
			if(optional.isPresent()) {
				Offerta offerta = optional.get();
				offerta.setNumeroAttivita(countAttivitaAlternanzaByOfferta(id));
				completaAssociazioni(offerta);
				offerta.setStato(getStato(offerta));
				return offerta;
			}			
		}
		return null;
	}
	
	private void completaAssociazioni(Offerta offerta) {
		if(offerta != null) {
			List<OffertaIstituto> list = offertaIstitutoRepository.findByOffertaId(offerta.getId());
			for(OffertaIstituto o : list) {
				offerta.getIstitutiAssociati().add(new OffertaIstitutoStub(o));
			}
		}
	}

	public Offerta saveOffertaIstituto(Offerta offerta, String istitutoId) throws Exception {
		Offerta offertaDb = getOfferta(offerta.getId());
		if(offertaDb == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		} else {
			OffertaIstituto oi = offertaIstitutoRepository.findByOffertaIdAndIstitutoId(offerta.getId(), istitutoId);
			if(oi == null) {
				throw new BadRequestException(errorLabelManager.get("offerta.owner"));
			}
			offertaDb.setReferenteScuola(offerta.getReferenteScuola());
			offertaDb.setReferenteScuolaCF(offerta.getReferenteScuolaCF());
			offertaDb.setReferenteScuolaTelefono(offerta.getReferenteScuolaTelefono());
			offertaRepository.update(offertaDb);
			return offertaDb;
		}
	}
	
	public Offerta deleteOfferta(Long id, String istitutoId) throws Exception {
		Offerta offerta = getOfferta(id);
		if(offerta == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		}
		if(!istitutoId.equals(offerta.getIstitutoId())) {
			throw new BadRequestException(errorLabelManager.get("offerta.owner"));
		}
		if(offerta.getNumeroAttivita() > 0) {
			throw new BadRequestException(errorLabelManager.get("offerta.used"));
		}
		documentManager.deleteDocumentsByRisorsaId(offerta.getUuid());
		competenzaManager.deleteAssociatedCompetenzeByRisorsaId(offerta.getUuid());
		offertaRepository.deleteById(offerta.getId());
		return offerta;
	}

	public Offerta saveOffertaByEnte(Offerta offerta, String enteId) throws Exception {
		Offerta offertaDb = getOfferta(offerta.getId());
		if(offertaDb == null) {
			offerta.setEnteId(enteId);
			Azienda azienda = aziendaManager.getAzienda(enteId);
			if(azienda != null) {
				offerta.setNomeEnte(azienda.getNome());
			}
			offerta.setUuid(Utils.getUUID());
			offerta.setPostiRimanenti(offerta.getPostiDisponibili());
			return offertaRepository.save(offerta);
		} else {
			if(!enteId.equals(offertaDb.getEnteId())) {
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

	public Offerta deleteOffertaByEnte(Long id, String enteId) throws Exception {
		Offerta offerta = getOfferta(id);
		if(offerta == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		}
		if(!enteId.equals(offerta.getEnteId())) {
			throw new BadRequestException(errorLabelManager.get("offerta.owner"));
		}
		if(offerta.getNumeroAttivita() > 0) {
			throw new BadRequestException(errorLabelManager.get("offerta.used"));
		}
		documentManager.deleteDocumentsByRisorsaId(offerta.getUuid());
		competenzaManager.deleteAssociatedCompetenzeByRisorsaId(offerta.getUuid());
		offertaRepository.deleteById(offerta.getId());
		return offerta;
	}

	public void associaIstitutiByEnte(Long id, String enteId, 
			List<OffertaIstitutoStub> istituti) throws Exception{
		Offerta offerta = getOfferta(id);
		if(offerta == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		}
		if(!enteId.equals(offerta.getEnteId())) {
			throw new BadRequestException(errorLabelManager.get("offerta.owner"));
		}
		if(offerta.getNumeroAttivita() > 0) {
			throw new BadRequestException(errorLabelManager.get("offerta.used"));
		}
		cancellaIstitutiAssociati(id);
		for(OffertaIstitutoStub o : istituti) {
			OffertaIstituto entity = new OffertaIstituto();
			entity.setOffertaId(id);
			entity.setIstitutoId(o.getIstitutoId());
			entity.setNomeIstituto(o.getNomeIstituto());
			offertaIstitutoRepository.save(entity);
		}
	}

	private void cancellaIstitutiAssociati(Long offertaId) {
		List<OffertaIstituto> list = offertaIstitutoRepository.findByOffertaId(offertaId);
		for(OffertaIstituto o : list) {
			offertaIstitutoRepository.delete(o);
		}
	}

	public Offerta getOffertaByEnte(Long id, String enteId) throws Exception {
		Offerta offerta = getOfferta(id);
		if(offerta == null) {
			throw new BadRequestException(errorLabelManager.get("offerta.notfound"));
		}
		if(!enteId.equals(offerta.getEnteId())) {
			throw new BadRequestException(errorLabelManager.get("offerta.owner"));
		}
		return offerta;
	}

	public Page<Offerta> findOffertaByEnte(String enteId, String text, String stato, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT off.id, off.dataInizio FROM Offerta off LEFT JOIN OffertaIstituto oi ON off.id=oi.offertaId"
				+ " WHERE off.enteId=(:enteId)");
		if(Utils.isNotEmpty(text)) {
			sb.append(" AND (UPPER(off.titolo) LIKE (:text) OR UPPER(oi.nomeIstituto) LIKE (:text))");
		}
		boolean bozza = false;
		if(Utils.isNotEmpty(stato)) {
			Stati statoEnum = Stati.valueOf(stato);
			if(statoEnum == Stati.disponibile) {
				sb.append(" AND off.postiRimanenti > 0 AND off.dataFine >= (:date)");
			} else if (statoEnum == Stati.bozza) {
				sb.append(" AND off.postiRimanenti > 0 AND off.dataFine >= (:date)");
				bozza = true;
			} else {
				sb.append(" AND (off.postiRimanenti <= 0 OR off.dataFine < (:date))");
			}
		}
		sb.append(" GROUP BY off.id, off.dataInizio, oi.offertaId");
		if(bozza) {
			sb.append(" HAVING COUNT(oi.offertaId)=0");
		}
		sb.append(" ORDER BY off.dataInizio DESC");
		String q = sb.toString();
		
		Query query = em.createQuery(q);
		query.setParameter("enteId", enteId);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		if(Utils.isNotEmpty(stato)) {
			LocalDate localDate = LocalDate.now();
			query.setParameter("date", localDate);
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Object[]> rows = query.getResultList();
		List<Offerta> list = new ArrayList<>();
		for (Object[] obj : rows) {
			Long offertaId = (Long) obj[0];
			Offerta offerta = getOfferta(offertaId);
			list.add(offerta);
		}
		
		String counterQuery = "SELECT COUNT(off) FROM Offerta off WHERE off.id IN (" 
				+ q.replace("SELECT off.id, off.dataInizio", "SELECT off.id").replace("ORDER BY off.dataInizio DESC", "")
				+ ")";
		Query cQuery = queryToCount(counterQuery, query);
		long total = (Long) cQuery.getSingleResult();
		
		Page<Offerta> page = new PageImpl<Offerta>(list, pageRequest, total);
		return page;
	}


}
