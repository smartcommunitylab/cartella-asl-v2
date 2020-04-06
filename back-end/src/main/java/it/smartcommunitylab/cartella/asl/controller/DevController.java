package it.smartcommunitylab.cartella.asl.controller;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylab.cartella.asl.csv.ImportFromCsv;
import it.smartcommunitylab.cartella.asl.manager.APIUpdateManager;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.DataManager;
import it.smartcommunitylab.cartella.asl.manager.EsperienzaAllineamentoManager;
import it.smartcommunitylab.cartella.asl.manager.QueriesManager;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.EsperienzaSvoltaAllineamento;
import it.smartcommunitylab.cartella.asl.model.TipologiaTipologiaAttivita;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRepository;
import it.smartcommunitylab.cartella.asl.repository.CompetenzaRepository;
import it.smartcommunitylab.cartella.asl.repository.EsperienzaAllineamentoRepository;
import it.smartcommunitylab.cartella.asl.repository.TipologiaTipologiaAttivitaRepository;
import it.smartcommunitylab.cartella.asl.util.JsonDB;
import it.smartcommunitylab.cartella.asl.util.Utils;

@RestController
public class DevController {

	@Autowired
	private QueriesManager aslManager;

	@Autowired
	private CompetenzaRepository cRepository;

	@Autowired
	private EsperienzaAllineamentoRepository esperienzaAllineamentoRepository;

	@Autowired
	private TipologiaTipologiaAttivitaRepository tRepository;

	@Autowired
	private ASLUserRepository uRepository;

	@Autowired
	private APIUpdateManager apiUpdateManager;

	@Autowired
	private JsonDB jsonDB;

	@Autowired
	private ASLRolesValidator usersValidator;

	@Autowired
	private ImportFromCsv csvManager;
	
	@Autowired
	private DataManager dataManager;
	
	@Autowired
	private EsperienzaAllineamentoManager esperienzaAllineamentoManager;
	

