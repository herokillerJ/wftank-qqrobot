package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Votes{

	@JsonProperty("count")
	private int count;

	@JsonProperty("voted")
	private int voted;

	public int getCount(){
		return count;
	}

	public int getVoted(){
		return voted;
	}

	@Override
 	public String toString(){
		return 
			"Votes{" + 
			"count = '" + count + '\'' + 
			",voted = '" + voted + '\'' + 
			"}";
		}
}