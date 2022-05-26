package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.repository.MetaInfoRepository;
import it.smartcommunitylab.cartella.asl.repository.StudenteRepository;
import it.smartcommunitylab.cartella.asl.services.CartellaImportAziende;
import it.smartcommunitylab.cartella.asl.services.CartellaImportCourse;
import it.smartcommunitylab.cartella.asl.services.CartellaImportCourseMetaInfo;
import it.smartcommunitylab.cartella.asl.services.CartellaImportInstitutes;
import it.smartcommunitylab.cartella.asl.services.CartellaImportRegistration;
import it.smartcommunitylab.cartella.asl.services.CartellaImportStudente;
import it.smartcommunitylab.cartella.asl.services.CartellaImportTeachingUnit;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportAziende;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportCorsi;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportCourseMetaInfo;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportIscrizioneCorsi;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportIstituzioni;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportProfessori;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportProfessoriClassi;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportStudenti;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportUnita;

@Component
@Transactional
public class APIUpdateManager {

	private static final transient Log logger = LogFactory.getLog(APIUpdateManager.class);

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;

	@Value("${infoTN.startingYear}")
	private int startingYear;

	@Value("${infoTN.user}")
	private String user;

	@Value("${infoTN.pass}")
	private String pass;

	@Autowired
	private MetaInfoRepository metaInfoRepository;
	@Autowired
	private InfoTnImportProfessori infoTnImportProfessori;
	@Autowired
	private InfoTnImportProfessoriClassi infoTnImportProfesoriClassi;
	@Autowired
	private InfoTnImportIstituzioni infoTnImportIstituzioni;
	@Autowired
	private InfoTnImportUnita infoTnImportUnita;
	@Autowired
	private InfoTnImportCourseMetaInfo infoTnImportCourseMetaInfo;
	@Autowired
	private InfoTnImportCorsi infoTnImportCorsi;
	@Autowired
	private InfoTnImportStudenti infoTnImportStudenti;
	@Autowired
	private InfoTnImportIscrizioneCorsi infoTnImportIscrizioneCorsi;
	@Autowired
	private InfoTnImportAziende infoTnImportAziende;
	@Autowired
	private CartellaImportRegistration importCartellaRegistation;
	@Autowired
	private CartellaImportStudente importCartellaStudente;
	@Autowired
	private CartellaImportInstitutes importCartellaIstituti;
	@Autowired
	private CartellaImportTeachingUnit importCartellaTeachingUnit;
	@Autowired
	private CartellaImportAziende importCartellaAziende;
	@Autowired
	private CartellaImportCourseMetaInfo importCartellaCourseMetaInfo;
	@Autowired
	private CartellaImportCourse importCartellaCourses;
	@Autowired
	StudenteRepository studenteRepository;
	@Autowired
	protected EntityManager em;
	@Autowired
	protected AziendaManager aziendaManager;

	public List<MetaInfo> fetchMetaInfoForAPI(String key) {
		String q = "SELECT mi FROM  MetaInfo mi WHERE mi.name = (:key) ORDER BY mi.schoolYear ASC";
		TypedQuery<MetaInfo> query = em.createQuery(q, MetaInfo.class);
		query.setParameter("key", key);
		return query.getResultList();
	}

	public void saveMetaInfoList(String key, List<MetaInfo> list) {
		for(MetaInfo mi : list) {
			metaInfoRepository.update(mi);
		}
	}

	public void importAllCartella() {
		if (logger.isInfoEnabled()) {
			logger.info("start CartellaScheduledTask.importAll");
		}
		// istituti.
		importCartellaIstituti.importIstitutiFromRESTAPI();
		// teaching unit.
		importCartellaTeachingUnit.importTeachingUnitFromRESTAPI();
		// aziende.
		importCartellaAziende.importAziendeFromRESTAPI();
		// courseMetaInfo.
		importCartellaCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
		// course.
		importCartellaCourses.importCoursesFromRESTAPI();
		// students.
		importCartellaStudente.importStudentsFromRESTAPI();
		// registration.
		importCartellaRegistation.importRegistationFromRESTAPI();
	}

