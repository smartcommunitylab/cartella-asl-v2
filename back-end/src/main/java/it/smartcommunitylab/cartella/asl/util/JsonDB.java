package it.smartcommunitylab.cartella.asl.util;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Offerta;
import it.smartcommunitylab.cartella.asl.model.PianoAlternanza;
import it.smartcommunitylab.cartella.asl.model.PresenzaGiornaliera;
import it.smartcommunitylab.cartella.asl.model.TipologiaAttivita;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.OffertaRepository;
import it.smartcommunitylab.cartella.asl.repository.PianoAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.PresenzaGiornaliereRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaAttivitaRepository;

@Component
@Transactional
@Repository
public class JsonDB {

	@Autowired
	private TipologiaAttivitaRepository tipologiaAttivitaRepository;

	@Autowired
	private CompetenzaRepository competenzeRepository;

	@Autowired
	private EsperienzaSvoltaRepository esperienzaSvoltaRepository;

	@Autowired
	private PianoAlternanzaRepository pianoAlternanzaRepository;

	@Autowired
	private AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	
	@Autowired
	private OffertaRepository offertaRepository;
	
	@Autowired
	private PresenzaGiornaliereRepository presenzeRepository;

	@Autowired
	@Value("${import.dir}")
	private String importPath;

	@Autowired
	@Value("${export.dir}")
	private String exportPath;

	ObjectMapper mapper = new ObjectMapper();

	public void exportTipologieAttivita() throws Exception {
		List<TipologiaAttivita> tipologieAttivita = tipologiaAttivitaRepository.findAll();
		File f = new File(exportPath, "tipologieAttivita.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, tipologieAttivita);
	}

	public void importTipologieAttivita() throws Exception {
		File f = new File(importPath, "tipologieAttivita.json");
		List<TipologiaAttivita> tipologieAttivita = mapper.readValue(f, new TypeReference<List<TipologiaAttivita>>() {
		});
		tipologieAttivita.forEach(x -> {
			x.setUuid(UUID.randomUUID().toString());
		});
		tipologiaAttivitaRepository.saveAll(tipologieAttivita);
	}

	public void exportCompetenze() throws Exception {
		List<Competenza> competenze = competenzeRepository.findAll();
		File f = new File(exportPath, "competenze.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, competenze);
	}

	public void importCompetenze() throws Exception {
		File f = new File(importPath, "competenze.json");
		List<Competenza> competenze = mapper.readValue(f, new TypeReference<List<Competenza>>() {
		});
		competenzeRepository.saveAll(competenze);
	}

	public void exportEsperienzeSvolte() throws Exception {
		List<EsperienzaSvolta> esperienzeSvolte = esperienzaSvoltaRepository.findAll();
		File f = new File(exportPath, "esperienzeSvolte.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, esperienzeSvolte);
	}

	public void importEsperienzeSvolte() throws Exception {
		File f = new File(importPath, "esperienzeSvolte.json");
		List<EsperienzaSvolta> esperienzeSvolte = mapper.readValue(f, new TypeReference<List<EsperienzaSvolta>>() {
		});
		esperienzeSvolte.forEach(es -> {
			es.setUuid(UUID.randomUUID().toString());
		});
		esperienzaSvoltaRepository.saveAll(esperienzeSvolte);
	}

	public void exportPianiAlternanza() throws Exception {
		List<PianoAlternanza> pianiAlternanza = pianoAlternanzaRepository.findAll();
		File f = new File(exportPath, "pianiAlternanza.json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, pianiAlternanza);
	}

	public void importPianiAlternanza() throws Exception {
		File f = new File(importPath, "pianiAlternanza.json");
		List<PianoAlternanza> pianiAlternanza = mapper.readValue(f, new TypeReference<List<PianoAlternanza>>() {
		});
		for (PianoAlternanza p : pianiAlternanza) {
			p.setUuid(UUID.randomUUID().toString());
		}
		pianoAlternanzaRepository.saveAll(pianiAlternanza);
	}

	public void importAttivitaAlternanza() throws Exception {
		File f = new File(importPath, "attivitaAlternanza.json");
		List<AttivitaAlternanza> attivitaAlternanza = mapper.readValue(f,
				new TypeReference<List<AttivitaAlternanza>>() {
				});
		attivitaAlternanza.forEach(aa -> {
			aa.setUuid(UUID.randomUUID().toString());
		});
		attivitaAlternanzaRepository.saveAll(attivitaAlternanza);
	}

	public void importOfferte() throws Exception {
		File f = new File(importPath, "offerte.json");
		List<Offerta> offerte = mapper.readValue(f,
				new TypeReference<List<Offerta>>() {
				});
		offerte.forEach(off -> {
			off.setUuid(UUID.randomUUID().toString());
		});
		offertaRepository.saveAll(offerte);
		
	}

	public void importPresenzeGiornaliere() throws Exception {
		File f = new File(importPath, "presenzeGiornaliere.json");
		List<PresenzaGiornaliera> pz = mapper.readValue(f,
				new TypeReference<List<PresenzaGiornaliera>>() {
				});
		presenzeRepository.saveAll(pz);
	}
	
	

}
