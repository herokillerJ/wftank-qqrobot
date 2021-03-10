package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DerivedData{

	@JsonProperty("sizes")
	private Sizes sizes;

	public void setSizes(Sizes sizes){
		this.sizes = sizes;
	}

	public Sizes getSizes(){
		return sizes;
	}

	@Override
 	public String toString(){
		return 
			"DerivedData{" + 
			"sizes = '" + sizes + '\'' + 
			"}";
		}
}