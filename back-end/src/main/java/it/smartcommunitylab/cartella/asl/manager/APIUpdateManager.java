package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import it.smartcommunitylab.cartella.asl.services.InfoTnImportProfessori;
import it.smartcommunitylab.cartella.asl.services.InfoTnImportProfessoriClassi;

@Component
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
	private InfoTnImportProfessori importInfoTNProfessori;
	@Autowired
	private InfoTnImportProfessoriClassi importInfoTnProfesoriClassi;
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

	@Transactional
	public void importCartellaRegistration() throws Exception {

		if (logger.isInfoEnabled()) {
			logger.info("start ScheduledTask.importCartellaRegistration(" + new Date() + ")");
		}
		// registation.
		importCartellaRegistation.importRegistationFromRESTAPI();

	}

	@Transactional
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

	@Transactional
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

	// @Scheduled(cron = "0 58 23 * * ?")
	public void importAllInfoTN() throws Exception {

		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importAll");
		}
		// professori.
		importInfoTNProfessori.importProfessoriFromRESTAPI();
		// professoriClassi.
		importInfoTnProfesoriClassi.importProfessoriClassiFromRESTAPI();

	}

	public void importInfoTNProfessori() throws Exception {

		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNProfessori");
		}
		// professori.
		importInfoTNProfessori.importProfessoriFromRESTAPI();

	}

	public void importInfoTNProfessoriClassi() throws Exception {

		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask.importInfoTNProfessoriClassi");
		}
		// professoriClassi.
		importInfoTnProfesoriClassi.importProfessoriClassiFromRESTAPI();

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
