package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Meta{

	@JsonProperty("badges")
	private List<BadgesItem> badges;

	public List<BadgesItem> getBadges(){
		return badges;
	}

	@Override
 	public String toString(){
		return 
			"Meta{" + 
			"badges = '" + badges + '\'' + 
			"}";
		}
}