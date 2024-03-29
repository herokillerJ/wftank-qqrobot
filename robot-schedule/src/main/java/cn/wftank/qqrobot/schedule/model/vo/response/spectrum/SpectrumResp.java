package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SpectrumResp<T> {

	@JsonProperty("msg")
	private String msg;

	@JsonProperty("code")
	private String code;

	@JsonProperty("data")
	private RespData<T> respData;

	@JsonProperty("success")
	private int success;

}