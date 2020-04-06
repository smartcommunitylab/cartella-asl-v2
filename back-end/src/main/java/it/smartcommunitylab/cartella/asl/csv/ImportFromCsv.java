package it.smartcommunitylab.cartella.asl.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRepository;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRoleRepository;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;

@Component
public class ImportFromCsv {
	private static Log logger = LogFactory.getLog(ImportFromCsv.class);
	
	@Autowired
	private StudenteRepository studenteRepository;
	
//	@Autowired
//	private ReferenteAlternanzaRepository referenteAlternanzaRepository;
	
	@Autowired
	private IstituzioneRepository istituzioneRepository;
	
	@Autowired
	private ASLUserRepository aslUserRepository;
	@Autowired
	private ASLUserRoleRepository roleRepository;
	
	/**
	 * Create ASLUser starting from csv info and studenteRepository.
	 * @param csv : [student-cf,student-email]
	 * @return
	 */
	public List<ASLUser> importStudenteRole(Reader contentReader) {
		BufferedReader in = new BufferedReader(contentReader);
		List<ASLUser> result = new ArrayList<>();
		String line;
		try {
			while((line = in.readLine()) != null) {
				try {
					String[] strings = line.split(";");
					String cf = strings[0].toUpperCase().trim();
					String email = strings[1].trim();
					Studente studente = studenteRepository.findStudenteByCf(cf);
					if(studente == null) {
						logger.warn("importStudente: student not found - " + cf);
						continue;
					}
					ASLUser aslUser = aslUserRepository.findByCf(cf);
					if(aslUser == null) {
						aslUser = new ASLUser();
						aslUser.setCf(cf);
						aslUser.setEmail(email);
						aslUser.setName(studente.getName());
						aslUser.setSurname(studente.getSurname());
						aslUserRepository.save(aslUser);
						ASLUserRole userRole = new ASLUserRole(ASLRole.STUDENTE, studente.getId(), aslUser.getId());
						roleRepository.save(userRole);
						result.add(aslUser);
						logger.warn("importStudente: aslUser created - " + cf);
					} else {
						logger.warn("importStudente: aslUser already present - " + cf);
					}
				} catch (Exception e) {
					logger.warn("importStudente:" + e.getMessage());
				}
			}
		} catch (IOException e) {
			logger.warn("importStudente:" + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Create ASLUser starting from csv info and referenteAlternanzaRepository and istituzioneRepository.
	 * @param csv : [referente-cf,referente-email,istitute-extId] 
	 * @return
	 */
	public List<ASLUser> importFunzioneStrumentaleRole(Reader contentReader) {
		BufferedReader in = new BufferedReader(contentReader);
		List<ASLUser> result = new ArrayList<>();
		String line;
		try {
			while((line = in.readLine()) != null) {
				try {
					String[] strings = line.split(";");
//					String cf = strings[0].toUpperCase().trim();
					String email = strings[0].trim();
					String extId = strings[1].trim();
					String name = strings[2].trim();
					String surname = strings[3].trim();
//					ReferenteAlternanza referente = 
//							referenteAlternanzaRepository.findReferenteAlternanzaByCf(cf);
//					if(referente == null) {
//						logger.warn("importFunzioneStrumentale: referente not found - " + cf);
//						continue;
//					}
					Istituzione istituzione = istituzioneRepository.findIstitutzioneByExtId("INFOTNISTRUZIONE", extId);
					if(istituzione == null) {
						logger.warn("importFunzioneStrumentale: istituzione not found - " + extId);
						continue;
					}
					ASLUser aslUser = aslUserRepository.findByEmail(email);
					if(aslUser == null) {
						aslUser = new ASLUser();
//						aslUser.setCf(cf);
						aslUser.setEmail(email);
						aslUser.setName(name);
						aslUser.setSurname(surname);
						aslUserRepository.save(aslUser);
						ASLUserRole userRole = new ASLUserRole(ASLRole.FUNZIONE_STRUMENTALE, istituzione.getId(), aslUser.getId());
						roleRepository.save(userRole);
						result.add(aslUser);
					} else {
						logger.warn("importFunzioneStrumentale: aslUser already present - " + email);
					}
				} catch (Exception e) {
					logger.warn("importFunzioneStrumentale:" + e.getMessage());
				}
			}
		} catch (IOException e) {
			logger.warn("importFunzioneStrumentale:" + e.getMessage());
		}
		return result;
	}
}
