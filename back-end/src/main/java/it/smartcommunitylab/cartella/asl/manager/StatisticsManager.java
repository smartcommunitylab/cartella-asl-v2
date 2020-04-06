package it.smartcommunitylab.cartella.asl.manager;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.statistics.Address;
import it.smartcommunitylab.cartella.asl.model.statistics.Instance;
import it.smartcommunitylab.cartella.asl.model.statistics.Internship;
import it.smartcommunitylab.cartella.asl.model.statistics.KPI;
import it.smartcommunitylab.cartella.asl.model.statistics.Location;
import it.smartcommunitylab.cartella.asl.model.statistics.Organization;
import it.smartcommunitylab.cartella.asl.model.statistics.POI;
import it.smartcommunitylab.cartella.asl.model.statistics.Product;
import it.smartcommunitylab.cartella.asl.model.statistics.Provider;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;

@Repository
@Transactional
public class StatisticsManager extends RelationshipManager {

	private static final String ISFOL = "ISFOL";

	private static final String COUNT_OPPORTUNITA = "SELECT COUNT (DISTINCT o0) FROM Opportunita o0 JOIN o0.competenze c0 WHERE o0.postiRimanenti != o0.postiDisponibili ";
	
	private static final String COMPETENZE = "SELECT DISTINCT c0 FROM Opportunita o0 JOIN o0.competenze c0 ";
	
	
	private static final String COUNT_AZIENDE_PARTECIPANTI = "SELECT COUNT (DISTINCT op0.azienda) FROM Opportunita op0";
	
	private static final String OPPORTUNITA = "SELECT DISTINCT o0 FROM Opportunita o0 ";
	private static final String AZIENDE = "SELECT DISTINCT az0 FROM Azienda az0 ";
	
	private static final String AZIENDE_AND_OPPORTUNITA = "SELECT DISTINCT az0, opp0 FROM Azienda az0, Opportunita opp0 WHERE opp0.azienda = az0 ";
	
	private static final String AZIENDE_AND_ATTIVITA = "SELECT DISTINCT az0, att0 FROM Azienda az0, AttivitaAlternanza att0 WHERE att0.opportunita.azienda = az0 ";
	private static final String ISTITUTI_AND_ATTIVITA = "SELECT DISTINCT is0, att0 FROM Istituzione is0, AttivitaAlternanza att0 WHERE att0.istitutoId = is0.id ";

//	private static final String PIANO_STUDENTE= "SELECT DISTINCT pa0 FROM PianoAlternanza pa0, AnnoAlternanza aa0, AttivitaAlternanza at0, EsperienzaSvolta es0, Studente s0 "
//			+ "WHERE s0.id = (:studenteId) AND es0.studente = s0 AND es0.attivitaAlternanza = at0 AND at0.annoAlternanza = aa0 AND aa0.id = at0.annoAlternanza.id";
	
	private static final String PIANO_STUDENTE= "SELECT DISTINCT pa0 FROM PianoAlternanza pa0, AnnoAlternanza aa0, AttivitaAlternanza at0, EsperienzaSvolta es0, Studente s0 "
			+ "WHERE s0.id = (:studenteId) AND es0.studente = s0 AND es0.attivitaAlternanza = at0 AND at0.annoAlternanza = aa0 AND aa0.id = at0.annoAlternanza.id AND aa0 MEMBER OF pa0.anniAlternanza";	
	
	@Autowired
	private AttivitaAlternanzaRepository aaRepository;
	
	@Autowired
	private StudenteRepository sRepository;	
	
	@Autowired
	protected EntityManager em;
	
	@Autowired
	protected OffertaRepository offertaRepository;
	
	@Autowired
	protected StudenteRepository studenteRepository;
	
	@Autowired
	CompetenzaManager competenzaManager;