	public void importCartellaRegistration() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start ScheduledTask.importCartellaRegistration(" + new Date() + ")");
		}
		// registation.
		importCartellaRegistation.importRegistationFromRESTAPI();
	}

	public void importCartellaStudenti() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importCartellaStudenti");
		}
		// students.
		importCartellaStudente.importStudentsFromRESTAPI();
	}

	public void importCartellaIstituti() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importCartellaIstituti");
		}
		// istituti.
		importCartellaIstituti.importIstitutiFromRESTAPI();
	}

	public void importCartellaTeachingUnit() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importCartellaTeachingUnit");
		}
		// teaching unit.
		importCartellaTeachingUnit.importTeachingUnitFromRESTAPI();
	}

	public void importCartellaAziende() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importCartellaAziende");
		}
		// aziende.
		importCartellaAziende.importAziendeFromRESTAPI();
		// align aziende.
		aziendaManager.alignAziendeConsoleInfoTN();
	}

	public void importCartellaCourseMetaInfo() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importCartellaCourseMetaInfo");
		}
		// courseMetaInfo.
		importCartellaCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
	}

	public void importCartellaCourses() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importCartellaCourses");
		}
		// course.
		importCartellaCourses.importCoursesFromRESTAPI();
	}

	public void importAllInfoTN() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importAllInfoTN");
		}
		infoTnImportAziende.importAziendaFromRESTAPI();
		infoTnImportIstituzioni.importIstituzioniFromRESTAPI();
		infoTnImportUnita.importUnitaFromRESTAPI();
		infoTnImportCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
		infoTnImportCorsi.importCorsiFromRESTAPI();
		infoTnImportStudenti.importStudentiFromRESTAPI();
		infoTnImportIscrizioneCorsi.importIscrizioneCorsiFromRESTAPI();
		infoTnImportProfesoriClassi.importProfessoriClassiFromRESTAPI();
		infoTnImportProfessori.importProfessoriFromRESTAPI();
	}

	@Transactional
	public void importInfoTNProfessori() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNProfessori");
		}
		// professori.
		infoTnImportProfessori.importProfessoriFromRESTAPI();
	}

	@Transactional
	public void importInfoTNProfessoriClassi() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNProfessoriClassi");
		}
		// professoriClassi.
		infoTnImportProfesoriClassi.importProfessoriClassiFromRESTAPI();
	}
	
	public void importInfoTNIstituti() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNIstituti");
		}
		infoTnImportIstituzioni.importIstituzioniFromRESTAPI();
	}

	public void importInfoTNUnita() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNUnita");
		}
		infoTnImportUnita.importUnitaFromRESTAPI();
	}
	
	public void importInfoTNCorsiMetaInfo() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNCorsiMetaInfo");
		}
		infoTnImportCourseMetaInfo.importCourseMetaInfoFromRESTAPI();
	}
	
	public void importInfoTNCorsi() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNCorsi");
		}
		infoTnImportCorsi.importCorsiFromRESTAPI();
	}
	
	@Transactional
	public void importInfoTNStudenti() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNStudenti");
		}
		infoTnImportStudenti.importStudentiFromRESTAPI();
	}
	
	@Transactional
	public void importInfoTNRegistrazioni() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNRegistrazioni");
		}
		infoTnImportIscrizioneCorsi.importIscrizioneCorsiFromRESTAPI();
	}
	
	@Transactional
	public void importInfoTNAziende() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNAziende");
		}
		infoTnImportAziende.importAziendaFromRESTAPI();
	}
	
	public List<MetaInfo> createMetaInfoForAPI(String apiKey, boolean multipleYears) {
		List<MetaInfo> metaInfos = new ArrayList<MetaInfo>();
		if (multipleYears) {
			for (int i = startingYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
				MetaInfo metaInfo = new MetaInfo();
				metaInfo.setName(apiKey);
				metaInfo.setSchoolYear(i);
				metaInfoRepository.save(metaInfo);
				metaInfos.add(metaInfo);
			}
		} else {
			MetaInfo metaInfo = new MetaInfo();
			metaInfo.setName(apiKey);
			metaInfoRepository.save(metaInfo);
			metaInfos.add(metaInfo);
		}
		return metaInfos;
	}

}