	@Value("${import.dir}")
	private String importPath;

	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false);

	private static Log logger = LogFactory.getLog(DevController.class);
	
	@GetMapping("/api/data")
	public Object getData(@RequestParam(required=false) String type) throws Exception {
		logger.info(String.format("getData[%s]", type));
		if (type == null || type.isEmpty()) {
			return dataManager.getAll();
		}
		return dataManager.get(type);
	}
	
	@GetMapping("/api/tipologiaTipologiaAttivita/{id}")
	public TipologiaTipologiaAttivita getTipologiaAttivita(@PathVariable long id) {
		return aslManager.getTipologiaTipologiaAttivita(id);
	}		
	
	@GetMapping("/api/tipologieTipologiaAttivita")
	public List<TipologiaTipologiaAttivita> getTipologiaAttivita() {
		return aslManager.getTipologieTipologiaAttivita();
	}	

    /**
     * return the string representation of anno scolastico given a date
     * 
     * 
     * @param anno expected format YYYY-MM-dd, default value is now
     * @return string representing the anno scolastico in the form of YYYY-YY
     */
    @GetMapping("/api/annoScolastico-by-date")
    public String getAnnoScolastico(@RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE) Date anno) {
        if (anno == null) {
            anno = new Date();
        }
        return Utils.annoScolastico(anno);
    }


    /**
     * return the string representation of anno scolastico given a timestamp
     * 
     * 
     * @param anno expected as timestamp, default value is now
     * @return string representing the anno scolastico in the form of YYYY-YY
     */
    @GetMapping("/api/annoScolastico-by-timestamp")
    public String getAnnoScolastico(@RequestParam(required = false) Long anno) {
        if (anno == null) {
            anno = System.currentTimeMillis();
        }
        return Utils.annoScolastico(anno);
    }

	@GetMapping("/admin/importStudenteRole")
	public void importStudenteRole(@RequestParam String filePath, HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		File file = new File(filePath);
		if (file.exists()) {
			FileReader fileReader = new FileReader(file);
			csvManager.importStudenteRole(fileReader);
			logger.warn("importStudenteRole - file imported:" + filePath);
		} else {
			logger.warn("importStudenteRole - file doesn't exists:" + filePath);
		}
	}

	@GetMapping("/admin/importFunzioneStrumentaleRole")
	public void importFunzioneStrumentaleRole(@RequestParam String filePath, HttpServletRequest request)
			throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		File file = new File(filePath);
		if (file.exists()) {
			FileReader fileReader = new FileReader(file);
			csvManager.importFunzioneStrumentaleRole(fileReader);
			logger.warn("importFunzioneStrumentaleRole - file imported:" + filePath);
		} else {
			logger.warn("importFunzioneStrumentaleRole - file doesn't exists:" + filePath);
		}
	}

	@GetMapping("/admin/importDataset")
	public void initDataset(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);

		List<TipologiaTipologiaAttivita> tipologie = mapper.readValue(
				new File(importPath + "/tipologiaTipologiaAttivita.json"),
				new TypeReference<List<TipologiaTipologiaAttivita>>() {
				});
		tRepository.saveAll(tipologie);

		List<Competenza> competenze = mapper.readValue(new File(importPath + "/competenze.json"),
				new TypeReference<List<Competenza>>() {
				});
		cRepository.saveAll(competenze);

	}

	@GetMapping("/admin/addCompetences")
	public void addCompetences(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<Competenza> competenze = mapper.readValue(new File(importPath + "/competenze.json"),
				new TypeReference<List<Competenza>>() {
				});
		cRepository.saveAll(competenze);
	}

	@GetMapping("/admin/importUsers")
	public void initUsers(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		List<ASLUser> users = mapper.readValue(new File(importPath + "/users.json"),
				new TypeReference<List<ASLUser>>() {
				});
		uRepository.saveAll(users);

	}

	@GetMapping("/admin/importJsonDB")
	public void importJsonDB(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		delete();
		jsonDB.importPianiAlternanza();
		jsonDB.importTipologieAttivita();
		jsonDB.importAttivitaAlternanza();
		jsonDB.importEsperienzeSvolte();
		jsonDB.importOfferte();
		jsonDB.importPresenzeGiornaliere();

	}

	@GetMapping("/admin/exportJsonDB")
	public void exportJsonDB(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		jsonDB.exportTipologieAttivita();
		jsonDB.exportCompetenze();
		jsonDB.exportEsperienzeSvolte();
		jsonDB.exportPianiAlternanza();
	}

	@GetMapping("/admin/dumpCartella")
	public void dumpCartella(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importAllCartella();
	}

	@GetMapping("/admin/dumpCartella/registration")
	public void dumpCartellaRegistration(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaRegistration();
	}

	@GetMapping("/admin/dumpCartella/studenti")
	public void dumpCartellaStudenti(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaStudenti();
	}

	@GetMapping("/admin/dumpCartella/istituti")
	public void dumpCartellaIstituti(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaIstituti();
	}

	@GetMapping("/admin/dumpCartella/aziende")
	public void dumpCartellaAziende(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaAziende();
	}

	@GetMapping("/admin/dumpCartella/courseMetaInfo")
	public void dumpCartellaCourseMetaInfo(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaCourseMetaInfo();
	}

	@GetMapping("/admin/dumpCartella/courses")
	public void dumpCartellaCourses(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaCourses();
	}

	@GetMapping("/admin/dumpCartella/unita")
	public void dumpCartellaTeachingUnit(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importCartellaTeachingUnit();
		;
	}

	@GetMapping("/admin/dumpInfoTN")
	public void dumpInfoTN(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importAllInfoTN();
	}

	@GetMapping("/admin/dumpInfoTN/professori")
	public void dumpInfoTNProfessori(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importInfoTNProfessori();
	}

	@GetMapping("/admin/dumpInfoTN/professoriClassi")
	public void dumpInfoTNProfessoriClassi(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		apiUpdateManager.importInfoTNProfessoriClassi();
	}

	private void delete() {
		aslManager.reset();
	}

	@GetMapping("/admin/scheduleImport")
	public void adminSchedule(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask");
		}
		apiUpdateManager.importCartellaIstituti();
		apiUpdateManager.importCartellaTeachingUnit();
		apiUpdateManager.importCartellaAziende();
		apiUpdateManager.importCartellaCourseMetaInfo();
		apiUpdateManager.importCartellaCourses();
		apiUpdateManager.importCartellaStudenti();
		apiUpdateManager.importCartellaRegistration();
	}

	/**
	 * SCHEDULED ROUTINE
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 00 02 * * ?")
	public void scheduledImport() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("start schedule import from Cartella");
		}
		apiUpdateManager.importCartellaAziende();
		apiUpdateManager.importCartellaStudenti();
		apiUpdateManager.importCartellaRegistration();
	}

	/**
	 * ADMIN API CALL FOR ALLINEARE ESPERIENZA.
	 * 
	 * @throws Exception
	 */
	@GetMapping("/admin/alignEsperienzaInfoTN")
	public void allineamentEsperienzaAdmin(HttpServletRequest request) throws Exception {
		usersValidator.checkRole(request, ASLRole.ADMIN);
		if (logger.isInfoEnabled()) {
			logger.info("start InfoTnScheduledTask per allineare esperienza");
		}
		for (EsperienzaSvoltaAllineamento esAl : esperienzaAllineamentoRepository.findByDaAllineare(true)) {
			esperienzaAllineamentoManager.allineaEsperienzaSvolta(esAl);
		}
	}
}
