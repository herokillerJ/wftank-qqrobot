package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Thumbnail{

	@JsonProperty("url")
	private String url;

	public String getUrl(){
		return url;
	}

	@Override
 	public String toString(){
		return 
			"Thumbnail{" + 
			"url = '" + url + '\'' + 
			"}";
		}
}