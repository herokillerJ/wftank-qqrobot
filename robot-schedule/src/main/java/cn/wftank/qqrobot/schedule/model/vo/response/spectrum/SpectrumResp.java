package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpectrumResp<T> {

	@JsonProperty("msg")
	private String msg;

	@JsonProperty("code")
	private String code;

	@JsonProperty("data")
	private RespData<T> respData;

	@JsonProperty("success")
	private int success;

	public String getMsg(){
		return msg;
	}

	public String getCode(){
		return code;
	}

	public RespData<T> getData(){
		return respData;
	}

	public int getSuccess(){
		return success;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"msg = '" + msg + '\'' + 
			",code = '" + code + '\'' + 
			",data = '" + respData + '\'' +
			",success = '" + success + '\'' + 
			"}";
		}
}