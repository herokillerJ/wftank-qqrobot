package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Presence{

	@JsonProperty("status")
	private String status;

	@JsonProperty("info")
	private Object info;

	@JsonProperty("since")
	private int since;

	public String getStatus(){
		return status;
	}

	public Object getInfo(){
		return info;
	}

	public int getSince(){
		return since;
	}

	@Override
 	public String toString(){
		return 
			"Presence{" + 
			"status = '" + status + '\'' + 
			",info = '" + info + '\'' + 
			",since = '" + since + '\'' + 
			"}";
		}
}