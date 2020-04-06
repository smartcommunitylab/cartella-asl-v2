package it.smartcommunitylab.cartella.asl.model.users;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.smartcommunitylab.cartella.asl.model.Azienda;
import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.Studente;

@Entity
@Table(name = "asluser") //, uniqueConstraints= @UniqueConstraint(columnNames={"cf", "email"}))
public class ASLUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique=true)
	private String cf;
	@Column(unique=true)
	private String email;

	private String name;
	private String surname;
	
	private String username;
	
	@Transient
	private Set<ASLUserRole> roles = Sets.newHashSet();
	
	@Transient
	private Boolean authorized = Boolean.FALSE;
	
	@Transient
	private String token;
	@Transient
	private long expiration;	
	@Transient
	private String refreshToken;	
	
	@Transient
	private Map<String, Studente> studenti = Maps.newTreeMap();
	@Transient
	private Map<String, Azienda> aziende = Maps.newTreeMap();
	@Transient
	private Map<String, Istituzione> istituti = Maps.newTreeMap();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<ASLUserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<ASLUserRole> roles) {
		this.roles = roles;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Map<String, Studente> getStudenti() {
		return studenti;
	}

	public void setStudenti(Map<String, Studente> studenti) {
		this.studenti = studenti;
	}

	public Map<String, Azienda> getAziende() {
		return aziende;
	}

	public void setAziende(Map<String, Azienda> aziende) {
		this.aziende = aziende;
	}

	public Map<String, Istituzione> getIstituti() {
		return istituti;
	}

	public void setIstituti(Map<String, Istituzione> istituti) {
		this.istituti = istituti;
	}

	
	public Boolean getAuthorized() {
		return authorized;
	}

	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
