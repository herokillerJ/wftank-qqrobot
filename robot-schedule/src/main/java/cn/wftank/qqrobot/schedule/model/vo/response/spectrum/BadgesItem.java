package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BadgesItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("url")
	private String url;

	public String getName(){
		return name;
	}

	public String getIcon(){
		return icon;
	}

	public String getUrl(){
		return url;
	}

	@Override
 	public String toString(){
		return 
			"BadgesItem{" + 
			"name = '" + name + '\'' + 
			",icon = '" + icon + '\'' + 
			",url = '" + url + '\'' + 
			"}";
		}
}