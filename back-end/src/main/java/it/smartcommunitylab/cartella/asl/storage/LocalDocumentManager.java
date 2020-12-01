package it.smartcommunitylab.cartella.asl.storage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import it.smartcommunitylab.cartella.asl.exception.ASLCustomException;
import it.smartcommunitylab.cartella.asl.model.AttivitaAlternanza;
import it.smartcommunitylab.cartella.asl.model.Documento;
import it.smartcommunitylab.cartella.asl.model.Documento.TipoDoc;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvolta;
import it.smartcommunitylab.cartella.asl.repository.AttivitaAlternanzaRepository;
import it.smartcommunitylab.cartella.asl.repository.DocumentoRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaSvoltaRepository;

@Component
public class LocalDocumentManager {
	private static final transient Logger logger = LoggerFactory.getLogger(LocalDocumentManager.class);

	@Value("${storage.local.dir}")
	private String storageDir;

	@Autowired
	private DocumentoRepository documentoRepository;
	
	@Autowired
	private AttivitaAlternanzaRepository attivitaAlternanzaRepository;
	
	@Autowired
	private EsperienzaSvoltaRepository esperienzaSvoltaRepository;

	public boolean removeDocument(String uuid) throws Exception {

		boolean removed = false;
		Documento doc = documentoRepository.findDocumentoByUuid(uuid);

		try {
			deleteFile(uuid);
			documentoRepository.delete(doc);
			removed = true;
		} catch (Exception e) {
			throw new ASLCustomException(e.getMessage());
		}

		return removed;

	}
	
	public void deleteDocumentsByRisorsaId(String risorsaId) {
		List<Documento> documents = documentoRepository.findDocumentoByRisorsaId(risorsaId);
		for(Documento doc : documents) {
			try {
				deleteFile(doc.getUuid());
				documentoRepository.delete(doc);
			} catch (Exception e) {
				logger.warn(String.format("error deleting file %s", doc.getUuid()));
			}
		}
	}

	private File saveFile(MultipartFile data, String key) throws IOException {
		File file = new File(storageDir, key);
		data.transferTo(file);

		return file;
	}

	public File loadFile(String name) throws IOException {
		File file = new File(storageDir, name);

		return file;
	}

	public boolean deleteFile(String name) throws IOException {
		File file = new File(storageDir, name);
		return file.delete();

	}

	public Documento getEntity(String uuid) {
		return documentoRepository.findDocumentoByUuid(uuid);
	}


	public Documento addDocumentToRisorsa(String uuid, TipoDoc tipo, MultipartFile data, HttpServletRequest request) throws Exception {
		Documento doc = new Documento();
		doc.setUuid(UUID.randomUUID().toString());
		doc.setRisorsaId(uuid);
		doc.setFormatoDocumento(data.getContentType());
		doc.setNomeFile(data.getOriginalFilename());
		doc.setDataUpload(LocalDate.now());
		doc.setTipo(tipo);
		if (data != null) {
			saveFile(data, doc.getUuid());
		}
		documentoRepository.save(doc);
		return doc;
	}

	public List<Documento> getDocument(String risorsaId) throws Exception {
		List<Documento> docs = documentoRepository.findDocumentoByRisorsaId(risorsaId);
		return docs;
	}
	
	public List<Documento> getDocumentByAttivita(String risorsaId) throws Exception {
		List<String> risorsaIds = new ArrayList<String>();
		AttivitaAlternanza aa = attivitaAlternanzaRepository.findByUuid(risorsaId);
		if(aa != null) {
			risorsaIds.add(risorsaId);
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaRepository.findByAttivitaAlternanzaId(aa.getId());
			esperienze.forEach(e -> risorsaIds.add(e.getUuid()));
		}
		List<Documento> docs = documentoRepository.findByRisorsaIdIn(risorsaIds);
		return docs;
	}

	public List<Documento> getDocumentByStudente(String uuid) throws Exception {
		List<Documento> result = new ArrayList<>();
		EsperienzaSvolta es = esperienzaSvoltaRepository.findByUuid(uuid);
		if(es != null) {
			List<Documento> list = documentoRepository.findDocumentoByRisorsaId(uuid);
			list.forEach(doc -> {
				if(doc.getTipo().equals(TipoDoc.doc_generico) || 
						doc.getTipo().equals(TipoDoc.valutazione_esperienza)) {
					result.add(doc);
				}
			});			
			AttivitaAlternanza aa = attivitaAlternanzaRepository.findById(es.getAttivitaAlternanzaId()).orElse(null);
			if(aa != null) {
				list = documentoRepository.findDocumentoByRisorsaId(aa.getUuid());
				list.forEach(doc -> {
					if(doc.getTipo().equals(TipoDoc.piano_formativo) ||
							doc.getTipo().equals(TipoDoc.doc_generico) || 
							doc.getTipo().equals(TipoDoc.valutazione_studente) ||
							doc.getTipo().equals(TipoDoc.valutazione_esperienza)) {
						result.add(doc);
					}
				});			
			}
		}
		return result;
 	}

	public List<Documento> getDocumentByEnte(String uuid) {
		List<Documento> result = new ArrayList<>();
		AttivitaAlternanza aa = attivitaAlternanzaRepository.findByUuid(uuid);
		if(aa != null) {
			List<Documento> list = documentoRepository.findDocumentoByRisorsaId(uuid);
			list.forEach(doc -> {
				if(doc.getTipo().equals(TipoDoc.piano_formativo) ||
						doc.getTipo().equals(TipoDoc.doc_generico) || 
						doc.getTipo().equals(TipoDoc.valutazione_studente) ||
						doc.getTipo().equals(TipoDoc.convenzione)) {
					result.add(doc);
				}
			});			
			List<EsperienzaSvolta> esperienze = esperienzaSvoltaRepository.findByAttivitaAlternanzaId(aa.getId());
			for(EsperienzaSvolta e: esperienze) {
				list = documentoRepository.findDocumentoByRisorsaId(e.getUuid());
				list.forEach(doc -> {
					if(doc.getTipo().equals(TipoDoc.valutazione_esperienza) ||
							doc.getTipo().equals(TipoDoc.doc_generico)) {
						result.add(doc);
					}
				});			
			}
		}
		return result;
	}
}
