package it.smartcommunitylab.cartella.asl.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Splitter;
import com.google.common.primitives.Doubles;

import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.manager.ASLRolesValidator;
import it.smartcommunitylab.cartella.asl.manager.StatisticsManager;
import it.smartcommunitylab.cartella.asl.model.Competenza;
import it.smartcommunitylab.cartella.asl.model.statistics.Instance;
import it.smartcommunitylab.cartella.asl.model.statistics.Internship;
import it.smartcommunitylab.cartella.asl.model.statistics.KPI;
import it.smartcommunitylab.cartella.asl.model.statistics.POI;
import it.smartcommunitylab.cartella.asl.model.users.ASLRole;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.model.users.ASLUserRole;

@RestController
public class StatisticsController {

	@Autowired
	private StatisticsManager statisticsManager;
	
	@Autowired
	private ASLRolesValidator usersValidator;	
	
	@GetMapping("/api/statistics/internships")
	public List<Internship> getInternships(@RequestParam(required=false) Integer typology, @RequestParam(required=false) Long from, @RequestParam(required=false) Long to, @RequestParam(required = false) String coordinates, @RequestParam(required = false) Integer radius, @RequestParam(required = false) String companyId, @RequestParam(required = false) String courseId) throws Exception {
		double[] coords = null;
		if (coordinates != null && !coordinates.isEmpty()) {
			coords = Doubles.toArray(Splitter.on(",").splitToList(coordinates).stream().map(x -> Double.parseDouble(x)).collect(Collectors.toList()));
		}		
		
		List<Internship> result = statisticsManager.findInternships(typology, from, to, coords, radius, companyId, courseId);
	
		return result;
	}
	
	@GetMapping("/api/statistics/internships/instances")
	public List<Instance> getInternshipsInstances(@RequestParam(required=false) Integer typology, @RequestParam(required=false) Long from, @RequestParam(required=false) Long to, @RequestParam(required = false) String coordinates, @RequestParam(required = false) Integer radius, @RequestParam(required = false) String companyId, @RequestParam(required = false) String courseId) throws Exception {
		double[] coords = null;
		if (coordinates != null && !coordinates.isEmpty()) {
			coords = Doubles.toArray(Splitter.on(",").splitToList(coordinates).stream().map(x -> Double.parseDouble(x)).collect(Collectors.toList()));
		}		
		
		List<Instance> result = statisticsManager.findInternshipsInstances(typology, from, to, coords, radius, companyId, courseId);
	
		return result;
	}	
	
	@GetMapping("/api/statistics/companies")
	public List<POI> getCompanies(@RequestParam(required = false) String coordinates, @RequestParam(required = false) Integer radius) throws Exception {
		double[] coords = null;
		if (coordinates != null && !coordinates.isEmpty()) {
			coords = Doubles.toArray(Splitter.on(",").splitToList(coordinates).stream().map(x -> Double.parseDouble(x)).collect(Collectors.toList()));
		}		
		
		List<POI> result = statisticsManager.findCompanies(coords, radius);
	
		return result;
	}	
	
	@GetMapping("/api/statistics/companies/kpi")
	public List<KPI> getCompaniesKPI(@RequestParam(required = false) String companyId, @RequestParam(required = false) String schoolYear) throws Exception {
		return statisticsManager.getAziendeKPI(companyId, schoolYear);
	}	
	
	@GetMapping("/api/statistics/companies/partecipating/kpi")
	public KPI getPartecipatingCompaniesKPI(@RequestParam(required = false) String companyId, @RequestParam(required = false) String schoolYear) throws Exception {
		return statisticsManager.getPartecipatingCompaniesKPI();
	}		
	
	@GetMapping("/api/statistics/institutes/kpi")
	public List<KPI> getInstitutesKPI(@RequestParam(required = false) String instituteId, @RequestParam(required = false) String schoolYear) throws Exception {
		return statisticsManager.getIstitutiKPI(instituteId, schoolYear);
	}	
	
	@GetMapping("/api/statistics/internships/kpi")
	public KPI getInternshipsKPI(@RequestParam(required = false) Long skillId) throws Exception {
		return statisticsManager.countOpportunitaPerSkill(skillId);
	}	
	
	@GetMapping("/api/statistics/match/kpi")
	public List<KPI> getMatchKPI(@RequestParam(required = false) String instituteId, @RequestParam(required = false) String courseId) throws Exception {
		return statisticsManager.match(instituteId, courseId);
	}	
		
	
	
	@GetMapping("/api/statistics/competenze")
	public List<Competenza> getSkills() throws Exception {
		return statisticsManager.getCompetenze();
	}
	
	@GetMapping("/api/statistics/skills/student")
	public Map<String, Object> getStudent(HttpServletRequest request) throws Exception {
		ASLUser user = usersValidator.getUser(request);
		
		if (user != null) {
			ASLUserRole role = user.getRoles().stream().filter(x -> ASLRole.STUDENTE.equals(x.getRole())).findFirst().orElse(null);

			if (role == null) {
				throw new UnauthorizedException("No STUDENTE role found");
			}
			return statisticsManager.getStudentProfile(role.getDomainId());
		} else {
			throw new UnauthorizedException("User not found");
		}
	}		
	
	

	
}
