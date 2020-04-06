package it.smartcommunitylab.cartella.asl.test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Registration;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.CorsoDiStudioRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.RegistrationRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaTipologiaAttivitaRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasetTest {

	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	private StudenteRepository sRepository;		
	
	@Autowired
	private IstituzioneRepository iRepository;		
	
	@Autowired
	private RegistrationRepository rRepository;	
	
	@Autowired
	private CorsoDiStudioRepository cdsRepository;		
	
	@Autowired
	private TipologiaTipologiaAttivitaRepository tRepository;		
	
	@Autowired
	private CompetenzaRepository cRepository;		

	@Autowired
	private AziendaRepository azRepository;	
	
	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);	
	
////	@Test
//	public void t0Export() throws Exception {
//		jsonDB.exportCompetenze();
//		jsonDB.exportEsperienzeSvolte();
////		jsonDB.exportAnniAlternanza();
//		jsonDB.exportPianiAlternanza();
//	}


//	@Test
	public void exportMachFBK() throws Exception {
		List<Istituzione> istituzioni = iRepository.findAll();
		istituzioni = istituzioni.stream().filter(x -> "6246e755-cd2b-423a-82fc-6c13ef238052".equals(x.getId())).collect(Collectors.toList());
		File f = new File("src/main/resources/dataset", "istituzioni.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, istituzioni);		
		
//		List<CorsoDiStudio> corsi = cRepository.findCorsoDiStudioByIstitutoId("6246e755-cd2b-423a-82fc-6c13ef238052");
//		f = new File("src/main/resources/dataset", "corsi.json");
//		mapper.writerWithDefaultPrettyPrinter().writeValue(f, corsi);				
		
		List<Registration> registration = rRepository.findRegistrationByCourseIdAndSchoolYear("76fe561c-985d-45c8-b79f-08d03156801e", "2017-18");
		registration.forEach(x -> {
			x.setInstituteId("6246e755-cd2b-423a-82fc-6c13ef238052");
			x.setOrigin("INFOTNISTRUZIONE");
		});
		List<String> studentIds = registration.stream().map(x -> x.getStudentId()).collect(Collectors.toList());
		
		List<Studente> studenti = sRepository.findAllById(studentIds);

		Map<String, Integer> reclass = Maps.newTreeMap();
		
		int i = 0;
		for (String id: studentIds) {
			reclass.put(id, 3 + i);
			i = (i + 1) % 3;
		}
		registration.forEach(x -> {
			String id = x.getStudentId();
			x.setClassroom(reclass.get(id) + x.getClassroom().substring(1));
		});
		studenti.forEach(x -> {
			String id = x.getId();
//			x.setClassroom(reclass.get(id) + x.getClassroom().substring(1));
			x.setAnnoCorso(reclass.get(id));
		});		
		
		f = new File("src/main/resources/dataset", "registration.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, registration);
		f = new File("src/main/resources/dataset", "studenti.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, studenti);		
		
//		Azienda azienda = azRepository.findOne(5064L);
//		List<Azienda> aziende = Lists.newArrayList(azienda);
//		f = new File("src/main/resources/dataset", "aziende.json");
//		mapper.writerWithDefaultPrettyPrinter().writeValue(f, aziende);		
	}
	
	@Test
	public void importMach() throws Exception {
//		List<Istituzione> istituzioni = mapper.readValue(new File("src/main/resources/dataset/" + "istituzioni.json"), new TypeReference<List<Istituzione>>() {
//		});		
//		iRepository.save(istituzioni);
//		
//		List<Registration> registration = mapper.readValue(new File("src/main/resources/dataset/" + "registration.json"), new TypeReference<List<Registration>>() {
//		});		
//		rRepository.save(registration);
//		
//		List<CorsoDiStudio> corsi = mapper.readValue(new File("src/main/resources/dataset/" + "corsi.json"), new TypeReference<List<CorsoDiStudio>>() {
//		});		
//		cdsRepository.save(corsi);
//		
//		List<Studente> studenti = mapper.readValue(new File("src/main/resources/dataset/" + "studenti.json"), new TypeReference<List<Studente>>() {
//		});				
//		sRepository.save(studenti);
//		
//		List<Competenza> competenze = mapper.readValue(new File("src/main/resources/dataset/" + "competenze.json"), new TypeReference<List<Competenza>>() {
//		});				
//		cRepository.save(competenze);			
//	
//		List<Azienda> aziende = mapper.readValue(new File("src/main/resources/dataset/" + "aziende.json"), new TypeReference<List<Azienda>>() {
//		});				
//		azRepository.save(aziende);			

		List<TipologiaTipologiaAttivita> tipologie = mapper.readValue(new File("src/main/resources/dataset/" + "tipologiaTipologiaAttivita.json"), new TypeReference<List<TipologiaTipologiaAttivita>>() {
		});				
		tipologie.forEach(x -> {
			x.setId((long)x.getTipologia());
		});
		tRepository.saveAll(tipologie);			
		
	}
	
//	@Test
//	public void temp() {
//
//	}
	

}