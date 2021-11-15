package it.smartcommunitylab.cartella.asl.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import it.smartcommunitylab.cartella.asl.exception.BadRequestException;
import it.smartcommunitylab.cartella.asl.manager.CompetenzaManager;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.ext.OdtFile;
import it.smartcommunitylab.cartella.asl.model.ext.ProgettoFormativoOdt;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.AziendaRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Service
public class ProgettoFormativoFormatter {
	private static Log logger = LogFactory.getLog(ProgettoFormativoFormatter.class);
			
	@Autowired
	private AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	
	@Autowired
	private EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	
	@Autowired
	private StudenteRepository studenteRepository;
	
	@Autowired
	private AziendaRepository aziendaRepository;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private CompetenzaManager competenzaManager;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public OdtFile getOtdFile(String istitutoId, Long esperienzaSvoltaId) throws Exception {
		EsperienzaSvolta es = esperienzaSvoltaRepository.findById(esperienzaSvoltaId).orElse(null);
		if(es == null) {
			throw new BadRequestException("esperienza non trovato");
		}
		if(!es.getIstitutoId().equals(istitutoId)) {
			throw new BadRequestException("istituto non corrispondente");
		}
		Studente studente = studenteRepository.findById(es.getStudenteId()).orElse(null);
		if(studente == null) {
			throw new BadRequestException("studente non trovato");
		}
		AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
		if(aa == null) {
			throw new BadRequestException("attività non trovata");
		}
		if(aa.getTipologia() != 7) {
			throw new BadRequestException("tipologia non conforme");
		}
		Azienda azienda = aziendaRepository.findById(aa.getEnteId()).orElse(null);
		if(azienda == null) {
			throw new BadRequestException("anzienda/ente non trovata");
		}
		List<Competenza> competenze = competenzaManager.getRisorsaCompetenze(aa.getUuid());
		
		ProgettoFormativoOdt pf = new ProgettoFormativoOdt();
		pf.setCognome(studente.getSurname());
		pf.setNome(studente.getName());
		pf.setDataNascita(studente.getBirthdate());
		pf.setEmail(getNotEmpty(studente.getEmail()));
		pf.setClasse(es.getClasseStudente());
		pf.setEnte(azienda.getNome());
		pf.setPartitaIva(azienda.getPartita_iva());
		pf.setIndirizzo(getIndirizzo(aa, azienda));
		pf.setTipologia(getTipologiaAzienda(azienda));
		pf.setContattoTel(getNotEmpty(azienda.getPhone()));
		pf.setDateFrom(aa.getDataInizio().format(formatter));
		pf.setDateTo(aa.getDataFine().format(formatter));
		pf.setOre(String.valueOf(aa.getOre()));
		pf.setIntNominativo(getNotEmpty(aa.getReferenteScuola()));
		pf.setIntEmail(getNotEmpty(aa.getReferenteScuolaEmail()));
		pf.setEstNominativo(getNotEmpty(aa.getReferenteEsterno()));
		pf.setEstEmail(getNotEmpty(aa.getReferenteEsternoEmail()));
		pf.setDescrizione(getNotEmpty(aa.getDescrizione()));		
		
		// Load ODT file and set Velocity template engine and cache it to the registry
		Resource resource = resourceLoader.getResource("classpath:templates/progetto_formativo_it.odt");
		InputStream isTemplate = resource.getInputStream();
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(isTemplate, TemplateEngineKind.Velocity);
		
		// Create Java model context
		IContext context = report.createContext();
		context.put("pf", pf);
		context.put("competenze", competenze);
		
		// Generate report by merging Java model with the ODT
		Path tempFile = Files.createTempFile("edit-pf-", ".odt");
		tempFile.toFile().deleteOnExit();
		File outputFileODT = tempFile.toFile();
		OutputStream osODT = new FileOutputStream(outputFileODT);
		report.process(context, osODT);
		osODT.flush();
		osODT.close();
		
		if(logger.isInfoEnabled()) {
			logger.info("getOtdFile:" + outputFileODT.getAbsolutePath());
		}
		String filename = "Progetto formativo " + es.getNominativoStudente() + " " + es.getClasseStudente() + " " + aa.getTitolo() + ".odt";
    OdtFile odtFile = new OdtFile();
    odtFile.setFile(outputFileODT);
    odtFile.setFilename(filename);
		return odtFile;
	}
	
	private String getNotEmpty(String value) {
		if(value == null) {
			return "";
		}
		return value;
	}
	
	private String getIndirizzo(AttivitaAlternanza aa, Azienda azienda) {
		if(Utils.isNotEmpty(aa.getLuogoSvolgimento())) {
			return aa.getLuogoSvolgimento();
		}
		if(Utils.isNotEmpty(azienda.getAddress())) {
			return azienda.getAddress();
		}
		return "";
	}
	
	private String getTipologiaAzienda(Azienda azienda) {
		switch(azienda.getIdTipoAzienda()) {
			case 1: return "Associazione";
			case 5: return "Cooperativa";
			case 10: return "Impresa";
			case 15: return "Libero professionista";
			case 20: return "Pubblica amministrazione";
			case 25: return "Ente privato/Fondazione";
		}
		return "";
	}


}
