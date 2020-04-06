package it.smartcommunitylab.cartella.asl.model.users;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;


public class ASLAuthCheck {

	private ASLRole role;
	private List<Object> entityIds;

	public ASLAuthCheck(ASLRole role, List<Object> entityIds) {
		super();
		this.role = role;
		this.entityIds = entityIds;
	}
	
	public ASLAuthCheck(ASLRole role, Object entityId) {
		super();
		this.role = role;
		this.entityIds = Lists.newArrayList(entityId);
	}
	
	public ASLRole getRole() {
		return role;
	}
	public void setRole(ASLRole role) {
		this.role = role;
	}
	public List<Object> getEntityIds() {
		return entityIds;
	}
	public void setEntityIds(List<Object> entityIds) {
		this.entityIds = entityIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityIds == null) ? 0 : entityIds.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ASLAuthCheck other = (ASLAuthCheck) obj;
		if (entityIds == null) {
			if (other.entityIds != null)
				return false;
		} else if (!entityIds.equals(other.entityIds))
			return false;
		if (role != other.role)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return role + ": " + (entityIds != null && !entityIds.contains(null) ? Joiner.on(",").join(entityIds) : null);
//		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}		
	
	
	
}
