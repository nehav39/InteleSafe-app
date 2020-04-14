package org.intelehealth.intelesafe.models.user;

import com.google.gson.annotations.SerializedName;

public class LinksItem{

	@SerializedName("rel")
	private String rel;

	@SerializedName("uri")
	private String uri;

	public void setRel(String rel){
		this.rel = rel;
	}

	public String getRel(){
		return rel;
	}

	public void setUri(String uri){
		this.uri = uri;
	}

	public String getUri(){
		return uri;
	}

	@Override
 	public String toString(){
		return 
			"LinksItem{" + 
			"rel = '" + rel + '\'' + 
			",uri = '" + uri + '\'' + 
			"}";
		}
}