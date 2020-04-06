package it.smartcommunitylab.cartella.asl.model.audit;

import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.base.Joiner;

import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

@Entity
@Table(name = "audit")
public class AuditEntry {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;	
	
	private long timestamp;
	
	private String operationType;
	
	private String resourceType;
	private String resourceId;


	@Transient
	private ASLUser user;
	
	private String userRoles;
	
	private Long userId;
	private String userCf;
	private String userEmail;
	
	private String callerClass;
	private String callerMethod;
	
	public AuditEntry() {
	}
	
	public AuditEntry(String operation, Class resource, Object entityId, ASLUser user, Object caller) {
		this.operationType = operation;
		this.resourceType = resource.getSimpleName();
		this.resourceId = entityId.toString();
		this.user = user;
		if (user != null) {
			this.userRoles = Joiner.on(",").join(user.getRoles().stream().map(x -> x.getRole()).collect(Collectors.toSet()));
			this.userId = user.getId();
			this.userCf = user.getCf();
			this.userEmail = user.getEmail();
		}
		this.callerClass = caller.getClass().getEnclosingMethod().getDeclaringClass().getSimpleName();
		this.callerMethod = caller.getClass().getEnclosingMethod().getName();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}



	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}



	public ASLUser getUser() {
		return user;
	}

	public void setUser(ASLUser user) {
		this.user = user;
	}

	public String getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserCf() {
		return userCf;
	}

	public void setUserCf(String userCf) {
		this.userCf = userCf;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getCallerClass() {
		return callerClass;
	}

	public void setCallerClass(String callerClass) {
		this.callerClass = callerClass;
	}

	public String getCallerMethod() {
		return callerMethod;
	}

	public void setCallerMethod(String callerMethod) {
		this.callerMethod = callerMethod;
	}


	
	
	
}
