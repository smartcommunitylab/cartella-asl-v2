package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.AziendaEstera;
import it.smartcommunitylab.cartella.asl.model.Convenzione;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.report.ReportUtilizzoAzienda;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaEsteraRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class AziendaManager extends DataEntityManager {
	@SuppressWarnings("unused")
	private static final transient Log logger = LogFactory.getLog(AziendaManager.class);
	
	@Autowired
	AziendaRepository aziendaRepository;
	@Autowired
	OffertaRepository offertaRepository;
	@Autowired
	AttivitaAlternanzaRepository attivitaAltRepository;
	@Autowired
	AziendaEsteraRepository aziendaEsteraRepository;
	@Autowired
	RegistrazioneEnteManager registrazioneEnteManager;
	@Autowired
	ConvenzioneManager convenzioneManager;

	private static final String AZIENDE = "SELECT DISTINCT az FROM Azienda az ";

	public Page<Azienda> findAziende(String text, String istitutoId, Pageable pageRequest) {

		StringBuilder sb = new StringBuilder(AZIENDE);
		if (Utils.isNotEmpty(text)) {
			sb.append(" WHERE UPPER(az.nome) LIKE (:text) OR UPPER(az.partita_iva) LIKE (:text)");
		}
		
		sb.append(" ORDER BY az.nome ASC");

		String q = sb.toString();

		TypedQuery<Azienda> query = em.createQuery(q, Azienda.class);

		if (Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}

		Query cQuery = queryToCountQuery(q, query);
		long t = (Long) cQuery.getSingleResult();

		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Azienda> result = query.getResultList();

		for(Azienda a : result) {
			addRegistrazioneEnte(a);
			if(Utils.isNotEmpty(istitutoId)) {
				addConvenzione(a, istitutoId);
			}
		}
		Page<Azienda> page = new PageImpl<Azienda>(result, pageRequest, t);
		return page;
	}

	public Azienda checkExistingAzienda(String partita_iva, String originInfotn) {
		return aziendaRepository.findByPartitaIvaAndOrigin(partita_iva, Constants.ORIGIN_INFOTN);
	}

	public Page<Azienda> findAziende(String pIva, String text, double[] coordinate, Integer distance,
			Pageable pageRequest) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Azienda> cq1 = cb.createQuery(Azienda.class);
		Root<Azienda> azienda = cq1.from(Azienda.class);

		CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
		cq2.select(cb.count(cq2.from(Azienda.class)));

		List<Predicate> predicates = Lists.newArrayList();

		if (coordinate != null && distance != null) {
			double t1 = distance / Math.abs(Math.cos(Math.toRadians(coordinate[0])) * Constants.KM_IN_ONE_LAT);
			double t2 = distance / Constants.KM_IN_ONE_LAT;

			double lonA = coordinate[1] - t1;
			double lonB = coordinate[1] + t1;

			double latA = coordinate[0] - t2;
			double latB = coordinate[0] + t2;

			Predicate predicate1 = cb.between(azienda.get("latitude").as(Double.class), latA, latB);
			Predicate predicate2 = cb.between(azienda.get("longitude").as(Double.class), lonA, lonB);
			Predicate geoPredicate = cb.and(predicate1, predicate2);

			predicates.add(geoPredicate);
		}

		if (pIva != null && !pIva.isEmpty()) {
			Predicate ivaPredicate = cb.equal(azienda.get("partita_iva").as(String.class), pIva);
			predicates.add(ivaPredicate);
		}
		if (text != null && !text.isEmpty()) {
			String filter = "%" + text.trim() + "%";
			Predicate predicate1 = cb.like(azienda.get("nome").as(String.class), filter);
			Predicate predicate2 = cb.like(azienda.get("description").as(String.class), filter);

			Predicate textPredicate = cb.or(predicate1, predicate2);
			predicates.add(textPredicate);
		}

		cq1.where(predicates.toArray(new Predicate[predicates.size()]));
		cq2.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Azienda> query1 = em.createQuery(cq1);
		TypedQuery<Long> query2 = em.createQuery(cq2);

		query1.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query1.setMaxResults(pageRequest.getPageSize());

		List<Azienda> result = query1.getResultList();
		long total = query2.getSingleResult();

		Page<Azienda> page = new PageImpl<Azienda>(result, pageRequest, total);
		return page;
	}
	
	public Azienda saveAzienda(Azienda a) throws Exception {
		List<Azienda> list = aziendaRepository.findByPartitaIva(a.getPartita_iva());
		Azienda aziendaDb = getAzienda(a.getId());
		if(aziendaDb == null) {
			if(list.size() > 0) {
				throw new BadRequestException("partita iva already exists");
			}			
			a.setId(Utils.getUUID());
			a.setOrigin(Constants.ORIGIN_CONSOLE);
			allineaAziendaEstera(null, a.getPartita_iva(), a.isEstera());
			return aziendaRepository.save(a); 
		} else {
			if(!a.getPartita_iva().equals(aziendaDb.getPartita_iva())) {
				if(list.size() > 0) {
					throw new BadRequestException("partita iva already exists");
				}							
			}
			allineaAziendaEstera(aziendaDb.getPartita_iva(), a.getPartita_iva(), a.isEstera());
			aziendaRepository.update(a);
			return a;
		}
	}
	
	private void allineaAziendaEstera(String oldPartitaIva, String newPartitaIva, boolean estera) {
		if(Utils.isNotEmpty(oldPartitaIva)) {
			AziendaEstera aziendaEstera = aziendaEsteraRepository.findByPartitaIva(oldPartitaIva);
			if(aziendaEstera != null) {
				aziendaEsteraRepository.deleteById(aziendaEstera.getId());
			}
			if(estera) {
				aziendaEstera = new AziendaEstera();
				aziendaEstera.setPartita_iva(newPartitaIva);
				aziendaEsteraRepository.save(aziendaEstera);
			}
		} else {
			if(estera) {
				AziendaEstera aziendaEstera = new AziendaEstera();
				aziendaEstera.setPartita_iva(newPartitaIva);
				aziendaEsteraRepository.save(aziendaEstera);				
			}
		}
	}
	
	public Azienda getAzienda(String id) {
		if(Utils.isNotEmpty(id)) {
			Optional<Azienda> optional = aziendaRepository.findById(id);
			if(optional.isPresent()) {
				Azienda azienda = optional.get();
				AziendaEstera aziendaEstera = aziendaEsteraRepository.findByPartitaIva(azienda.getPartita_iva());
				if(aziendaEstera != null) {
					azienda.setEstera(true);
				}
				addRegistrazioneEnte(azienda);
				return azienda;
			}
		}
		return null;
	}
	
	private void addRegistrazioneEnte(Azienda a) {
		if(a != null) {
			a.setRegistrazioneEnte(registrazioneEnteManager.getRichiestaRegistrazione(a.getId()));
		}
	}
	
	private void addConvenzione(Azienda a, String istitutoId) {
		Convenzione c = convenzioneManager.getUltimaConvenzione(istitutoId, a.getId());
		if(c != null) {
			a.setConvenzione(c);
		}
	}
	
	public Azienda deleteAzienda(String id) throws BadRequestException {
		Azienda azienda = getAzienda(id);
		if(azienda == null) {
			throw new BadRequestException("entity not found");
		}
		if(!Constants.ORIGIN_CONSOLE.equals(azienda.getOrigin())) {
			throw new BadRequestException("entity not erasable");
		}
		ReportUtilizzoAzienda report = getReportUtilizzo(id);
		if(report.isUsed()) {
			throw new BadRequestException("entity is referred by other data");
		}
		aziendaRepository.deleteById(id);
		return azienda;
	}
	
	public ReportUtilizzoAzienda getReportUtilizzo(String id) {
		ReportUtilizzoAzienda report = new ReportUtilizzoAzienda();
		
		String sqlOfferte = "select count(*) from Offerta off where off.enteId = (:aziendaId)";
		Query queryOfferte = em.createQuery(sqlOfferte);
		queryOfferte.setParameter("aziendaId", id);
		report.setOfferte((long) queryOfferte.getSingleResult());
		
		String sqlAttivita = "select count(*) from AttivitaAlternanza aa where aa.enteId = (:aziendaId)";
		Query queryAttivita = em.createQuery(sqlAttivita);
		queryAttivita.setParameter("aziendaId", id);
		report.setAttivitaAlternanza((long) queryAttivita.getSingleResult());
		
		return report;
	}

	public void alignAziendeConsoleInfoTN() {
		logger.info("alignAziendeConsoleInfoTN --->  Started");
		List<Azienda> deleteConsoleAgencyIds = new ArrayList<Azienda>();
		// check 'partita_iva' with origin 'CONSOLE'.
		List<Azienda> aziendeConsole = aziendaRepository.findByOrigin(Constants.ORIGIN_CONSOLE);

		if (aziendeConsole != null && !(aziendeConsole.isEmpty())) {
			for (Azienda azConsole : aziendeConsole) {
				// check for 'partita_iva' && 'origin'(INFOTNISTRUZIONE).
				Azienda azInfoTN = aziendaRepository.findByPartitaIvaAndOrigin(azConsole.getPartita_iva(),
						Constants.ORIGIN_INFOTN);

				if (azInfoTN != null) {
					logger.info("Duplicate Azienda(PIVA): " + azConsole.getPartita_iva());
					alignOfferta(azConsole, azInfoTN);
					alignAttivita(azConsole, azInfoTN);

					// maintain list of CONSOLE agency ids to delete at last.
					if (deleteConsoleAgencyIds.indexOf(azConsole) < 0) {
						deleteConsoleAgencyIds.add(azConsole);
					}

				}
			}
		}

		// delete console agencies.
		for (Azienda del : deleteConsoleAgencyIds) {
			logger.info("Deleteing CONSOLE origin agency with id" + del.getId());
			aziendaRepository.delete(del);
		}

	}
	
	void alignOfferta(Azienda azConsole, Azienda azInfoTN) {

		// check if exist offerta with 'CONSOLE' origin agency.
		List<Offerta> offerte = offertaRepository.findOffertaByEnteId(azConsole.getId());
		// update with agency(INFOTN).
		for (Offerta off : offerte) {
			logger.info("Merge(update/remove) offerta with Azienda(PIVA): " + azConsole.getPartita_iva());
			off.setEnteId(azInfoTN.getId());
			
			if (azInfoTN.getNome() != null) {
				off.setNomeEnte(azInfoTN.getNome());
			}
			if (azInfoTN.getLatitude() != null) {
				off.setLatitude(azInfoTN.getLatitude());
			}
			if (azInfoTN.getLongitude() != null) {
				off.setLongitude(azInfoTN.getLongitude());
			}
			if (azInfoTN.getAddress() != null) {
				off.setLuogoSvolgimento(azInfoTN.getAddress());					
			}
			
			offertaRepository.save(off);
		}

	}

	void alignAttivita(Azienda azConsole, Azienda azInfoTN) {
		// check if exist attivita with 'CONSOLE' origin agency.
		List<AttivitaAlternanza> aas = attivitaAltRepository.findAttivitaAlternanzaByEnteId(azConsole.getId());
		// update with agency(INFOTN).
		for (AttivitaAlternanza aa : aas) {
			logger.info("Merge(update/remove) attivita' with Azienda(PIVA): " + azConsole.getPartita_iva());
			aa.setEnteId(azInfoTN.getId());
			
			if (azInfoTN.getNome() != null) {
				aa.setNomeEnte(azInfoTN.getNome());
			}			
			if (azInfoTN.getLatitude() != null) {
				aa.setLatitude(azInfoTN.getLatitude());					
			}			
			if (azInfoTN.getLongitude() != null) {
				aa.setLongitude(azInfoTN.getLongitude());
			}			
			if (azInfoTN.getAddress() != null) {
				aa.setLuogoSvolgimento(azInfoTN.getAddress());
			}
			
			attivitaAltRepository.save(aa);
			
		}

	}

	public Azienda updateAziendaByEnte(Azienda azienda) throws Exception {
		Azienda aziendaDb = getAzienda(azienda.getId());
		if(aziendaDb == null) {
			throw new BadRequestException("ente non trovato");
		} else {
			List<Azienda> list = aziendaRepository.findByPartitaIva(azienda.getPartita_iva());
			if(!azienda.getPartita_iva().equals(aziendaDb.getPartita_iva())) {
				if(list.size() > 0) {
					throw new BadRequestException("partita iva already exists");
				}							
			}
			aziendaDb.setPartita_iva(azienda.getPartita_iva());
			aziendaDb.setNome(azienda.getNome());
			aziendaDb.setDescription(azienda.getDescription());
			aziendaDb.setEmail(azienda.getEmail());
			aziendaDb.setPec(azienda.getPec());
			aziendaDb.setPhone(azienda.getPhone());
			aziendaDb.setAtecoCode(azienda.getAtecoCode());
			aziendaDb.setAtecoDesc(azienda.getAtecoDesc());
			aziendaDb.setLegaleRappresentante(azienda.getLegaleRappresentante());
			aziendaDb.setMedicoCompetente(azienda.getMedicoCompetente());
			aziendaDb.setResponsabileSicurezza(azienda.getResponsabileSicurezza());
			aziendaDb.setViaPiazza(azienda.getViaPiazza());
			aziendaDb.setCap(azienda.getCap());
			aziendaDb.setComune(azienda.getComune());
			aziendaDb.setProvincia(azienda.getProvincia());
			aziendaDb.setAddress(azienda.getAddress());
			allineaAziendaEstera(aziendaDb.getPartita_iva(), azienda.getPartita_iva(), azienda.isEstera());
			aziendaRepository.update(aziendaDb);
			return aziendaDb;
		}
	}

}
