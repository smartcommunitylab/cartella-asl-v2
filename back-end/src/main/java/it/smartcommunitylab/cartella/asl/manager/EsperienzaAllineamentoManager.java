package it.smartcommunitylab.cartella.asl.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.EsperienzaSvoltaAllineamento;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaAllineamentoRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;
import it.smartcommunitylab.cartella.asl.services.InfoTNAlignExpService;

@Repository
@Transactional
public class EsperienzaAllineamentoManager {
	private static final transient Log logger = LogFactory.getLog(EsperienzaAllineamentoManager.class);
	
	@Autowired
	EsperienzaAllineamentoRepository esperienzaAllineamentoRepository;
	@Autowired
	protected InfoTNAlignExpService infoTNAlignExpService;	
	@Autowired
	EsperienzaSvoltaRepository esperienzaSvoltaRepository;
	
	public void addEsperienzaSvoltaAllineamento(Long esperienzaSvoltaId) {
		EsperienzaSvoltaAllineamento esa = new EsperienzaSvoltaAllineamento();
		esa.setEspSvoltaId(esperienzaSvoltaId);
		esperienzaAllineamentoRepository.save(esa);
	}
	
	public void allineaEsperienzaSvolta(EsperienzaSvoltaAllineamento esperienzaSvoltaAllineamento) {
		if (esperienzaSvoltaAllineamento.isDaAllineare()) {
			esperienzaSvoltaAllineamento.setUltimoAllineamento(System.currentTimeMillis());
			// align with infoTN.
			String response;
			try {
				response = infoTNAlignExpService.alignEsperienza(esperienzaSvoltaAllineamento);
				if (response != null && response.equalsIgnoreCase("ok")) {
					esperienzaSvoltaAllineamento.setAllineato(true);
					esperienzaSvoltaAllineamento.setDaAllineare(false); //shall i nullify errore, tentativi string here
				} else {
					esperienzaSvoltaAllineamento.setAllineato(false);
					esperienzaSvoltaAllineamento.setDaAllineare(true);
					esperienzaSvoltaAllineamento.setNumeroTentativi(esperienzaSvoltaAllineamento.getNumeroTentativi() + 1);
					int index = response.length() > 2000 ? 2000 : response.length();
					esperienzaSvoltaAllineamento.setErrore(response.substring(0, index));
				}
			} catch (Exception e) {
				esperienzaSvoltaAllineamento.setAllineato(false);
				esperienzaSvoltaAllineamento.setDaAllineare(true);
				esperienzaSvoltaAllineamento.setNumeroTentativi(esperienzaSvoltaAllineamento.getNumeroTentativi() + 1);
				if (e.getMessage() != null && !e.getMessage().isEmpty()) {
					int index = e.getMessage().length() > 2000 ? 2000 : e.getMessage().length();
					esperienzaSvoltaAllineamento.setErrore(e.getMessage().substring(0, index)); // save only first 2000 chars
				}
				logger.error(e);
			}
			esperienzaAllineamentoRepository.save(esperienzaSvoltaAllineamento);
		}
	}

	@Scheduled(cron = "0 00 03 * * ?")
	public void allineamentEsperienza() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask per allineare esperienza");
		}
		for (EsperienzaSvoltaAllineamento esAl : esperienzaAllineamentoRepository.findByDaAllineare(true)) {
			allineaEsperienzaSvolta(esAl);
		}
	}

	public void deleteEsperienzaSvoltaAllineamento(Long esperienzaSvoltaId) {
		EsperienzaSvoltaAllineamento esa = esperienzaAllineamentoRepository.findByEspSvoltaId(esperienzaSvoltaId);
		if(esa != null) {
			esperienzaAllineamentoRepository.delete(esa);
		}
	}

	
}
