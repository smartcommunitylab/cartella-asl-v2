package it.smartcommunitylab.cartella.asl.test;

import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Maps;

import it.smartcommunitylab.cartella.asl.manager.AziendaManager;
import it.smartcommunitylab.cartella.asl.manager.QueriesManager;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// @Transactional
public class QueriesTest {

	@Autowired
	private QueriesManager aslManager;

	@Autowired
	private EsperienzaSvoltaRepository esRepository;

	@Autowired
	private AttivitaAlternanzaRepository aaRepository;	
	
	@Autowired
	private OffertaRepository oRepository;

	@Autowired
	private AziendaRepository azRepository;	
	
	@Autowired
	private CompetenzaRepository cRepository;	
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	private AziendaManager aziendaManager;

	private static Map<Long, String> ids = Maps.newTreeMap();

	@Before
	public void init() {
		esRepository.deleteAll();
		aaRepository.deleteAll();
		oRepository.deleteAll();
		azRepository.deleteAll();
	}

	@Test
	public void t1EsperienzaSvolta() throws Exception {
		EsperienzaSvolta es1 = createEsperienzaSvolta(1L);
		EsperienzaSvolta es2 = createEsperienzaSvolta(2L);

//		EsperienzaSvolta es3 = esRepository.findEsperienzaSvoltaByAziendaId(es.getId());
		Long id = 2L;
		List<Integer> list = Lists.newArrayList();
//		Page<EsperienzaSvolta> ess = aslManager.findEsperienzaSvoltaByIstitutoAndAzienda("is" + id, ids.get(id), 0L, null, list, list, null);
//		System.err.println(ess);

	}
	
	private EsperienzaSvolta createEsperienzaSvolta(Long id) throws Exception {
		EsperienzaSvolta es = new EsperienzaSvolta();
//		es.setNome("ES" + id);
		
//		SchedaValutazione sva = new SchedaValutazione();
//		sva.setCommento("ca" + id);
//		es.setSchedaValutazioneAzienda(sva);
//		SchedaValutazione svs = new SchedaValutazione();
//		es.setSchedaValutazioneStudente(svs);	
		
//		DiarioDiBordo ddb = new DiarioDiBordo();
//		Giornata g1 = new Giornata();
//		g1.setAttivitaSvolta("as1");
//		g1.setData(Date.valueOf(LocalDate.of(2018, 2, 2)));
//		Giornata g2 = new Giornata();
//		g2.setData(Date.valueOf(LocalDate.of(2018, 2, 1)));
//		g2.setAttivitaSvolta("as2");
//		ddb.getGiornate().add(g1);
//		ddb.getGiornate().add(g2);
//		es.setDiarioDiBordo(ddb);
		
		AttivitaAlternanza aa = new AttivitaAlternanza();
		aa.setTitolo("AA" + id);
//		aa.setIstituto("is" + id);
		Studente s = new Studente();
		s.setName("S" + id);
//		es.setStudente(s);
//		es.setAttivitaAlternanza(aa);
		es = aslManager.saveEsperienzaSvolta(es);		
		
		Azienda az = new Azienda();
		az.setNome("AZ" + id);
		az = aziendaManager.saveAzienda(az);		
		Offerta o = new Offerta();
		o.setTitolo("O" + id);
//		o.setDataInizio(1000 * id);
//		o.setDataFine(2000 * id);
//		o.setAzienda(az);
//		o = aslManager.saveOpportunita(o);
		Competenza c = new Competenza();
		c.setTitolo("C" + id);
//		cRepository.save(c);
//		o.getCompetenze().add(c);
//		aa.setOpportunita(o);
		aslManager.saveAttivitaAlternanza(aa);

		ids.put(id, az.getId());
		return es;
	}
	
	

}