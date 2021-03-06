package cn.edu.buaa.g305.qpm.risk.controller;

public class RiskTypeVO {
	
	private String name;
	private String organization;
	
	private String description;
	private String exsample;
	//风险缓解措施
	private String mitigationMeasure;
	//风险应急措施
	private String emergencyMeasure;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExsample() {
		return exsample;
	}
	public void setExsample(String exsample) {
		this.exsample = exsample;
	}
	public String getMitigationMeasure() {
		return mitigationMeasure;
	}
	public void setMitigationMeasure(String mitigationMeasure) {
		this.mitigationMeasure = mitigationMeasure;
	}
	public String getEmergencyMeasure() {
		return emergencyMeasure;
	}
	public void setEmergencyMeasure(String emergencyMeasure) {
		this.emergencyMeasure = emergencyMeasure;
	}
	
}
