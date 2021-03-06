package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatestActivity{

	@JsonProperty("highlight_role_id")
	private Object highlightRoleId;

	@JsonProperty("member")
	private Member member;

	@JsonProperty("time_created")
	private int timeCreated;

	public Object getHighlightRoleId(){
		return highlightRoleId;
	}

	public Member getMember(){
		return member;
	}

	public int getTimeCreated(){
		return timeCreated;
	}

	@Override
 	public String toString(){
		return 
			"LatestActivity{" + 
			"highlight_role_id = '" + highlightRoleId + '\'' + 
			",member = '" + member + '\'' + 
			",time_created = '" + timeCreated + '\'' + 
			"}";
		}
}