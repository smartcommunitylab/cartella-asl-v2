package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class MetaInfo {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;	
	
	private String name;
	private long epocTimestamp = -1;
	private int totalRead;
	private int totalStore;
	private int schoolYear = -1;
	private boolean blocked;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getEpocTimestamp() {
		return epocTimestamp;
	}

	public void setEpocTimestamp(long epocTimestamp) {
		this.epocTimestamp = epocTimestamp;
	}

	public int getTotalRead() {
		return totalRead;
	}

	public void setTotalRead(int totalRead) {
		this.totalRead = totalRead;
	}

	public int getTotalStore() {
		return totalStore;
	}

	public void setTotalStore(int totalStore) {
		this.totalStore = totalStore;
	}

	public int getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(int schoolYear) {
		this.schoolYear = schoolYear;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
