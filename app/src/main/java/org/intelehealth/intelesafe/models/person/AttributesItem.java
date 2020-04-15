package org.intelehealth.intelesafe.models.person;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AttributesItem{

	@SerializedName("attributeType")
	private AttributeType attributeType;

	@SerializedName("display")
	private String display;

	@SerializedName("resourceVersion")
	private String resourceVersion;

	@SerializedName("voided")
	private boolean voided;

	@SerializedName("links")
	private List<LinksItem> links;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("value")
	private String value;

	public void setAttributeType(AttributeType attributeType){
		this.attributeType = attributeType;
	}

	public AttributeType getAttributeType(){
		return attributeType;
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

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"AttributesItem{" + 
			"attributeType = '" + attributeType + '\'' + 
			",display = '" + display + '\'' + 
			",resourceVersion = '" + resourceVersion + '\'' + 
			",voided = '" + voided + '\'' + 
			",links = '" + links + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",value = '" + value + '\'' + 
			"}";
		}
}