	public List<Internship> findInternships(Integer tipologia, Long from, Long to, double[] coordinate, Integer radius, String aziendaId, String corsoId) {
//		StringBuilder sb = new StringBuilder(OPPORTUNITA);
//		boolean geoQuery = false;
//		if (coordinate != null && radius != null) {
//			geoQuery = true;
//		}
//
//		if (corsoId != null && !corsoId.isEmpty()) {
//			sb.append(", AttivitaAlternanza a0 WHERE a0.opportunita = o0 AND a0.corsoId = (:corsoId) ");
//		}
//		
//		if (tipologia != null) {
//			sb.append(" AND o0.tipologia = (:tipologia) ");
//		}
//		if (from != null) {
//			sb.append(" AND o0.dataInizio >= (:from) ");
//		}
//		if (to != null) {
//			sb.append(" AND o0.dataFine <= (:to) ");
//		}
//		if (aziendaId != null) {
//			sb.append(" AND o0.azienda.id = (:aziendaId) ");
//		}		
//
//		sb.append(" ORDER BY o0.dataInizio");
//
//		String q = sb.toString();
//		
//		if (corsoId == null || corsoId.isEmpty()) {
//			q = q.replaceFirst(" AND ", " WHERE ");
//		}
//
//		TypedQuery<Opportunita> query = em.createQuery(q, Opportunita.class);
//
//		if (corsoId != null && !corsoId.isEmpty()) {
//			query.setParameter("corsoId", corsoId);
//		}
//		if (tipologia != null) {
//			query.setParameter("tipologia", tipologia);
//		}
//		if (from != null) {
//			query.setParameter("from", from);
//		}
//		if (to != null) {
//			query.setParameter("to", to);
//		}
//		if (aziendaId != null) {
//			query.setParameter("aziendaId", aziendaId);
//		}
//
//		List<Opportunita> result = query.getResultList();
//
//		if (geoQuery) {
//			List<OppCorso> geoResult = findOppCorsoNear(coordinate[0], coordinate[1], radius);
//			List<Opportunita> geoOpp = Lists.newArrayList();
//			geoResult.stream().filter(x -> x instanceof Opportunita).forEach(x -> geoOpp.add((Opportunita) x));
//			result = ListUtils.intersection(result, geoOpp);
//		}
//
////		List<POI> pois = result.stream().map(x -> opportunitaToPOI(x)).collect(Collectors.toList());
//		List<Internship> internships = result.stream().map(x -> opportunitaToInternship(x)).collect(Collectors.toList());
//
//		return internships;
		return null;
	}
	
