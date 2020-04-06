package it.smartcommunitylab.cartella.asl.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

@Entity
public class ScheduleUpdate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public Map<String, MetaInfoType> getUpdateMap() {
		return updateMap;
	}

	public void setUpdateMap(Map<String, MetaInfoType> updateMap) {
		this.updateMap = updateMap;
	}

	@OneToMany(mappedBy = "scheduleupdate", cascade = { CascadeType.ALL})
	@MapKey(name = "type")
	private Map<String, MetaInfoType> updateMap = new HashMap<>();

}
