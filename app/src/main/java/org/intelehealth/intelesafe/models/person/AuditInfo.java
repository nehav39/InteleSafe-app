package org.intelehealth.intelesafe.models.person;

import com.google.gson.annotations.SerializedName;

public class AuditInfo{

	@SerializedName("dateChanged")
	private String dateChanged;

	@SerializedName("creator")
	private Creator creator;

	@SerializedName("dateCreated")
	private String dateCreated;

	@SerializedName("changedBy")
	private ChangedBy changedBy;

	public void setDateChanged(String dateChanged){
		this.dateChanged = dateChanged;
	}

	public String getDateChanged(){
		return dateChanged;
	}

	public void setCreator(Creator creator){
		this.creator = creator;
	}

	public Creator getCreator(){
		return creator;
	}

	public void setDateCreated(String dateCreated){
		this.dateCreated = dateCreated;
	}

	public String getDateCreated(){
		return dateCreated;
	}

	public void setChangedBy(ChangedBy changedBy){
		this.changedBy = changedBy;
	}

	public ChangedBy getChangedBy(){
		return changedBy;
	}

	@Override
 	public String toString(){
		return 
			"AuditInfo{" + 
			"dateChanged = '" + dateChanged + '\'' + 
			",creator = '" + creator + '\'' + 
			",dateCreated = '" + dateCreated + '\'' + 
			",changedBy = '" + changedBy + '\'' + 
			"}";
		}
}