	public List<Instance> findInternshipsInstances(Integer tipologia, Long from, Long to, double[] coordinate, Integer radius, String aziendaId, String corsoId) {
		List<Internship> internships = findInternships(tipologia, from, to, coordinate, radius, aziendaId, corsoId);
		List<Instance> instances = Lists.newArrayList();
		
		internships.stream().forEach(x -> x.getInstances().stream().forEach(y -> instances.add(y)));
		
		return instances;
	}
	
//	private Internship opportunitaToInternship(Opportunita opp) {
//		Internship internship = new Internship();
//		
//		internship.setId("" + opp.getId());
//		internship.setName(opp.getTitolo());
//		internship.setType("Internship");
//		internship.setDescription(opp.getDescrizione());
//		
//		internship.setCompanyId(opp.getAzienda().getId());
//		
//		internship.setFromDate(opp.getDataInizio());
//		internship.setToDate(opp.getDataFine());
//		
//		internship.setTotalPlaces(opp.getPostiDisponibili());
//		internship.setRemainingPlaces(opp.getPostiRimanenti());
//		
//		List<AttivitaAlternanza> aas = aaRepository.findAttivitaAlternanzaByOpportunita(opp);
//		
//		Multiset<String> instancesSet = HashMultiset.create();
//		
//		for (AttivitaAlternanza aa: aas) {
//			instancesSet.add(aa.getCorsoId() + "@" + aa.getIstitutoId());
//		}
//		
//		List<Instance> instances = Lists.newArrayList();
//		instancesSet.elementSet().forEach(x -> {
//			Instance instance = new Instance();
//			String[] ids = x.split("@");
//			instance.setId((opp.getId() + "_" + ids[1] + "_" + ids[0]));
//			instance.setCourseId(ids[0]);
//			instance.setInstituteId(ids[1]);
//			instance.setNumber(instancesSet.count(x));
//			instance.setCompanyId(opp.getAzienda().getId());
//			instance.setInternshipId(Long.toString(opp.getId()));
//			instances.add(instance);
//		});		
//		internship.setInstances(instances);
//		
//		return internship;
//		
//	}
	
//	private POI opportunitaToPOI(Opportunita opp) {
//		POI poi = new POI();
//
//		poi.setId("" + opp.getId());
//		poi.setName(opp.getTitolo());
//		poi.setDescription(opp.getDescrizione());
//		if (opp.getAzienda() != null) {
//			Address address = new Address();
//			address.setAddressCountry("IT");
//			address.setAddressLocality(opp.getAzienda().getAddress());
//			poi.setAddress(address);
//		}
//		if (opp.getCoordinate() != null) {
//			Location location = new Location();
//			location.setType("Point");
//			location.setCoordinates(opp.getCoordinate().toCoordinates());
//			poi.setLocation(location);
//		}
//		poi.setSource("");
//
//		fillOpportunitaPOIMetadata(opp, poi);
//		
//		return poi;
//	}
	
//	private void fillOpportunitaPOIMetadata(Opportunita opp, POI poi) {
//		poi.getMetadata().put("fromDate", opp.getDataInizio());
//		poi.getMetadata().put("toDate", opp.getDataFine());
//		poi.getMetadata().put("companyId", opp.getAzienda().getId());
//		
//		List<AttivitaAlternanza> aas = aaRepository.findAttivitaAlternanzaByOpportunita(opp);
//		
//		Multiset<String> instancesSet = HashMultiset.create();
//		
//		for (AttivitaAlternanza aa: aas) {
//			instancesSet.add(aa.getCorsoId() + "@" + aa.getIstitutoId());
//		}
//		
//		List<Map<String, Object>> instances = Lists.newArrayList();
//		instancesSet.elementSet().forEach(x -> {
//			Map<String, Object> instance = Maps.newTreeMap();
//			String[] ids = x.split("@");
//			instance.put("courseId", ids[0]);
//			instance.put("instituteId", ids[1]);
//			instance.put("number", instancesSet.count(x));
//			instance.put("companyId", opp.getAzienda().getId());
//			instances.add(instance);
//		});
		
//		List<Map<String, Object>> instances = Lists.newArrayList();
//		instancesSet.elementSet().forEach(x -> {
//			Map<String, Object> instance = Maps.newTreeMap();
//			String[] ids = x.split("@");
//			instance.put("courseId", ids[0]);
//			instance.put("instituteId", ids[1]);
//			instance.put("number", instancesSet.count(x));
//			instance.put("companyId", opp.getAzienda().getId());
//			instances.add(instance);
//		});		
		
//		poi.getMetadata().put("instances", instances);
//		
//		poi.getMetadata().put("totalPlaces", opp.getPostiDisponibili());
//		poi.getMetadata().put("remainingPlaces", opp.getPostiRimanenti());
//		
//	}
	
	public List<POI> findCompanies(double[] coordinate, Integer radius) {
		List<Azienda> result = null;
		

		boolean geoQuery = false;
		if (coordinate != null && radius != null) {
			geoQuery = true;
		}

		if (!geoQuery) {
		StringBuilder sb = new StringBuilder(AZIENDE);
		sb.append(" ORDER BY az0.nome");

		String q = sb.toString();
		
		TypedQuery<Azienda> query = em.createQuery(q, Azienda.class);

		result = query.getResultList();
		} else {
			result = findAziendaNear(coordinate[0], coordinate[1], radius);
		}

		result.removeIf(x -> x.getCoordinate() == null || x.getCoordinate().hasDefaultValue());
		
		Multimap<Azienda, Offerta> azopp = findAziendaOpportunita(coordinate, radius);
		
		result.removeIf(x -> azopp.get(x).isEmpty());
		
		List<POI> pois = result.stream().map(x -> aziendaToPOI(x, azopp.get(x))).collect(Collectors.toList());

		return pois;
	}	
	
	public Multimap<Azienda, Offerta> findAziendaOpportunita(double[] coordinate, Integer radius) {
		List<Object[]> result = null;

		Multimap<Azienda, Offerta> azopp = ArrayListMultimap.create();
		
		boolean geoQuery = false;
		if (coordinate != null && radius != null) {
			geoQuery = true;
		}

		if (!geoQuery) {
			StringBuilder sb = new StringBuilder(AZIENDE_AND_OPPORTUNITA);
			// sb.append(" ORDER BY az0.nome");

			String q = sb.toString();

			Query query = em.createQuery(q);

			result = query.getResultList();
			
			for (Object[] res: result) {
				azopp.put((Azienda)res[0], (Offerta)res[1]);
			}
			
		}


		
		return azopp;
	}	
	
	
	
