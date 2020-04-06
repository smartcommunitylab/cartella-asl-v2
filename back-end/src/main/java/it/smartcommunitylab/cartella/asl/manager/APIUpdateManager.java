package it.smartcommunitylab.cartella.asl.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.cartella.asl.model.MetaInfo;
import it.smartcommunitylab.cartella.asl.model.MetaInfoType;
import it.smartcommunitylab.cartella.asl.model.ScheduleUpdate;
import it.smartcommunitylab.cartella.asl.repository.ScheduleUpdateRepository;
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

	private ScheduleUpdate scheduleUpdate;

	@Value("${infoTN.api}")
	private String infoTNAPIUrl;

	@Value("${infoTN.startingYear}")
	private int startingYear;

	@Value("${infoTN.user}")
	private String user;

	@Value("${infoTN.pass}")
	private String pass;

	@Autowired
	private ScheduleUpdateRepository scheduleUpdateRepository;
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

	@PostConstruct
	public void verifica() {
		// se c'è un oggetto ScheduledUpdate non fa nulla
		if (scheduleUpdateRepository.count() < 1) {
			if (logger.isInfoEnabled()) {
				logger.info("start InfoTnImportTask for fresh import");
			}
			scheduleUpdate = new ScheduleUpdate();
			try {
				// save empty scheduleUpdate object.
				scheduleUpdateRepository.save(scheduleUpdate);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public List<MetaInfo> fetchMetaInfoForAPI(String key) {

		List<MetaInfo> result = null;
		// get updated object from mongo.
		scheduleUpdate = getScheduleUpdate();
		if (scheduleUpdate != null && scheduleUpdate.getUpdateMap().containsKey(key)) {
			result = scheduleUpdate.getUpdateMap().get(key).getMetaInfos();
		}

		return result;

	}

	public void saveMetaInfoList(String key, List<MetaInfo> list) {
		scheduleUpdate.getUpdateMap().get(key).setMetaInfos(list);
		scheduleUpdateRepository.save(scheduleUpdate);
	}

	public ScheduleUpdate getScheduleUpdate() {
		// get update object from mongo.
		if (scheduleUpdateRepository.count() > 0) {
			scheduleUpdate = scheduleUpdateRepository.findAll().get(0);
		}
		return scheduleUpdate;
	}

	public void setScheduleUpdate(ScheduleUpdate scheduleUpdate) {
		this.scheduleUpdate = scheduleUpdate;
	}

	public void saveScheduleUpdate() {
		scheduleUpdateRepository.save(scheduleUpdate);
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

		// init.
		MetaInfoType metaInfoType = new MetaInfoType();
		metaInfoType.setType(apiKey);
		metaInfoType.setScheduleUpdate(scheduleUpdate);
		List<MetaInfo> metaInfos = new ArrayList<MetaInfo>();
		metaInfoType.setMetaInfos(metaInfos);

		if (multipleYears) {

			for (int i = startingYear; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
				MetaInfo metaInfo = new MetaInfo();
				metaInfo.setName(apiKey);
				metaInfo.setSchoolYear(i);
				metaInfos.add(metaInfo);
			}

		} else {
			MetaInfo metaInfo = new MetaInfo();
			metaInfo.setName(apiKey);
			metaInfos.add(metaInfo);
		}

		scheduleUpdate.getUpdateMap().put(apiKey, metaInfoType);

		return metaInfos;

	}

}
