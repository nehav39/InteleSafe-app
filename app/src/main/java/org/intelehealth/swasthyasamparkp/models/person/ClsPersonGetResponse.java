package org.intelehealth.swasthyasamparkp.models.person;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ClsPersonGetResponse{

	@SerializedName("addresses")
	private List<AddressesItem> addresses;

	@SerializedName("birthdate")
	private String birthdate;

	@SerializedName("gender")
	private String gender;

	@SerializedName("display")
	private String display;

	@SerializedName("resourceVersion")
	private String resourceVersion;

	@SerializedName("dead")
	private boolean dead;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("preferredAddress")
	private PreferredAddress preferredAddress;

	@SerializedName("auditInfo")
	private AuditInfo auditInfo;

	@SerializedName("birthdateEstimated")
	private boolean birthdateEstimated;

	@SerializedName("deathdateEstimated")
	private boolean deathdateEstimated;

	@SerializedName("names")
	private List<NamesItem> names;

	@SerializedName("deathDate")
	private String deathDate;

	@SerializedName("attributes")
	private List<AttributesItem> attributes;

	@SerializedName("voided")
	private boolean voided;

	@SerializedName("birthtime")
	private String birthtime;

	@SerializedName("links")
	private List<LinksItem> links;

	@SerializedName("preferredName")
	private PreferredName preferredName;

	@SerializedName("causeOfDeath")
	private String causeOfDeath;

	@SerializedName("age")
	private int age;

	public void setAddresses(List<AddressesItem> addresses){
		this.addresses = addresses;
	}

	public List<AddressesItem> getAddresses(){
		return addresses;
	}

	public void setBirthdate(String birthdate){
		this.birthdate = birthdate;
	}

	public String getBirthdate(){
		return birthdate;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setDisplay(String display){
		this.display = display;
	}

	public String getDisplay(){
		return display;
	}

	public void setResourceVersion(String resourceVersion){
		this.resourceVersion = resourceVersion;
	}

	public String getResourceVersion(){
		return resourceVersion;
	}

	public void setDead(boolean dead){
		this.dead = dead;
	}

	public boolean isDead(){
		return dead;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setPreferredAddress(PreferredAddress preferredAddress){
		this.preferredAddress = preferredAddress;
	}

	public PreferredAddress getPreferredAddress(){
		return preferredAddress;
	}

	public void setAuditInfo(AuditInfo auditInfo){
		this.auditInfo = auditInfo;
	}

	public AuditInfo getAuditInfo(){
		return auditInfo;
	}

	public void setBirthdateEstimated(boolean birthdateEstimated){
		this.birthdateEstimated = birthdateEstimated;
	}

	public boolean isBirthdateEstimated(){
		return birthdateEstimated;
	}

	public void setDeathdateEstimated(boolean deathdateEstimated){
		this.deathdateEstimated = deathdateEstimated;
	}

	public boolean isDeathdateEstimated(){
		return deathdateEstimated;
	}

	public void setNames(List<NamesItem> names){
		this.names = names;
	}

	public List<NamesItem> getNames(){
		return names;
	}

	public void setDeathDate(String deathDate){
		this.deathDate = deathDate;
	}

	public String getDeathDate(){
		return deathDate;
	}

	public void setAttributes(List<AttributesItem> attributes){
		this.attributes = attributes;
	}

	public List<AttributesItem> getAttributes(){
		return attributes;
	}

	public void setVoided(boolean voided){
		this.voided = voided;
	}

	public boolean isVoided(){
		return voided;
	}

	public void setBirthtime(String birthtime){
		this.birthtime = birthtime;
	}

	public String getBirthtime(){
		return birthtime;
	}

	public void setLinks(List<LinksItem> links){
		this.links = links;
	}

	public List<LinksItem> getLinks(){
		return links;
	}

	public void setPreferredName(PreferredName preferredName){
		this.preferredName = preferredName;
	}

	public PreferredName getPreferredName(){
		return preferredName;
	}

	public void setCauseOfDeath(String causeOfDeath){
		this.causeOfDeath = causeOfDeath;
	}

	public String getCauseOfDeath(){
		return causeOfDeath;
	}

	public void setAge(int age){
		this.age = age;
	}

	public int getAge(){
		return age;
	}

	@Override
 	public String toString(){
		return 
			"ClsPersonGetResponse{" + 
			"addresses = '" + addresses + '\'' + 
			",birthdate = '" + birthdate + '\'' + 
			",gender = '" + gender + '\'' + 
			",display = '" + display + '\'' + 
			",resourceVersion = '" + resourceVersion + '\'' + 
			",dead = '" + dead + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",preferredAddress = '" + preferredAddress + '\'' + 
			",auditInfo = '" + auditInfo + '\'' + 
			",birthdateEstimated = '" + birthdateEstimated + '\'' + 
			",deathdateEstimated = '" + deathdateEstimated + '\'' + 
			",names = '" + names + '\'' + 
			",deathDate = '" + deathDate + '\'' + 
			",attributes = '" + attributes + '\'' + 
			",voided = '" + voided + '\'' + 
			",birthtime = '" + birthtime + '\'' + 
			",links = '" + links + '\'' + 
			",preferredName = '" + preferredName + '\'' + 
			",causeOfDeath = '" + causeOfDeath + '\'' + 
			",age = '" + age + '\'' + 
			"}";
		}
}