	private POI aziendaToPOI(Azienda az, Collection<Offerta> opps) {

		POI poi = new POI();

		poi.setId("" + az.getId());
		poi.setName(az.getNome());
		poi.setDescription("");
		Address address = new Address();
		address.setAddressCountry("IT");
		address.setAddressLocality(az.getAddress());
		poi.setAddress(address);
		Location location = new Location();
		location.setType("Point");
		location.setCoordinates(az.getCoordinate().toCoordinates());
		poi.setLocation(location);

		poi.setSource("");

//		if (opps != null && !opps.isEmpty()) {
//			List<POI> oppPOI = Lists.newArrayList();
//			for (Opportunita opp: opps) {
//				oppPOI.add(opportunitaToPOI(opp));
//			}
//			
//			poi.getMetadata().put("internships", oppPOI);
//		}

		return poi;
	}	
	
	public List<Competenza> getCompetenze() {
		return competenzaRepository.findAll();
	}
	
//	public List<KPI> getAziendeKPI(String aziendaId, String annoScolastico) {
//		StringBuilder sb = new StringBuilder(AZIENDE_AND_ATTIVITA);
//		
//		if (aziendaId != null && !aziendaId.isEmpty()) {
//			sb.append(" AND az0.id = (:aziendaId) ");
//		}
//		if (annoScolastico != null && !annoScolastico.isEmpty()) {
//			sb.append(" AND att0.annoScolastico = (:annoScolastico) ");
//		}		
//		
//		String q = sb.toString();
//		
//		Query query = em.createQuery(q);
//
//		if (aziendaId != null && !aziendaId.isEmpty()) {
//			query.setParameter("aziendaId", aziendaId);
//		}
//		if (annoScolastico != null && !annoScolastico.isEmpty()) {
//			query.setParameter("annoScolastico", annoScolastico);
//		}		
//
//
//		List<Object[]> result = query.getResultList();		
//		
//		
//		Multimap<Azienda, Opportunita> azatt = HashMultimap.create();
//		result.stream().forEach(x -> {
//			azatt.put((Azienda)x[0], ((AttivitaAlternanza)x[1]).getOpportunita());
//		});
//		
//		List<KPI> kpis = Lists.newArrayList();
//		
//		azatt.keySet().stream().forEach(x -> kpis.addAll(aziendaToKPI(x, azatt.get(x), annoScolastico)));		
//		
//		return kpis;
//	}	
	
	public List<KPI> getAziendeKPI(String aziendaId, String annoScolastico) {
		List<Offerta> opps = null;
		
		if (aziendaId != null) {
			opps = offertaRepository.findOffertaByEnteId(aziendaId);
		} else {
			opps = offertaRepository.findAll();
		}

		
		Multimap<Azienda, Offerta> azatt = HashMultimap.create();
		opps.stream().forEach(x -> {
			Azienda az = aziendaRepository.getOne(x.getEnteId());
			if (az.getCoordinate() != null && !az.getCoordinate().hasDefaultValue()) {

				azatt.put(az, x);
			}
		});
		
		List<KPI> kpis = Lists.newArrayList();
		
		azatt.keySet().stream().forEach(x -> kpis.addAll(aziendaToKPI(x, azatt.get(x), annoScolastico)));		
		
		return kpis;
	}	
	
	public KPI getPartecipatingCompaniesKPI() {
		StringBuilder sb = new StringBuilder(COUNT_AZIENDE_PARTECIPANTI);
		
		String q = sb.toString();
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		
		Long count = query.getSingleResult();		
		
		long total = aziendaRepository.count();
		
		System.err.println("AP: " + count + " / " + total);
		
		return partecipatingToKPI((double)count / total);
	}		
	
