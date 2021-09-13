package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.model.export.ExportCsv;
import it.smartcommunitylab.cartella.asl.model.report.ReportDashboardEsperienza;
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioStudente;
import it.smartcommunitylab.cartella.asl.model.report.ReportStudenteDettaglioEnte;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class ExportDataManager {
	@Autowired
	private StudenteManager studenteManager;
	@Autowired
	private AttivitaAlternanzaManager attivitaAlternanzaManager;
	@Autowired
	private AziendaManager aziendaManager;
	@Autowired
	private TipologiaTipologiaAttivitaManager tipologiaAttivitaManager;
	@Autowired
	private DashboardManager dashboardManager;
	
	public ExportCsv getStudenteAttivitaReportCsv(String istitutoId, String studenteId) throws Exception {
		LocalDate today = LocalDate.now();
		ReportDettaglioStudente reportDettaglioStudente = studenteManager.getReportDettaglioStudente(istitutoId, studenteId, null);
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("dd-MM-YYYY");
		String filename = "Resoconto_Attività_" + reportDettaglioStudente.getStudente().getName() + "_"
				+ reportDettaglioStudente.getStudente().getSurname() + "_" + reportDettaglioStudente.getStudente().getCf() + "_"
				+ reportDettaglioStudente.getStudente().getCorsoDiStudio().getNome() + "_"
				+ reportDettaglioStudente.getStudente().getAnnoScolastico() + "_"
				+ reportDettaglioStudente.getStudente().getClassroom() + "_"
				+ today.format(ldf) + ".csv";
		StringBuffer sb = new StringBuffer("\"titolo attività\";\"tipo\";\"dataInizio\";\"dataFine\";\"classe\";oreInserite;oreValidate;oreProgrammate;oreSmartWorking;\"nomeEnte\";\"pivaEnte\"\n");
		reportDettaglioStudente.getEsperienze().forEach(esp -> {
			String attivita = "";
			String tipo = "";
			String dataInizio = "";
			String dataFine = "";
			String classeStudente = "";
			String oreInserite = "";
			String oreValidate = "";
			String oreProgrammate = "";
			String oreSmartWorking = "";
			String nomeEnte = "";
			String pivaEnte = "";
			AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(esp.getAttivitaAlternanzaId());
			if(aa != null) {
				attivita = aa.getTitolo();
				tipo = getTipologia(aa);
				dataInizio = aa.getDataInizio().format(ldf);
				dataFine = aa.getDataFine().format(ldf);
				classeStudente = esp.getClasseStudente();
				oreInserite = String.valueOf(esp.getOreDaValidare() + esp.getOreValidate());
				oreValidate = String.valueOf(esp.getOreValidate());
				oreProgrammate = String.valueOf(esp.getOreTotali());
				oreSmartWorking = String.valueOf(esp.getOreSmartWorking());
				if(Utils.isNotEmpty(aa.getEnteId())) {
					Azienda azienda = aziendaManager.getAzienda(aa.getEnteId());
					if(azienda != null) {
						nomeEnte = azienda.getNome();
						pivaEnte = azienda.getPartita_iva();
					}
				}
			}
			sb.append("\"" + attivita.replace("\"", "\\\"") + "\";");
			sb.append("\"" + tipo + "\";");
			sb.append("\"" + dataInizio + "\";");
			sb.append("\"" + dataFine + "\";");
			sb.append("\"" + classeStudente + "\";");
			sb.append(oreInserite + ";");
			sb.append(oreValidate + ";");
			sb.append(oreProgrammate + ";");
			sb.append(oreSmartWorking + ";");
			sb.append("\"" + nomeEnte.replace("\"", "\\\"") + "\";");
			sb.append("\"" + pivaEnte + "\"\n");
		});
		ExportCsv exportCsv = new ExportCsv();
		exportCsv.setFilename(filename);
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb);
		return exportCsv;
	}
	
	public ExportCsv getClasseAttivitaReportCsv(String istitutoId, String annoScolastico, 
			String corsoId, String corso, String classe) throws Exception {
		LocalDate today = LocalDate.now();
		List<ReportDettaglioStudente> reportDettaglioStudenteList = studenteManager.getReportDettaglioStudente(
				istitutoId, annoScolastico, corsoId, classe);
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("dd-MM-YYYY");
		String filename = "Resoconto_Attività_" + corso + "_" + annoScolastico + "_" + classe + "_"
				+ today.format(ldf) + ".csv";
		StringBuffer sb = new StringBuffer("\"cognome\";\"nome\";\"cf\";\"titolo attività\";\"tipo\";\"dataInizio\";\"dataFine\";\"classe\";oreInserite;oreValidate;oreProgrammate;oreSmartWorking;\"nomeEnte\";\"pivaEnte\"\n");
		reportDettaglioStudenteList.forEach(report -> {
			String cognome = report.getStudente().getSurname();
			String nome = report.getStudente().getName();
			String cf = report.getStudente().getCf();
			report.getEsperienze().forEach(esp -> {
				String attivita = "";
				String tipo = "";
				String dataInizio = "";
				String dataFine = "";
				String classeStudente = "";
				String oreInserite = "";
				String oreValidate = "";
				String oreProgrammate = "";
				String oreSmartWorking = "";
				String nomeEnte = "";
				String pivaEnte = "";
				AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(esp.getAttivitaAlternanzaId());
				if(aa != null) {
					attivita = aa.getTitolo();
					tipo = getTipologia(aa);
					dataInizio = aa.getDataInizio().format(ldf);
					dataFine = aa.getDataFine().format(ldf);
					classeStudente = esp.getClasseStudente();
					oreInserite = String.valueOf(esp.getOreDaValidare() + esp.getOreValidate());
					oreValidate = String.valueOf(esp.getOreValidate());
					oreProgrammate = String.valueOf(esp.getOreTotali());
					oreSmartWorking = String.valueOf(esp.getOreSmartWorking());
					if(Utils.isNotEmpty(aa.getEnteId())) {
						Azienda azienda = aziendaManager.getAzienda(aa.getEnteId());
						if(azienda != null) {
							nomeEnte = azienda.getNome();
							pivaEnte = azienda.getPartita_iva();
						}
					}
				}
				sb.append("\"" + cognome + "\";");
				sb.append("\"" + nome + "\";");
				sb.append("\"" + cf + "\";");
				sb.append("\"" + attivita.replace("\"", "\\\"") + "\";");
				sb.append("\"" + tipo + "\";");
				sb.append("\"" + dataInizio + "\";");
				sb.append("\"" + dataFine + "\";");
				sb.append("\"" + classeStudente + "\";");
				sb.append(oreInserite + ";");
				sb.append(oreValidate + ";");
				sb.append(oreProgrammate + ";");
				sb.append(oreSmartWorking + ";");
				sb.append("\"" + nomeEnte.replace("\"", "\\\"") + "\";");
				sb.append("\"" + pivaEnte + "\"\n");
			});			
		});
		ExportCsv exportCsv = new ExportCsv();
		exportCsv.setFilename(filename);
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb);
		return exportCsv;
	}
	
	public ExportCsv getDashboardEsperienze(String istitutoId, String annoScolastico, 
			String text, boolean getErrors) throws Exception {
		List<ReportDashboardEsperienza> list = dashboardManager.getReportEsperienze(istitutoId, annoScolastico, text, getErrors);
		String filename = "esperienze.csv";
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		StringBuffer sb = new StringBuffer("esperienzaId;\"istituto\";\"annoScolastico\";\"studenteId\";\"nominativoStudente\";\"cf\";\"classe\";\"titolo\";\"tipologia\";\"dataInizio\";\"dataFine\";\"stato\";oreTotali;oreValidate;\"allineato\";\"errore\";\"invio\"\n");
		for(ReportDashboardEsperienza esp : list) {
			sb.append(esp.getEsperienzaId() + ";");
			sb.append("\"" + esp.getIstituto() + "\";");
			sb.append("\"" + esp.getAnnoScolastico() + "\";");
			sb.append("\"" + esp.getStudenteId() + "\";");
			sb.append("\"" + esp.getNominativoStudente() + "\";");
			sb.append("\"" + esp.getCfStudente() + "\";");
			sb.append("\"" + esp.getClasseStudente() + "\";");
			sb.append("\"" + cleanString(esp.getTitolo()) + "\";");
			sb.append("\"" + getTipologia(esp.getTipologia()) + "\";");
			sb.append("\"" + esp.getDataInizio().format(ldf) + "\";");
			sb.append("\"" + esp.getDataFine().format(ldf) + "\";");
			sb.append("\"" + esp.getStato() + "\";");
			sb.append(esp.getOreTotali() + ";");
			sb.append(esp.getOreValidate() + ";");
			sb.append("\"" + esp.isAllineato() + "\";");
			if(Utils.isNotEmpty(esp.getErrore())) {
				String s = cleanString(esp.getErrore());
				int i = s.indexOf("{'sistemaOrigine'");
				if(i > 0) {
					s = s.substring(0, i);
				}
				sb.append("\"" + s + "\";");
			} else {
				sb.append("\"\";");
			}
			if(Utils.isNotEmpty(esp.getInvio())) {
				sb.append("\"" + cleanString(esp.getInvio()) + "\"\n");
			} else {
				sb.append("\"\"\n");
			}
		}
		ExportCsv exportCsv = new ExportCsv();
		exportCsv.setFilename(filename);
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb);
		return exportCsv;
	}
	
	public ExportCsv getEnteStudente(String enteId, String studenteId) {
		ReportStudenteDettaglioEnte report = studenteManager.getStudenteDettaglioEnte(studenteId, enteId);
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("dd-MM-YYYY");
		LocalDate today = LocalDate.now();
		String filename = "Resoconto_Attività_" + report.getStudente().getSurname() + "_" + report.getStudente().getName() + "_"
				+ today.format(ldf) + ".csv";
		StringBuffer sb = new StringBuffer("\"cognome\";\"nome\";\"cf\";\"dataNascita\";\"telefono\";\"email\";\"titolo attività\";\"tipo\";\"dataInizio\";\"dataFine\";oreProgrammate;\"tutorScolastico\";\"tutorEnte\"\n");
		report.getAttivitaList().forEach(aa -> {
			String cognome = report.getStudente().getSurname();
			String nome = report.getStudente().getName();
			String cf = report.getStudente().getCf();
			String dataNascita = report.getStudente().getBirthdate();
			String telefono = report.getStudente().getPhone();
			String email = report.getStudente().getEmail();
			String titolo = aa.getTitolo();
			String tipo = getTipologia(aa);
			String dataInizio = aa.getDataInizio().format(ldf);
			String dataFine = aa.getDataFine().format(ldf);
			String oreProgrammate = String.valueOf(aa.getOre());
			String tutorScolastico = aa.getReferenteScuola();
			String tutorEnte = aa.getReferenteEsterno();
			sb.append("\"" + cognome + "\";");
			sb.append("\"" + nome + "\";");
			sb.append("\"" + cf + "\";");
			sb.append("\"" + dataNascita + "\";");
			sb.append("\"" + telefono + "\";");
			sb.append("\"" + email + "\";");
			sb.append("\"" + titolo.replace("\"", "\\\"") + "\";");
			sb.append("\"" + tipo + "\";");
			sb.append("\"" + dataInizio + "\";");
			sb.append("\"" + dataFine + "\";");
			sb.append(oreProgrammate + ";");
			sb.append("\"" + tutorScolastico + "\";");
			sb.append("\"" + tutorEnte + "\"\n");		
		});
		ExportCsv exportCsv = new ExportCsv();
		exportCsv.setFilename(filename);
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb);
		return exportCsv;
	}

	private String getTipologia(AttivitaAlternanza aa) {
		TipologiaTipologiaAttivita tta = tipologiaAttivitaManager.getTipologiaTipologiaAttivitaByTipologia(aa.getTipologia());
		if(tta != null) {
			return tta.getTitolo();
		}
		return "";
	}
	
	private String getTipologia(int tipologia) {
		TipologiaTipologiaAttivita tta = tipologiaAttivitaManager.getTipologiaTipologiaAttivitaByTipologia(tipologia);
		if(tta != null) {
			return tta.getTitolo();
		}
		return "";		
	}
	
	private String cleanString(String text) {
		if(Utils.isNotEmpty(text)) {
			return text.replace("\"", "'");
			//return text.replace("\"", "'").replace('\n', ' ').replace('\r', ' ');
		}
		return "";
	}

}
