package it.smartcommunitylab.cartella.asl.manager;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import it.smartcommunitylab.cartella.asl.model.audit.AuditEntry;
import it.smartcommunitylab.cartella.asl.model.users.ASLUser;
import it.smartcommunitylab.cartella.asl.repository.AuditRepository;

@Component
public class AuditManager {

	@Autowired
	private AuditRepository auditRepository;
	
	private Map<Thread, ASLUser> threads = Maps.newHashMap();
	private Map<Thread, Object> read = Maps.newHashMap();
	private Map<Thread, Object> write = Maps.newHashMap();
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public void beginAudit(ASLUser user) {
		threads.put(Thread.currentThread(), user);
	}
	
	public void endAudit() {
		threads.remove(Thread.currentThread());
		read.remove(Thread.currentThread());
		write.remove(Thread.currentThread());
	}	
	
	public AuditEntry save(AuditEntry entry) {
		entry.setTimestamp(System.currentTimeMillis());
		if (entry.getUser() == null) {
			entry.setUser(threads.get(Thread.currentThread()));
		}
		
		return auditRepository.save(entry);
	}

		
	
}