	private KPI partecipatingToKPI(Double count) {
		KPI kpi = new KPI();

		kpi.setId("P/CO");
		kpi.setName("Partecipating companies");
		kpi.setDescription("Percentage of partecipating companies on total companies");

		kpi.setKpiValue(count);

		Provider prov = new Provider();
		prov.setName("FBK");
		kpi.setProvider(prov);

		kpi.setCalculationFrequency("hourly");

		kpi.getCategory().add("quantitative");
		kpi.setDateModified(new Date());

		return kpi;
	}	
	
	
	private List<KPI> aziendaToKPI(Azienda az, Collection<Offerta> os, String schoolYear) {
		KPI kpiP = new KPI();
		KPI kpiN = new KPI();

		List<KPI> kpis = Lists.newArrayList(kpiP, kpiN);		
		
		int places = os.stream().collect(Collectors.summingInt(x -> x.getPostiDisponibili()));
		
		kpiP.setId(az.getId() + "-P" + ((schoolYear != null) ? ("-" + schoolYear) : ""));
		kpiP.setName("Internships places");
		kpiP.setDescription("Internships places for company " + az.getNome() + ((schoolYear != null)?(", schoolyear " + schoolYear):""));
		kpiP.setKpiValue(places);

		int n = os.size();		
		
		kpiN.setId(az.getId() + "-N" + ((schoolYear != null) ? ("-" + schoolYear) : ""));
		kpiN.setName("Internships number");
		kpiN.setDescription("Internships number for company " + az.getNome() + ((schoolYear != null)?(", schoolyear " + schoolYear):""));
		kpiN.setKpiValue(n);		
		
		Address address = new Address();
		address.setAddressCountry("IT");
		address.setAddressLocality(az.getAddress());
		Organization org = new Organization();
		org.setIdentifier(az.getId());
		org.setName(az.getNome());
		Provider prov = new Provider();
		prov.setName("FBK");	
		
		for (KPI kpi : kpis) {

			kpi.setAddress(address);
			kpi.setCalculationFrequency("hourly");

			kpi.getCategory().add("quantitative");

			kpi.setOrganization(org);

			kpi.setProvider(prov);
			kpi.setDateModified(new Date());
		}
		
		return kpis;
	}
	
	public List<KPI> getIstitutiKPI(String istitutoId, String annoScolastico) {
		StringBuilder sb = new StringBuilder(ISTITUTI_AND_ATTIVITA);
		
		if (istitutoId != null && !istitutoId.isEmpty()) {
			sb.append(" AND att0.istitutoId = (:istitutoId) ");
		}
		if (annoScolastico != null && !annoScolastico.isEmpty()) {
			sb.append(" AND att0.annoScolastico = (:annoScolastico) ");
		}		
		
		String q = sb.toString();
		
		Query query = em.createQuery(q);

		if (istitutoId != null && !istitutoId.isEmpty()) {
			query.setParameter("istitutoId", istitutoId);
		}
		if (annoScolastico != null && !annoScolastico.isEmpty()) {
			query.setParameter("annoScolastico", annoScolastico);
		}		


		List<Object[]> result = query.getResultList();		
		
		
//		Multimap<Istituzione, Opportunita> isatt = HashMultimap.create();
//		result.stream().forEach(x -> {
//			if (((AttivitaAlternanza)x[1]).getOpportunita() != null) {
//				isatt.put((Istituzione)x[0], ((AttivitaAlternanza)x[1]).getOpportunita());
//		}
//		});

		Multimap<Istituzione, AttivitaAlternanza> isatt = HashMultimap.create();
//		result.stream().forEach(x -> {
//			if (((AttivitaAlternanza)x[1]).getOpportunita() != null) {
//				isatt.put((Istituzione)x[0], ((AttivitaAlternanza)x[1]));
//		}
//		});		
		
		List<KPI> kpis = Lists.newArrayList();
		
		isatt.keySet().stream().forEach(x -> kpis.addAll(istitutoToKPI(x, isatt.get(x), annoScolastico)));
			
		return kpis;

	}		
	
