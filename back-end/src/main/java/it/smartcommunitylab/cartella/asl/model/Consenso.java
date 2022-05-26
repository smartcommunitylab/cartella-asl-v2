package it.smartcommunitylab.cartella.asl.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "consenso", indexes = { 
		@Index(name = "email_idx", columnList = "email", unique = false),
		@Index(name = "cf_idx", columnList = "cf", unique = false)})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Consenso {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String cf;
	private Date creationDate;

	private Boolean authorized = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getAuthorized() {
		return authorized;
	}

	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}

}
