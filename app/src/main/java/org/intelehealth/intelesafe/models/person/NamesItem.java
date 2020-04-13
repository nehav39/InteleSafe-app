package org.intelehealth.intelesafe.models.person;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NamesItem{

	@SerializedName("familyName2")
	private String familyName2;

	@SerializedName("display")
	private String display;

	@SerializedName("givenName")
	private String givenName;

	@SerializedName("familyName")
	private String familyName;

	@SerializedName("resourceVersion")
	private String resourceVersion;

	@SerializedName("middleName")
	private String middleName;

	@SerializedName("voided")
	private boolean voided;

	@SerializedName("links")
	private List<LinksItem> links;

	@SerializedName("uuid")
	private String uuid;

	public void setFamilyName2(String familyName2){
		this.familyName2 = familyName2;
	}

	public String getFamilyName2(){
		return familyName2;
	}

	public void setDisplay(String display){
		this.display = display;
	}

	public String getDisplay(){
		return display;
	}

	public void setGivenName(String givenName){
		this.givenName = givenName;
	}

	public String getGivenName(){
		return givenName;
	}

	public void setFamilyName(String familyName){
		this.familyName = familyName;
	}

	public String getFamilyName(){
		return familyName;
	}

	public void setResourceVersion(String resourceVersion){
		this.resourceVersion = resourceVersion;
	}

	public String getResourceVersion(){
		return resourceVersion;
	}

	public void setMiddleName(String middleName){
		this.middleName = middleName;
	}

	public String getMiddleName(){
		return middleName;
	}

	public void setVoided(boolean voided){
		this.voided = voided;
	}

	public boolean isVoided(){
		return voided;
	}

	public void setLinks(List<LinksItem> links){
		this.links = links;
	}

	public List<LinksItem> getLinks(){
		return links;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	@Override
 	public String toString(){
		return 
			"NamesItem{" + 
			"familyName2 = '" + familyName2 + '\'' + 
			",display = '" + display + '\'' + 
			",givenName = '" + givenName + '\'' + 
			",familyName = '" + familyName + '\'' + 
			",resourceVersion = '" + resourceVersion + '\'' + 
			",middleName = '" + middleName + '\'' + 
			",voided = '" + voided + '\'' + 
			",links = '" + links + '\'' + 
			",uuid = '" + uuid + '\'' + 
			"}";
		}
}