	private List<KPI> istitutoToKPI(Istituzione is, Collection<AttivitaAlternanza> aas, String schoolYear) {
		Multimap<CorsoDiStudio, Offerta> csopp = ArrayListMultimap.create();
		
		for (AttivitaAlternanza aa: aas) {
			Offerta off = offertaRepository.getOne(aa.getOffertaId());
			CorsoDiStudio cs = corsoDiStudioRepository.findCorsoDiStudioByIstitutoIdAndAnnoScolasticoAndCourseId(aa.getIstitutoId(), aa.getAnnoScolastico(), ""); //??
			if (cs != null) {
				
				csopp.put(cs, off);
			}
		}
		
		List<KPI> kpis = Lists.newArrayList();
		
		for (CorsoDiStudio cs: csopp.keySet()) {
			kpis.addAll(istitutoToKPI(is, cs, csopp.get(cs), schoolYear));
		}
		
		return kpis;
		
	}
	
	private List<KPI> istitutoToKPI(Istituzione is, CorsoDiStudio cs, Collection<Offerta> os, String schoolYear) {
		KPI kpiP = new KPI();
		KPI kpiN = new KPI();

		List<KPI> kpis = Lists.newArrayList(kpiP, kpiN);

		int places = os.size();

		kpiP.setId(is.getId() + "-" + cs.getCourseId() + "-P" + ((schoolYear != null) ? ("-" + schoolYear) : ""));
		kpiP.setName("Internships places");
		kpiP.setDescription("Internships places for institute " + is.getName() + ", course " + cs.getNome() + ((schoolYear != null) ? (", schoolyear " + schoolYear) : ""));
		kpiP.setKpiValue(places);

		int n = os.stream().map(x -> x.getId()).collect(Collectors.toSet()).size();

		kpiN.setId(is.getId() + "-" + cs.getCourseId() + "-N" + ((schoolYear != null) ? ("-" + schoolYear) : ""));
		kpiN.setName("Internships number");
		kpiN.setDescription("Internships number for institute " + is.getName() + ", course " + cs.getNome() + ((schoolYear != null) ? (", schoolyear " + schoolYear) : ""));
		kpiN.setKpiValue(n);

		Address address = new Address();
		address.setAddressCountry("IT");
		address.setAddressLocality(is.getAddress());
		Organization org = new Organization();
		org.setIdentifier(is.getId());
		org.setName(is.getName());
		Provider prov = new Provider();
		prov.setName("FBK");
		Product prod = new Product();
		prod.setIdentifier(cs.getCourseId());
		prod.setName(cs.getNome());

		for (KPI kpi : kpis) {
			kpi.setAddress(address);
			kpi.setCalculationFrequency("hourly");

			kpi.getCategory().add("quantitative");

			kpi.setOrganization(org);
			kpi.setProduct(prod);

			kpi.setProvider(prov);
			kpi.setDateModified(new Date());
		}

		kpis.remove(kpiN);
		
		return kpis;
	}
	
	
	public KPI countOpportunitaPerSkill(Long competenzaId) {
		StringBuilder sb = new StringBuilder(COUNT_OPPORTUNITA);
		if (competenzaId != null) {
			sb.append(" AND c0.id IS (:id)");
		}
		
		String q = sb.toString();
		TypedQuery<Long> query = em.createQuery(q, Long.class);
		
		if (competenzaId != null) {
			query.setParameter("id", competenzaId);
		}
		
		Long count = query.getSingleResult();
		
		Competenza competenza = null;
		
		if (competenzaId != null) {
			competenza = competenzaManager.getCompetenza(competenzaId);
		}
		
		return countOpportunitaPerSkillToKPI(competenza, count);
	}
	
	private KPI countOpportunitaPerSkillToKPI(Competenza competenza, Long count) {
		KPI kpi = new KPI();
		
		if (competenza != null) {
			kpi.setId(competenza.getId() + "-O/S");
			kpi.setName("Internships number for skill '" + competenza.getTitolo() + "'");
			kpi.setDescription("Number of Internships providing the skill '" + competenza.getTitolo() + "'");
		} else {
			kpi.setId("O/S");
			kpi.setName("Internships number");
			kpi.setDescription("Number of Internships");			
		}
		kpi.setKpiValue(count.intValue());

		Provider prov = new Provider();
		prov.setName("FBK");
		kpi.setProvider(prov);
		if (competenza != null) {
			Product prod = new Product();
			prod.setIdentifier(competenza.getId().toString());
			prod.setName(competenza.getTitolo());	
			kpi.setProduct(prod);
		}
		kpi.setCalculationFrequency("hourly");

		kpi.getCategory().add("quantitative");		
		kpi.setDateModified(new Date());
		
		return kpi;
	}
	
