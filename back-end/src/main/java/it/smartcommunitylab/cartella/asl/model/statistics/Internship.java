package it.smartcommunitylab.cartella.asl.model.statistics;

import java.util.List;

import com.google.common.collect.Lists;

public class Internship {
	private String id;

	private String type;

	private String name;

	private String description;

	private String companyId;

	private long fromDate;

	private int remainingPlaces;

	private long toDate;

	private int totalPlaces;

	private List<Instance> instances = Lists.newArrayList();

	public String getCompanyId() {
		return this.companyId;
	}

	public String getDescription() {
		return this.description;
	}

	public long getFromDate() {
		return this.fromDate;
	}

	public String getId() {
		return this.id;
	}

	public List<Instance> getInstances() {
		return this.instances;
	}

	public String getName() {
		return this.name;
	}

	public int getRemainingPlaces() {
		return this.remainingPlaces;
	}

	public long getToDate() {
		return this.toDate;
	}

	public int getTotalPlaces() {
		return this.totalPlaces;
	}

	public String getType() {
		return this.type;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFromDate(long fromDate) {
		this.fromDate = fromDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemainingPlaces(int remainingPlaces) {
		this.remainingPlaces = remainingPlaces;
	}

	public void setToDate(long toDate) {
		this.toDate = toDate;
	}

	public void setTotalPlaces(int totalPlaces) {
		this.totalPlaces = totalPlaces;
	}

	public void setType(String type) {
		this.type = type;
	}
}