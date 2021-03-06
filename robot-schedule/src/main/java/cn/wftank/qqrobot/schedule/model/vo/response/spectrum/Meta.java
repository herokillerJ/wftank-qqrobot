package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

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