	public List<KPI> match(String istitutoId, String corsoDiStudioId) {
		List<KPI> result = Lists.newArrayList();
		
		List<PianoAlternanza> pas = null;
		List<Competenza> allCompetenze = null;
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PianoAlternanza> cq1 = cb.createQuery(PianoAlternanza.class);
			Root<PianoAlternanza> piani = cq1.from(PianoAlternanza.class);

			List<Predicate> predicates = Lists.newArrayList();
			if (istitutoId != null) {
				Predicate p = cb.equal(piani.get("istitutoId").as(String.class), istitutoId);
				predicates.add(p);
			}
			if (corsoDiStudioId != null) {
				Predicate p = cb.equal(piani.get("corsoDiStudioId").as(String.class), corsoDiStudioId);
				predicates.add(p);
			}

			cq1.where(predicates.toArray(new Predicate[predicates.size()]));

			TypedQuery<PianoAlternanza> query1 = em.createQuery(cq1);

			pas = query1.getResultList();
		}
		
//		{
//			StringBuilder sb = new StringBuilder(COMPETENZE);
//			
//			String q = sb.toString();
//			
//			TypedQuery<Competenza> query = em.createQuery(q, Competenza.class);
//
//			allCompetenze = query.getResultList();	
//		}
	    
/** NAWAZ		for (PianoAlternanza pa: pas) {
			Set<Competenza> paCompetenze = Sets.newHashSet(pa.getCompetenze());
			Set<Competenza> oCompetenze = Sets.newHashSet();
			for (AnnoAlternanza ana: pa.getAnniAlternanza()) {
				List<AttivitaAlternanza> aas = attivitaAlternanzaRepository.findAttivitaAlternanzaByAnnoAlternanza(ana);
				for (AttivitaAlternanza aa: aas) {
					if (aa.getOpportunita() != null) {
						oCompetenze.addAll(aa.getOpportunita().getCompetenze());
					}
				}
			}
			
			double intersectSize = Sets.intersection(paCompetenze, oCompetenze).size();
			double ratio =  intersectSize / paCompetenze.size();
			
			result.add(matchAttivitaAlternanzaCompetenzeToKPI(pa, ratio));
			
		}
		**/
		
		
		return result;
	}
	
	private KPI matchAttivitaAlternanzaCompetenzeToKPI(PianoAlternanza pa, Double count) {
		KPI kpi = new KPI();

		kpi.setId("OAA/PA-" + pa.getId() + "-" + pa.getIstitutoId() + "-" + pa.getCorsoDiStudioId());
		kpi.setName("Internships/school skills ratio");
		kpi.setDescription("Ratio between learned skills and skills planned by school");

		kpi.setKpiValue(count);

		Provider prov = new Provider();
		prov.setName("FBK");
		kpi.setProvider(prov);

		Product prod = new Product();
		prod.setIdentifier(pa.getId() + "-" + pa.getIstitutoId() + "-" + pa.getCorsoDiStudioId());
//		NAWAZ prod.setName(pa.getTitolo() + ", " + pa.getIstituto() + ", " + pa.getCorsoDiStudio());
		kpi.setProduct(prod);

		kpi.setCalculationFrequency("hourly");

		kpi.getCategory().add("quantitative");
		kpi.setDateModified(new Date());

		return kpi;
	}
	
