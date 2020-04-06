package it.smartcommunitylab.cartella.asl.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import it.smartcommunitylab.cartella.asl.beans.Point;
import it.smartcommunitylab.cartella.asl.manager.AziendaManager;
import it.smartcommunitylab.cartella.asl.manager.CompetenzaManager;
import it.smartcommunitylab.cartella.asl.manager.PianoAlternanzaManager;
import it.smartcommunitylab.cartella.asl.manager.QueriesManager;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.CorsoDiStudio;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.repository.PianoAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.RegistrationRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaAttivitaRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaTipologiaAttivitaRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImportTest {

	private static final int COMPETENZE_N = 10000;
	private static final int COMPETENZE_W = 100;

	@Autowired
	private QueriesManager aslManager;
	
	@Autowired
	private CompetenzaManager competenzaManager;
	
	@Autowired
	private PianoAlternanzaManager pianoAltManager;

	@Autowired
	private CompetenzaRepository cRepository;

	@Autowired
	private EsperienzaSvoltaRepository esRepository;

	@Autowired
	private StudenteRepository sRepository;

	@Autowired
	private AttivitaAlternanzaRepository aaRepository;

	@Autowired
	private OffertaRepository oRepository;

	@Autowired
	private OffertaRepository offertaRepository;
	
	@Autowired
	private AziendaRepository azRepository;

	@Autowired
	private PianoAlternanzaRepository paRepository;

	@Autowired
	private CorsoDiStudioRepository cdsRepository;

	@Autowired
	private TipologiaAttivitaRepository taRepository;
	
	@Autowired
	private TipologiaTipologiaAttivitaRepository ttaRepository;	
	
	@Autowired
	private RegistrationRepository rRepository;
	
	@Autowired
	private AziendaManager aziendaManager;

	@Autowired
	ApplicationContext applicationContext;

	// @PersistenceContext
	// EntityManager em;

	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);

	@Test
	public void t0Delete() {

		aslManager.reset();
	}

	// TODO: generate esperienza svolta + attivita' alternanza with values:
	// prev/current/next month & 3 possible states (3*3=9)

	@Test
	public void t1ImportAzienda() throws Exception {
		long maxA = 0;
		long maxC = 0;

		String path = "azienda/front-end/data/competenze.json";
		List<Competenza> competenze = mapper.readValue(new File(path), new TypeReference<List<Competenza>>() {
		});
		for (Competenza x : competenze) {
			competenzaManager.saveCompetenza(x);
			StringBuilder sb = new StringBuilder();
			Joiner.on("%").appendTo(sb, x.getAbilita());
			maxA = Math.max(maxA, sb.toString().length());
			sb = new StringBuilder();
			Joiner.on("%").appendTo(sb, x.getConoscenze());
			maxC = Math.max(maxC, sb.toString().length());
		}
		;
		System.err.println("MAX: " + maxA + " / " + maxC);

		path = "azienda/front-end/data/aziende.json";
		List<Azienda> aziende = mapper.readValue(new File(path), new TypeReference<List<Azienda>>() {
		});
		for (Azienda a : aziende) {
			aziendaManager.saveAzienda(a);
		}

		path = "azienda/front-end/data/attivitaAlternanza.json";
		List<AttivitaAlternanza> attivitaAlternanza = mapper.readValue(new File(path),
				new TypeReference<List<AttivitaAlternanza>>() {
				});
		attivitaAlternanza.forEach(x -> aslManager.saveAttivitaAlternanza(x));

		path = "azienda/front-end/data/esperienzeSvolte.json";
		List<EsperienzaSvolta> esperienzeSvolte = mapper.readValue(new File(path),
				new TypeReference<List<EsperienzaSvolta>>() {
				});
		esperienzeSvolte.forEach(x -> aslManager.saveEsperienzaSvolta(x));

		path = "azienda/front-end/data/opportunita.json";
		List<Offerta> opportunita = mapper.readValue(new File(path), new TypeReference<List<Offerta>>() {
		});
		opportunita.forEach(x -> {
			Point p = new Point(x.getTipologia() * 0.1 + 46.075693, x.getTipologia() * 0.1 + 11.120527);
//			x.setCoordinate(p);
//			aslManager.saveOfferta(x);
		});

//		path = "azienda/front-end/data/corsiInterni.json";
//		List<CorsoInterno> corsiInterni = mapper.readValue(new File(path), new TypeReference<List<CorsoInterno>>() {
//		});
//		corsiInterni.forEach(x -> {
//			Point p = new Point(x.getTipologia() * -0.1 + 46.075693, x.getTipologia() * -0.1 + 11.120527);
//			x.setCoordinate(p);
//			aslManager.saveCorsoInterno(x);
//		});

		path = "azienda/front-end/data/studenti.json";
		List<Studente> studenti = mapper.readValue(new File(path), new TypeReference<List<Studente>>() {
		});
		studenti.forEach(x -> aslManager.saveStudente(x));

		path = "azienda/front-end/data/registration.json";
		List<Registration> registration = mapper.readValue(new File(path), new TypeReference<List<Registration>>() {
		});
		registration.forEach(x -> rRepository.save(x));		
		
		// call populate.
		populateData();
	}

	@Test
	public void t1ImportScuola() throws Exception {
		String path = "scuola/front-end/data/pianiAlternanza.json";
		List<PianoAlternanza> pianiAlternanza = mapper.readValue(new File(path),
				new TypeReference<List<PianoAlternanza>>() {
				});
//		pianiAlternanza.forEach(x -> {
//			for (int i = 0; i < 3; i++) {
//
//				AnnoAlternanza aa = new AnnoAlternanza();
//				// aa.setNome((2016 + i) + "/" + (2017 + i) + " anno");
//				// aa.setNome((2016 + i % 2) + "/" + (2017 + i % 2) + " anno");
//				aa.setNome("2017/2018");
//				aa.setOreTotali((i + 1) * 10);
//				aa = anaRepository.save(aa);
//				x.getAnniAlternanza().add(aa);
//			}
//			aslManager.savePianoAlternanza(x);
//		});
		pianiAlternanza.forEach(x -> {
			pianoAltManager.createPianoAlternanza(x);
		});

		path = "scuola/front-end/data/corsiDiStudio.json";
		List<CorsoDiStudio> corsi = mapper.readValue(new File(path), new TypeReference<List<CorsoDiStudio>>() {
		});
		corsi.forEach(x -> cdsRepository.save(x));

		path = "scuola/front-end/data/tipologiaTipologiaAttivita.json";
		List<TipologiaTipologiaAttivita> tipologiaTipologiaAttivita = mapper.readValue(new File(path),
				new TypeReference<List<TipologiaTipologiaAttivita>>() {
				});
		tipologiaTipologiaAttivita.forEach(x -> ttaRepository.save(x));
		
//		path = "scuola/front-end/data/tipologiaAttivita.json";
//		List<TipologiaAttivita> tipologiaAttivita = mapper.readValue(new File(path),
//				new TypeReference<List<TipologiaAttivita>>() {
//				});
//		tipologiaAttivita.forEach(x -> taRepository.save(x));		
	}

