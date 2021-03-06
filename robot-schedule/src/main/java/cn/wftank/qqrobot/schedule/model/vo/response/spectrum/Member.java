package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Member{

	@JsonProperty("isGM")
	private boolean isGM;

	@JsonProperty("spoken_languages")
	private List<String> spokenLanguages;

	@JsonProperty("signature")
	private String signature;

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("displayname")
	private String displayname;

	@JsonProperty("roles")
	private Roles roles;

	@JsonProperty("nickname")
	private String nickname;

	@JsonProperty("id")
	private String id;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("presence")
	private Presence presence;

	public boolean isIsGM(){
		return isGM;
	}

	public List<String> getSpokenLanguages(){
		return spokenLanguages;
	}

	public String getSignature(){
		return signature;
	}

	public Meta getMeta(){
		return meta;
	}

	public String getDisplayname(){
		return displayname;
	}

	public Roles getRoles(){
		return roles;
	}

	public String getNickname(){
		return nickname;
	}

	public String getId(){
		return id;
	}

	public String getAvatar(){
		return avatar;
	}

	public Presence getPresence(){
		return presence;
	}

	@Override
 	public String toString(){
		return 
			"Member{" + 
			"isGM = '" + isGM + '\'' + 
			",spoken_languages = '" + spokenLanguages + '\'' + 
			",signature = '" + signature + '\'' + 
			",meta = '" + meta + '\'' + 
			",displayname = '" + displayname + '\'' + 
			",roles = '" + roles + '\'' + 
			",nickname = '" + nickname + '\'' + 
			",id = '" + id + '\'' + 
			",avatar = '" + avatar + '\'' + 
			",presence = '" + presence + '\'' + 
			"}";
		}
}