package it.smartcommunitylab.cartella.asl.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;

public class DocxTest {
	
	@Test
	public void testConvenzione() throws Exception {
		Istituzione istituzione = new Istituzione();
		istituzione.setName("Istituto1");
		istituzione.setCf("11223344");
		istituzione.setAddress("via della rota 23");
		
		Azienda azienda = new Azienda();
		azienda.setNome("Azienda1");
		azienda.setAddress("via fortuna 13");
		azienda.setPartita_iva("55556667788");
		
		InputStream isTemplate = ClassLoader.getSystemResourceAsStream("docs/Convenzione_semplice_scuole.docx");
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(isTemplate, TemplateEngineKind.Velocity);
		
		IContext context = report.createContext();
		context.put("istituzione", istituzione);
		context.put("azienda", azienda);
		
		Path tempFile = Files.createTempFile("asl-convenzione", ".docx");
		System.out.println(tempFile.toString());
		File outputFileDoc = tempFile.toFile();
		OutputStream osDoc = new FileOutputStream(outputFileDoc);
		
		report.process(context, osDoc);
		osDoc.flush();
		osDoc.close();
	}
	
	@Test
	public void testProgettoFormativo() throws Exception {
		Azienda azienda = new Azienda();
		azienda.setNome("Azienda1");
		azienda.setAddress("via fortuna 13");
		azienda.setPartita_iva("55556667788");
		azienda.setPhone("0461-112233");
		azienda.setEmail("info@azienda.it");
		azienda.setPec("azienda@pec.it");
		
		Studente studente = new Studente();
		studente.setName("nome1");
		studente.setSurname("cognome1");
		studente.setCf("111111");
		
		AttivitaAlternanza attivita = new AttivitaAlternanza();
		attivita.setOre(40);

		InputStream isTemplate = ClassLoader.getSystemResourceAsStream("docs/Progetto_formativo.docx");
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(isTemplate, TemplateEngineKind.Velocity);
		
		IContext context = report.createContext(); 
		context.put("azienda", azienda);
		context.put("studente", studente);
		context.put("attivita", attivita);
		context.put("tipologia", "Stage");
		context.put("classe", "4AI");
		context.put("dataInizio", "12/06/2018");
		context.put("dataFine", "24/06/2018");
		context.put("tutorScuola", "tutor scuola");
		context.put("tutorAzienda", "tutor azienda");

		Path tempFile = Files.createTempFile("asl-progetto", ".docx");
		System.out.println(tempFile.toString());
		File outputFileDoc = tempFile.toFile();
		OutputStream osDoc = new FileOutputStream(outputFileDoc);
		
		report.process(context, osDoc);
		osDoc.flush();
		osDoc.close();
	}

}
