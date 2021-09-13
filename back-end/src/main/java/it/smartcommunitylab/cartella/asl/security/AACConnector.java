package it.smartcommunitylab.cartella.asl.security;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.cartella.asl.exception.UnauthorizedException;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRepository;
import it.smartcommunitylab.cartella.asl.repository.ASLUserRoleRepository;
import it.smartcommunitylab.cartella.asl.util.ErrorLabelManager;

@Component
public class AACConnector {
	
	private static final String EMAIL = "email";
	private static final String CF = "cf";

	private static final transient Logger logger = LoggerFactory.getLogger(AACConnector.class);	
	
//	@Autowired
//	@Value("${oauth.serverUrl}")	
//	private String aacURL;
	
	@Autowired
	private ASLUserRepository userRepository;	
	
	@Autowired
	private ASLUserRoleRepository roleRepository;
	
	@Autowired
	private ErrorLabelManager errorLabelManager;	
	
//	private ObjectMapper mapper = new ObjectMapper();	
	
//	private LoadingCache<String, AccountProfile> accountProfileCache;
	
	@PostConstruct
	public void init() throws Exception {
//		CacheLoader<String, AccountProfile> loader = new CacheLoader<String, AccountProfile>() {
//      @Override
//      public AccountProfile load(String key) throws Exception {
//      	return findAccountProfile(key);
//      }
//		};
//		accountProfileCache = CacheBuilder.newBuilder()
//				.maximumSize(500)
//				.expireAfterWrite(60,TimeUnit.MINUTES)
//	      .build(loader);
	}

	public ASLUser getASLUser(HttpServletRequest request) throws UnauthorizedException {
		
			//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			AccountProfile accountProfile = getAccoutProfile(request);

			ASLUser user = null;
			String result = null;
			String type = null;
			
			BearerTokenAuthentication authentication = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
			OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
			//TODO check cf
			result = (String) principal.getAttributes().get("email");
			type = EMAIL;
			
			/*if (accountProfile != null) {
				if (accountProfile.getAccounts().containsKey("adc")) {
					result = accountProfile.getAttribute("adc", "pat_attribute_codicefiscale");
					type = CF;
				} else if (accountProfile.getAccounts().containsKey("cie")) {
					result = accountProfile.getAttribute("cie", "fiscalNumberId");
					type = CF;
				} else {
					Map<String, String> accountAttributes = null;
					if (accountProfile.getAccounts().containsKey("google")) {
						accountAttributes = accountProfile.getAccountAttributes("google");
					} else if (accountProfile.getAccounts().containsKey("facebook")) {
						accountAttributes = accountProfile.getAccountAttributes("facebook");
					} else if (accountProfile.getAccounts().containsKey("internal")) {
						accountAttributes = accountProfile.getAccountAttributes("internal");
					}
					result = accountAttributes.get(EMAIL);
					type = EMAIL;
				}*/

				if (result != null) {
					if (EMAIL.equals(type)) {
						user = getASLUser(result);
					} else if (CF.equals(type)) {
						user = getASLUserByCF(result);
					}
				} else {
					throw new UnauthorizedException(String.format(errorLabelManager.get("api.access.error")));
				}
				
				if (user == null) {
					logger.warn(String.format("User not found:%s - %s", type, result));
					throw new UnauthorizedException(String.format(errorLabelManager.get("user.notfound"), result));
				}

//			} else {
//				throw new UnauthorizedException(String.format(errorLabelManager.get("api.access.error")));
//			}
			return user;			
	}	
	
	private ASLUser getASLUser(String email) throws UnauthorizedException {
		
		ASLUser user = null;
		
		if (email != null) {
			user = userRepository.findByEmail(email.toLowerCase());
		}
		if(user != null) {
			user.getRoles().addAll(roleRepository.findByUserId(user.getId()));
		}
		
		return user;
	}	
	
	private ASLUser getASLUserByCF(String cf) throws UnauthorizedException {
		ASLUser user = null;
		
		if (cf != null) {
			user = userRepository.findByCf(cf.toUpperCase());
		}		
		if(user != null) {
			user.getRoles().addAll(roleRepository.findByUserId(user.getId()));
		}
		
		return user;
	}	
	
//	private ASLUser getASLUser(Authentication authentication) throws UnauthorizedException {
//		String email = authentication.getName();
//		//TODO get CF, name, surname
//		String cf = null;
//		String name = null;
//		String surname = null;
//		
//		ASLUser user = null;
//		
//		if (email != null) {
//			user = userRepository.findByEmail(email);
//		} else if (cf != null) {
//			user = userRepository.findByCf(cf);
//		}
//		
//		if (user == null) {
//			throw new UnauthorizedException(String.format(errorLabelManager.get("user.notfound"), name, surname));
//		}
//		user.getRoles().addAll(roleRepository.findByUserId(user.getId()));
//		
//		return user;
//	}
	
//	private AccountProfile getAccoutProfile(HttpServletRequest request) {
//		AccountProfile result = null;
//		String token = request.getHeader("Authorization");
//		if (token != null && !token.isEmpty()) {
//			token = token.replace("Bearer ", "");
//			try {
//				result = accountProfileCache.get(token);
//			} catch (Exception e) {
//				if (logger.isWarnEnabled()) {
//					logger.warn(String.format("getAccoutProfile[%s]: %s", token, e.getMessage()));
//				}
//			} 
//		}
//		return result;
//	}	
	
//	private AccountProfile findAccountProfile(String token)  throws SecurityException, AACException {
//		logger.info("findAccountProfile: " + token);
//		try {
//	        final HttpResponse resp;
//	        String url = aacURL + "accountprofile/me";
//	        final HttpGet get = new HttpGet(url);
//	        get.setHeader("Accept", "application/json");
//	        get.setHeader("Authorization", "Bearer " + token);
//	        try {
//	            resp = getHttpClient().execute(get);
//	            final String response = EntityUtils.toString(resp.getEntity());
//	            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//	            	AccountProfile data = mapper.readValue(response, AccountProfile.class);
//	                return data;
//	            }
//	            throw new AACException("Error in accountprofile/me " + resp.getStatusLine());
//	        } catch (final Exception e) {
//	            throw new AACException(e);
//	        }
//		} catch (Exception e) {
//			throw new AACException(e);
//		}
//	}	
			
//    private HttpClient getHttpClient() {
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        return httpClient;
//    }	
	
}
