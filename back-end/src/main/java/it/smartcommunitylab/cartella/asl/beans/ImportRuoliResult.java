package it.smartcommunitylab.cartella.asl.beans;

import java.util.ArrayList;
import java.util.List;

import it.smartcommunitylab.cartella.asl.model.users.ASLUser;

public class ImportRuoliResult {
	private List<ASLUser> users = new ArrayList<>();
	private List<String> alreadyPresent = new ArrayList<>();
	private List<String> notFound = new ArrayList<>();
	private List<String> errors = new ArrayList<>();
		
	public List<ASLUser> getUsers() {
		return users;
	}
	public void setUsers(List<ASLUser> users) {
		this.users = users;
	}
	public List<String> getNotFound() {
		return notFound;
	}
	public void setNotFound(List<String> notFound) {
		this.notFound = notFound;
	}
	public List<String> getAlreadyPresent() {
		return alreadyPresent;
	}
	public void setAlreadyPresent(List<String> alreadyPresent) {
		this.alreadyPresent = alreadyPresent;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}
