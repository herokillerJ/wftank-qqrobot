package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Module{

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("key")
	private String key;

	@JsonProperty("url")
	private String url;

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setKey(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	@Override
 	public String toString(){
		return 
			"Module{" + 
			"id = '" + id + '\'' + 
			",title = '" + title + '\'' + 
			",key = '" + key + '\'' + 
			",url = '" + url + '\'' + 
			"}";
		}
}