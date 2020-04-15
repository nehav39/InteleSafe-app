package org.intelehealth.intelesafe.models.person;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ChangedBy{

	@SerializedName("display")
	private String display;

	@SerializedName("links")
	private List<LinksItem> links;

	@SerializedName("uuid")
	private String uuid;

	public void setDisplay(String display){
		this.display = display;
	}

	public String getDisplay(){
		return display;
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
			"ChangedBy{" + 
			"display = '" + display + '\'' + 
			",links = '" + links + '\'' + 
			",uuid = '" + uuid + '\'' + 
			"}";
		}
}