package org.bs4lndt2.covid19.bo;

public class IndiaBO implements Comparable<IndiaBO> {

	private Integer statePriority;
	private String stateName;
	private Integer totalCases;
	private Integer totalCured;
	private Integer totalDeaths;
	private Integer totalActive;

	public Integer getStatePriority() {
		return statePriority;
	}

	public void setStatePriority(Integer statePriority) {
		this.statePriority = statePriority;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Integer getTotalCases() {
		return totalCases;
	}

	public void setTotalCases(Integer totalCases) {
		this.totalCases = totalCases;
	}

	public Integer getTotalCured() {
		return totalCured;
	}

	public void setTotalCured(Integer totalCured) {
		this.totalCured = totalCured;
	}

	public Integer getTotalDeaths() {
		return totalDeaths;
	}

	public void setTotalDeaths(Integer totalDeaths) {
		this.totalDeaths = totalDeaths;
	}

	public Integer getTotalActive() {
		return totalActive;
	}

	public void setTotalActive(Integer totalActive) {
		this.totalActive = totalActive;
	}

	public int compareTo(IndiaBO ibo) {
		if (getTotalCases() == null || ibo.getTotalCases() == null) {
			return 0;
		}
		return getTotalCases().compareTo(ibo.getTotalCases());
	}
}
