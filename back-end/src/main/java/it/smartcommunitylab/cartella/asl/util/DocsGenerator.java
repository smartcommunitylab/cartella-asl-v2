package it.smartcommunitylab.cartella.asl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.repository.TipologiaTipologiaAttivitaRepository;

@Component
public class DocsGenerator {
	private static final transient Logger logger = LoggerFactory.getLogger(DocsGenerator.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	private TipologiaTipologiaAttivitaRepository tipologiaAttivitaRepository;
	
	public byte[] getConvenzione(Istituzione istituzione, Azienda azienda) throws Exception {
		InputStream isTemplate = ClassLoader.getSystemResourceAsStream("docs/Convenzione_semplice_scuole.docx");
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(isTemplate, TemplateEngineKind.Velocity);
		
		IContext context = report.createContext();
		context.put("istituzione", istituzione);
		context.put("azienda", azienda);
		
		Path tempFile = Files.createTempFile("asl-convenzione", ".docx");
		File outputFileDoc = tempFile.toFile();
		OutputStream osDoc = new FileOutputStream(outputFileDoc);
		
		report.process(context, osDoc);
		osDoc.flush();
		osDoc.close();
		
		FileInputStream fisDoc = new FileInputStream(outputFileDoc);
		byte[] doc = IOUtils.toByteArray(fisDoc);
		
		fisDoc.close();
		outputFileDoc.delete();
		
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getConvenzione: %s - %s", istituzione.getId(), azienda.getId()));
		}
		return doc;
	}
	
//	public byte[] getProgettoFormativo(EsperienzaSvolta esperienza, 
//			String classeStudente) throws Exception {
//		TipologiaTipologiaAttivita tipologiaAttivita = tipologiaAttivitaRepository.findByTipologia(
//				esperienza.getAttivitaAlternanza().getTipologia());
//		Azienda azienda = esperienza.getAttivitaAlternanza().getOpportunita().getAzienda();
//		Date dataInizio = new Date(esperienza.getAttivitaAlternanza().getDataInizio());
//		Date dataFine = new Date(esperienza.getAttivitaAlternanza().getDataFine());
//		String tutorAzienda = esperienza.getAttivitaAlternanza().getOpportunita().getReferente();
//		String tutorScuola = esperienza.getAttivitaAlternanza().getReferenteScuola();
//		
//		InputStream isTemplate = ClassLoader.getSystemResourceAsStream("docs/Progetto_formativo.docx");
//		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(isTemplate, TemplateEngineKind.Velocity);
//		
//		IContext context = report.createContext();
//		context.put("azienda", azienda);
//		context.put("studente", esperienza.getStudente());
//		context.put("esperienza", esperienza);
//		context.put("attivita", esperienza.getAttivitaAlternanza());
//		context.put("tipologia", tipologiaAttivita.getTitolo());
//		context.put("classe", classeStudente);
//		context.put("dataInizio", sdf.format(dataInizio));
//		context.put("dataFine", sdf.format(dataFine));
//		context.put("tutorScuola", tutorScuola);
//		context.put("tutorAzienda", tutorAzienda);
//		
//		Path tempFile = Files.createTempFile("asl-progetto", ".docx");
//		File outputFileDoc = tempFile.toFile();
//		OutputStream osDoc = new FileOutputStream(outputFileDoc);
//		
//		report.process(context, osDoc);
//		osDoc.flush();
//		osDoc.close();
//		
//		FileInputStream fisDoc = new FileInputStream(outputFileDoc);
//		byte[] doc = IOUtils.toByteArray(fisDoc);
//		
//		fisDoc.close();
//		outputFileDoc.delete();
//		
//		if(logger.isInfoEnabled()) {
//			logger.info(String.format("getProgettoFormativo: %s - %s", azienda.getId(), esperienza.getId()));
//		}
//		return doc;
//	}

}
