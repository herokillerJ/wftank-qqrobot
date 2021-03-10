package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceStream{

	@JsonProperty("progressive")
	private String progressive;

	public void setProgressive(String progressive){
		this.progressive = progressive;
	}

	public String getProgressive(){
		return progressive;
	}

	@Override
 	public String toString(){
		return 
			"SourceStream{" + 
			"progressive = '" + progressive + '\'' + 
			"}";
		}
}