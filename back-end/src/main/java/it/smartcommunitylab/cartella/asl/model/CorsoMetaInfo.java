package it.smartcommunitylab.cartella.asl.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
//@Indexed
@Table(name = "corso_meta_info")
public class CorsoMetaInfo {

	@Id
	private String id;
	private String course;
	private String extId;
	private String origin;
	private String codMiur;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCodMiur() {
		return codMiur;
	}

	public void setCodMiur(String codMiur) {
		this.codMiur = codMiur;
	}

}
