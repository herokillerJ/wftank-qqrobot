package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueCouncilResp{

	@JsonProperty("msg")
	private String msg;

	@JsonProperty("code")
	private String code;

	@JsonProperty("data")
	private Data data;

	@JsonProperty("success")
	private int success;

}