//	private KPI matchAllCompetenzeToKPI(PianoAlternanza pa, Double count) {
//		KPI kpi = new KPI();
//
//		kpi.setId("O/PA-" + pa.getId() + "-" + pa.getIstitutoId() + "-" + pa.getCorsoDiStudioId());
//		kpi.setName("Internships/school skills ratio");
//		kpi.setDescription("Ratio between offered skills and skills planned by school");
//
//		kpi.setKpiValue(count);
//
//		Provider prov = new Provider();
//		prov.setName("FBK");
//		kpi.setProvider(prov);
//
//		Product prod = new Product();
//		prod.setIdentifier(pa.getId() + "-" + pa.getIstitutoId() + "-" + pa.getCorsoDiStudioId());
//		prod.setName(pa.getTitolo() + ", " + pa.getIstituto() + ", " + pa.getCorsoDiStudio());
//		kpi.setProduct(prod);
//
//		kpi.setCalculationFrequency("hourly");
//
//		kpi.getCategory().add("quantitative");
//		kpi.setDateModified(new Date());
//
//		return kpi;
//	}	
	
	
//	public List<Map<String, Object>> getStudentsProfiles(String instituteId) {
//		List<Studente> students = sRepository.findStudenteByIstitutoId(instituteId);
//		
//		List<Map<String, Object>> result = Lists.newArrayList();
//		students.forEach(x -> {
//			Map<String, Object> profile = getStudentProfile(x);
//			if (profile.get("competenzeProgrammate") != null && !((Map)profile.get("competenzeSvolte")).keySet().isEmpty()) {
//				result.add(profile);}
//			}
//		);
//		
//		return result;
//	}
	
	public Map<String, Object> getStudentProfile(String id) throws Exception {
//		Studente studente = studenteRepository.findStudenteByCf(cf);
		Studente studente = studenteRepository.findById(id).orElse(null);
		if(studente == null) {
			throw new UnauthorizedException("entity not found: Student with id " + id);
		}
		
		TypedQuery<PianoAlternanza> query1 = em.createQuery(PIANO_STUDENTE, PianoAlternanza.class);
		
		query1.setParameter("studenteId", studente.getId());
		
		List<PianoAlternanza> pas = query1.getResultList();
		
		Set<String> paCIds = Sets.newHashSet();
/** NAWAZ		for (PianoAlternanza pa: pas) {
			paCIds.addAll(pa.getCompetenze().stream().filter(x -> ISFOL.equals(x.getOwnerId())).map(x -> x.getIdCompetenza()).collect(Collectors.toSet()));
		} */
		
		TypedQuery<EsperienzaSvolta> query2 = em.createQuery(QueriesManager.ESPERIENZA_SVOLTA_BY_STUDENTE_ID, EsperienzaSvolta.class);
		
		query2.setParameter("studenteId", studente.getId());
		
		List<EsperienzaSvolta> ess = query2.getResultList();
		
		Multimap<String, String> azCIds = HashMultimap.create();
		Multimap<String, String> isCIds = HashMultimap.create();
//		for (EsperienzaSvolta es: ess) {
//			if (es.getAttivitaAlternanza().getOpportunita() != null) {
//				azCIds.putAll(es.getAttivitaAlternanza().getOpportunita().getAzienda().getId(), es.getAttivitaAlternanza().getOpportunita().getCompetenze().stream().filter(x -> ISFOL.equals(x.getOwnerId())).map(y -> y.getIdCompetenza()).collect(Collectors.toSet()));
//			}
//			if (es.getAttivitaAlternanza().getCorsoInterno() != null) {
//				isCIds.putAll(es.getAttivitaAlternanza().getCorsoInterno().getIstitutoId(), es.getAttivitaAlternanza().getCorsoInterno().getCompetenze().stream().filter(x -> ISFOL.equals(x.getOwnerId())).map(y -> y.getIdCompetenza()).collect(Collectors.toSet()));
//			}
//		}
		
		Map<String, Object> result = Maps.newTreeMap();
		Map<String, Object> skills = Maps.newTreeMap();
		
		result.put("id", studente.getId());
		skills.put("programmed", paCIds);
		skills.put("acquired", azCIds.asMap());
		skills.put("learned", isCIds.asMap());
		result.put("skills", skills);
		
//		result.put("annoCorso", studente.getAnnoCorso());
		result.put("instituteId", sRepository.findStudenteIstitutoId(studente.getId()));
		
		return result;
	}	
	
//	public void getStudentExperience(String studenteId) {
//		TypedQuery<EsperienzaSvolta> query = em.createQuery(QueriesManager.ESPERIENZA_SVOLTA_BY_STUDENTE_ID, EsperienzaSvolta.class);
//		
//		query.setParameter("studenteId", studenteId);
//		
//		List<EsperienzaSvolta> result = query.getResultList();
//	}

}