//	 @Test
//	public void t2AssociateAzienda() throws Exception {
//		long base = azRepository.findAll().stream().mapToLong(x -> x.getId()).min().orElse(1L) - 1;
//		long oppcorsiN = oRepository.findAll().size();
//		System.err.println("BASE = " + base);
//		List<Studente> studenti = sRepository.findAll();
//
//		Azienda az = azRepository.findOne(base + 1);
//
//		EsperienzaSvolta es = esRepository.findOne(base * 5 + 1);
////		Studente s = sRepository.findOne(base * 3 + 1);
//		Studente s = studenti.remove(0);
//		es.setStudente(s);
//		AttivitaAlternanza aa = aaRepository.findOne(base * 5 + 1);
//		Opportunita o = oRepository.findOne(base * 3 + 1);
//		CorsoInterno ci = ciRepository.findOne(oppcorsiN + base * 3 + 1);
//		o.setAzienda(az);
////		o.setReferenteAzienda(az.getReferentiAzienda().get(0));
//		Competenza c = cRepository.findOne(base * 5 + 1);
//		o.getCompetenze().add(c);
//		ci.getCompetenze().add(c);
//		es.getCompetenze().add(c);
////		aa.setOpportunita(o);
////		aa.setCorsoInterno(ci);
//		es.setAttivitaAlternanza(aa);
//		esRepository.save(es);
//
//		es = esRepository.findOne(base * 5 + 2);
////		s = sRepository.findOne(base * 3 + 2);
//		s = studenti.remove(0);
//		es.setStudente(s);
//		aa = aaRepository.findOne(base * 5 + 2);
//		o = oRepository.findOne(base * 3 + 1);
//		ci = ciRepository.findOne(oppcorsiN + base * 3 + 1);
//		ci.setCompetenze(o.getCompetenze());
//		es.getCompetenze().add(c);
////		aa.setOpportunita(o);
////		aa.setCorsoInterno(ci);
//		es.setAttivitaAlternanza(aa);
//		esRepository.save(es);
//
//		es = esRepository.findOne(base * 5 + 3);
////		s = sRepository.findOne(base * 3 + 3);
//		s = studenti.get(0);
//		es.setStudente(s);
//		aa = aaRepository.findOne(base * 5 + 3);
//		o = oRepository.findOne(base * 3 + 1);
//		ci = ciRepository.findOne(oppcorsiN + base * 3 + 1);
//		ci.setCompetenze(o.getCompetenze());
//		es.getCompetenze().add(c);
////		aa.setOpportunita(o);
////		aa.setCorsoInterno(ci);
//		es.setAttivitaAlternanza(aa);
//		esRepository.save(es);
//
//		es = esRepository.findOne(base * 5 + 4);
////		s = sRepository.findOne(base * 3 + 3);
//		s = studenti.get(0);
//		es.setStudente(s);
//		aa = aaRepository.findOne(base * 5 + 4);
//		o = oRepository.findOne(base * 3 + 2);
//		ci = ciRepository.findOne(oppcorsiN + base * 3 + 2);
////		o.setReferenteAzienda(az.getReferentiAzienda().get(1));
//		o.setAzienda(az);
//		c = cRepository.findOne(base * 5 + 2);
//		o.getCompetenze().add(c);
//		ci.getCompetenze().add(c);
//		es.getCompetenze().add(c);
////		aa.setOpportunita(o);
////		aa.setCorsoInterno(ci);
//		es.setAttivitaAlternanza(aa);
//		esRepository.save(es);
//
//		es = esRepository.findOne(base * 5 + 5);
////		s = sRepository.findOne(base * 3 + 3);
//		s = studenti.get(0);
//		es.setStudente(s);
//		aa = aaRepository.findOne(base * 5 + 5);
//		o = oRepository.findOne(base * 3 + 3);
//		ci = ciRepository.findOne(oppcorsiN + base * 3 + 3);
////		o.setReferenteAzienda(az.getReferentiAzienda().get(2));
//		o.setAzienda(az);
//		c = cRepository.findOne(base * 5 + 2);
//		o.getCompetenze().add(c);
//		ci.getCompetenze().add(c);
//		es.getCompetenze().add(c);
////		aa.setOpportunita(o);
////		aa.setCorsoInterno(ci);
//		es.setAttivitaAlternanza(aa);
//		esRepository.save(es);
//
//	}

