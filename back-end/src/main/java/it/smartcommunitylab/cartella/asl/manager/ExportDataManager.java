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
import it.smartcommunitylab.cartella.asl.model.report.ReportDettaglioStudente;
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
	
	public ExportCsv getStudenteAttivitaReportCsv(String istitutoId, String studenteId) throws Exception {
		LocalDate today = LocalDate.now();
		ReportDettaglioStudente reportDettaglioStudente = studenteManager.getReportDettaglioStudente(istitutoId, studenteId);
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		String filename = "Resoconto_Attività_" + reportDettaglioStudente.getStudente().getName() + "_"
				+ reportDettaglioStudente.getStudente().getSurname() + "_" + reportDettaglioStudente.getStudente().getCf() + "_"
				+ reportDettaglioStudente.getStudente().getCorsoDiStudio().getNome() + "_"
				+ reportDettaglioStudente.getStudente().getAnnoScolastico() + "_"
				+ reportDettaglioStudente.getStudente().getClassroom() + "_"
				+ today.format(ldf) + ".csv";
		StringBuffer sb = new StringBuffer("\"attività\";\"tipo\";\"dataInizio\";\"dataFine\";oreSvolte;\"nomeEnte\";\"pivaEnte\"\n");
		reportDettaglioStudente.getEsperienze().forEach(esp -> {
			String attivita = "";
			String tipo = "";
			String dataInizio = "";
			String dataFine = "";
			String oreSvolte = "";
			String nomeEnte = "";
			String pivaEnte = "";
			AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(esp.getAttivitaAlternanzaId());
			if(aa != null) {
				attivita = aa.getTitolo();
				tipo = getTipologia(aa);
				dataInizio = aa.getDataInizio().format(ldf);
				dataFine = aa.getDataFine().format(ldf);
				oreSvolte = String.valueOf(esp.getOreValidate());
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
			sb.append(oreSvolte + ";");
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
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		String filename = "Resoconto_Attività_" + corso + "_" + annoScolastico + "_" + classe + "_"
				+ today.format(ldf) + ".csv";
		StringBuffer sb = new StringBuffer("\"cognome\";\"nome\";\"cf\";\"attività\";\"tipo\";\"dataInizio\";\"dataFine\";oreSvolte;\"nomeEnte\";\"pivaEnte\"\n");
		reportDettaglioStudenteList.forEach(report -> {
			String cognome = report.getStudente().getSurname();
			String nome = report.getStudente().getName();
			String cf = report.getStudente().getCf();
			report.getEsperienze().forEach(esp -> {
				String attivita = "";
				String tipo = "";
				String dataInizio = "";
				String dataFine = "";
				String oreSvolte = "";
				String nomeEnte = "";
				String pivaEnte = "";
				AttivitaAlternanza aa = attivitaAlternanzaManager.getAttivitaAlternanza(esp.getAttivitaAlternanzaId());
				if(aa != null) {
					attivita = aa.getTitolo();
					tipo = getTipologia(aa);
					dataInizio = aa.getDataInizio().format(ldf);
					dataFine = aa.getDataFine().format(ldf);
					oreSvolte = String.valueOf(esp.getOreValidate());
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
				sb.append(oreSvolte + ";");
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
	
	private String getTipologia(AttivitaAlternanza aa) {
		TipologiaTipologiaAttivita tta = tipologiaAttivitaManager.getTipologiaTipologiaAttivitaByTipologia(aa.getTipologia());
		if(tta != null) {
			return tta.getTitolo();
		}
		return "";
	}
}
