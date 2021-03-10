package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Account{

	@JsonProperty("firstname")
	private Object firstname;

	@JsonProperty("displayname")
	private String displayname;

	@JsonProperty("nickname")
	private String nickname;

	@JsonProperty("id")
	private String id;

	@JsonProperty("media")
	private List<Object> media;

	@JsonProperty("lastname")
	private Object lastname;

	public void setFirstname(Object firstname){
		this.firstname = firstname;
	}

	public Object getFirstname(){
		return firstname;
	}

	public void setDisplayname(String displayname){
		this.displayname = displayname;
	}

	public String getDisplayname(){
		return displayname;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
	}

	public String getNickname(){
		return nickname;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setMedia(List<Object> media){
		this.media = media;
	}

	public List<Object> getMedia(){
		return media;
	}

	public void setLastname(Object lastname){
		this.lastname = lastname;
	}

	public Object getLastname(){
		return lastname;
	}

	@Override
 	public String toString(){
		return 
			"Account{" + 
			"firstname = '" + firstname + '\'' + 
			",displayname = '" + displayname + '\'' + 
			",nickname = '" + nickname + '\'' + 
			",id = '" + id + '\'' + 
			",media = '" + media + '\'' + 
			",lastname = '" + lastname + '\'' + 
			"}";
		}
}