//	@Test
//	public void t2AssociateScuola() throws Exception {
//		long base = azRepository.findAll().stream().mapToLong(x -> x.getId()).min().orElse(1L) - 1;
//
//		List<AttivitaAlternanza> aa = aaRepository.findAll();
//		List<PianoAlternanza> pa = paRepository.findAll();
//		List<AnnoAlternanza> ana = pa.stream().flatMap(x -> x.getAnniAlternanza().stream())
//				.collect(Collectors.toList());
//		List<TipologiaTipologiaAttivita> ttas = ttaRepository.findAll();
//		Iterator<AttivitaAlternanza> it1 = aa.iterator();
//		Iterator<AnnoAlternanza> it2 = ana.iterator();
//		List<OppCorso> oc = ocRepository.findAll();
//
//		List<AnnoAlternanza> ana2 = ana;
//		for (int i = 0; i < ana2.size(); i++) {
//			while (it1.hasNext()) {
//				AttivitaAlternanza aa0 = it1.next();
//				boolean skip = true;
//				// while (it2.hasNext()) {
//
//				// skip = !skip;
//				// if (skip) {
//				// continue;
//				// }
//
//				// for (AttivitaAlternanza aa0 : aa) {
//				// for (AnnoAlternanza ana0 : ana) {
//
//				// AnnoAlternanza ana0 = it2.next();
//				AnnoAlternanza ana0 = ana2.get(i / 2);
//
//				System.err.println(aa0.getAnnoScolastico() + " / " + ana0.getNome());
////				if (!(aa0.getAnnoScolastico()).equals((ana0.getNome()))) {
////					continue;
////				}
//
//				// anaRepository.save(ana0);
//				aa0.setAnnoAlternanza(ana0);
//				aaRepository.save(aa0);
//				break;
//			}
//		}
//
//		for (AnnoAlternanza ana0 : ana) {
//			if (ana0.getTipologieAttivita().isEmpty()) {
//				for (int i = 0; i < (ana.indexOf(ana0) % ttas.size() + 1); i++) {
//					TipologiaTipologiaAttivita oldTa = ttas.get(i);
//					TipologiaAttivita newTa = new TipologiaAttivita();
//					newTa.setTipologia(oldTa.getTipologia());
//					newTa.setTitolo(oldTa.getTitolo());
//					newTa = taRepository.save(newTa);
//					ana0.getTipologieAttivita().add(newTa);
//				}
//			}
//			anaRepository.save(ana0);
//		}
//
//		Competenza c1 = cRepository.findOne(base * 5 + 1);
//		Competenza c2 = cRepository.findOne(base * 5 + 2);
//		pa.forEach(x -> {
//			x.getCompetenze().add(c1);
//			x.getCompetenze().add(c2);
//			paRepository.save(x);
//		});
//		
//		oc.forEach(x -> {
//			if (x instanceof Opportunita) {
//				x.getCompetenze().add(c1);
//			} else {
//				x.getCompetenze().add(c2);
//			}
//			ocRepository.save(x);
//		});
//
//	}

	// @Test
	public void t3fillCompetenze() throws Exception {
		for (int i = 0; i < COMPETENZE_N; i++) {
			Competenza c = new Competenza();
			c.setTitolo("Lorem ipsum");
//			c.setProfilo(
//					"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
			c.setAbilita(new String[] { "Read", "Write" });
			c.setConoscenze(new String[] { "Latin", "Greek" });
			cRepository.save(c);
		}

	}

	public void populateData() throws Exception {

		// Azienda.
		Azienda azienda = azRepository.findAll().get(0);
		AttivitaAlternanza refattAlt = aaRepository.findAll().get(0);
		List<Studente> studenti  = sRepository.findAll();
		Studente refStudent = studenti.remove(0);

		List<Integer> tipoAzienda = new ArrayList<Integer>();
		tipoAzienda.add(6);
		tipoAzienda.add(7);
		tipoAzienda.add(8);
		tipoAzienda.add(9);
		tipoAzienda.add(10);

		Map<String, Offerta> offerStateMap = new HashMap<String, Offerta>();

//		CorsoInterno corsoInterno = ciRepository.findAll().get(0);
		
		// loop through all opportunita and set Azienda.
		for (Offerta offerta : offertaRepository.findAll()) {
			if (tipoAzienda.contains(offerta.getTipologia())) {
//				offerta.setAzienda(azienda);

				// create map s0,s1,s2
				String key = offerta.getTipologia() + "_"
						+ offerta.getTitolo().substring(offerta.getTitolo().lastIndexOf("-") + 1);
				offerStateMap.put(key, offerta);

				oRepository.save(offerta);
			}
		}

		// loop through iterator of attivita_alternanza and set opportunita
		// based on state.
		for (AttivitaAlternanza aa : aaRepository.findAll()) {
			String key = aa.getTipologia() + "_" + aa.getTitolo().substring(aa.getTitolo().lastIndexOf("-") + 1);

			if (offerStateMap.containsKey(key)) {
				Offerta temp = offerStateMap.get(key);
//				aa.setOpportunita(temp);
				aa.setDataInizio(temp.getDataInizio());
				aa.setDataFine(temp.getDataFine());
			} else {
//				aa.setCorsoInterno(corsoInterno);
//				aa.setDataInizio(corsoInterno.getDataInizio());
//				aa.setDataFine(corsoInterno.getDataFine());
			}
			aaRepository.save(aa);
		}

		Random r = new Random();
		int Low = 1;
		int High = 5;

		// create esperienza svolta
		for (EsperienzaSvolta esp : esRepository.findAll()) {
			AttivitaAlternanza temp = aaRepository.getOne(esp.getId());
			if (temp != null) {
				if (temp.getTitolo().indexOf("-s2") != -1) {
//					esp.setStato(2);
				} else if (temp.getTitolo().indexOf("-s1") != -1) {
//					esp.setStato(1);
				} else if (temp.getTitolo().indexOf("-s0") != -1) {
//					esp.setStato(0);
				} else if (temp.getTitolo().indexOf("-sMin1") != -1) {
//					esp.setStato(-1);
				} else {
//					esp.setStato(2);
				}
//				esp.setAttivitaAlternanza(temp);
			} else {
//				esp.setAttivitaAlternanza(refattAlt);
			}

//			Studente tempStudent = sRepository.findOne(new Long(r.nextInt(High - Low) + Low));
			if (!studenti.isEmpty()) {
//				esp.setStudente(studenti.remove(0));
			} else {
//				esp.setStudente(refStudent);
			}

			esRepository.save(esp);

		}

	}

}