package it.smartcommunitylab.cartella.asl.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class MetaInfoType {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private ScheduleUpdate scheduleupdate;

	@Column(name = "METAINFO_TYPE")
	private String type;

	@ManyToMany(cascade = { CascadeType.ALL })
	private List<MetaInfo> metaInfos;

	public List<MetaInfo> getMetaInfos() {
		return metaInfos;
	}

	public void setMetaInfos(List<MetaInfo> metaInfos) {
		this.metaInfos = metaInfos;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ScheduleUpdate getScheduleupdate() {
		return scheduleupdate;
	}

	public void setScheduleUpdate(ScheduleUpdate scheduleupdate) {
		this.scheduleupdate = scheduleupdate;
	}

}
