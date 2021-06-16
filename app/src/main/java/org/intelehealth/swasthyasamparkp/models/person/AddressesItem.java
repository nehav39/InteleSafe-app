package org.intelehealth.swasthyasamparkp.models.person;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AddressesItem{

	@SerializedName("country")
	private String country;

	@SerializedName("countyDistrict")
	private String countyDistrict;

	@SerializedName("endDate")
	private String endDate;

	@SerializedName("postalCode")
	private String postalCode;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("address7")
	private String address7;

	@SerializedName("address6")
	private String address6;

	@SerializedName("address5")
	private String address5;

	@SerializedName("voided")
	private boolean voided;

	@SerializedName("links")
	private List<LinksItem> links;

	@SerializedName("address4")
	private String address4;

	@SerializedName("preferred")
	private boolean preferred;

	@SerializedName("address9")
	private String address9;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("address8")
	private String address8;

	@SerializedName("address3")
	private String address3;

	@SerializedName("address2")
	private String address2;

	@SerializedName("address1")
	private String address1;

	@SerializedName("display")
	private String display;

	@SerializedName("resourceVersion")
	private String resourceVersion;

	@SerializedName("stateProvince")
	private String stateProvince;

	@SerializedName("cityVillage")
	private String cityVillage;

	@SerializedName("address10")
	private String address10;

	@SerializedName("address11")
	private String address11;

	@SerializedName("address12")
	private String address12;

	@SerializedName("address13")
	private String address13;

	@SerializedName("address14")
	private String address14;

	@SerializedName("address15")
	private String address15;

	@SerializedName("startDate")
	private String startDate;

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setCountyDistrict(String countyDistrict){
		this.countyDistrict = countyDistrict;
	}

	public String getCountyDistrict(){
		return countyDistrict;
	}

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
	}

	public void setPostalCode(String postalCode){
		this.postalCode = postalCode;
	}

	public String getPostalCode(){
		return postalCode;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setAddress7(String address7){
		this.address7 = address7;
	}

	public String getAddress7(){
		return address7;
	}

	public void setAddress6(String address6){
		this.address6 = address6;
	}

	public String getAddress6(){
		return address6;
	}

	public void setAddress5(String address5){
		this.address5 = address5;
	}

	public String getAddress5(){
		return address5;
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

	public void setAddress4(String address4){
		this.address4 = address4;
	}

	public String getAddress4(){
		return address4;
	}

	public void setPreferred(boolean preferred){
		this.preferred = preferred;
	}

	public boolean isPreferred(){
		return preferred;
	}

	public void setAddress9(String address9){
		this.address9 = address9;
	}

	public String getAddress9(){
		return address9;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	public void setAddress8(String address8){
		this.address8 = address8;
	}

	public String getAddress8(){
		return address8;
	}

	public void setAddress3(String address3){
		this.address3 = address3;
	}

	public String getAddress3(){
		return address3;
	}

	public void setAddress2(String address2){
		this.address2 = address2;
	}

	public String getAddress2(){
		return address2;
	}

	public void setAddress1(String address1){
		this.address1 = address1;
	}

	public String getAddress1(){
		return address1;
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

	public void setStateProvince(String stateProvince){
		this.stateProvince = stateProvince;
	}

	public String getStateProvince(){
		return stateProvince;
	}

	public void setCityVillage(String cityVillage){
		this.cityVillage = cityVillage;
	}

	public String getCityVillage(){
		return cityVillage;
	}

	public void setAddress10(String address10){
		this.address10 = address10;
	}

	public String getAddress10(){
		return address10;
	}

	public void setAddress11(String address11){
		this.address11 = address11;
	}

	public String getAddress11(){
		return address11;
	}

	public void setAddress12(String address12){
		this.address12 = address12;
	}

	public String getAddress12(){
		return address12;
	}

	public void setAddress13(String address13){
		this.address13 = address13;
	}

	public String getAddress13(){
		return address13;
	}

	public void setAddress14(String address14){
		this.address14 = address14;
	}

	public String getAddress14(){
		return address14;
	}

	public void setAddress15(String address15){
		this.address15 = address15;
	}

	public String getAddress15(){
		return address15;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	@Override
 	public String toString(){
		return 
			"AddressesItem{" + 
			"country = '" + country + '\'' + 
			",countyDistrict = '" + countyDistrict + '\'' + 
			",endDate = '" + endDate + '\'' + 
			",postalCode = '" + postalCode + '\'' + 
			",latitude = '" + latitude + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",address7 = '" + address7 + '\'' + 
			",address6 = '" + address6 + '\'' + 
			",address5 = '" + address5 + '\'' + 
			",voided = '" + voided + '\'' + 
			",links = '" + links + '\'' + 
			",address4 = '" + address4 + '\'' + 
			",preferred = '" + preferred + '\'' + 
			",address9 = '" + address9 + '\'' + 
			",longitude = '" + longitude + '\'' + 
			",address8 = '" + address8 + '\'' + 
			",address3 = '" + address3 + '\'' + 
			",address2 = '" + address2 + '\'' + 
			",address1 = '" + address1 + '\'' + 
			",display = '" + display + '\'' + 
			",resourceVersion = '" + resourceVersion + '\'' + 
			",stateProvince = '" + stateProvince + '\'' + 
			",cityVillage = '" + cityVillage + '\'' + 
			",address10 = '" + address10 + '\'' + 
			",address11 = '" + address11 + '\'' + 
			",address12 = '" + address12 + '\'' + 
			",address13 = '" + address13 + '\'' + 
			",address14 = '" + address14 + '\'' + 
			",address15 = '" + address15 + '\'' + 
			",startDate = '" + startDate + '\'' + 
			"}";
		}
}