package cn.wftank.qqrobot.common.model.vo.translate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BaiduTranslateResp{

	@JsonProperty("trans_result")
	private List<TransResultItem> transResult;

	@JsonProperty("from")
	private String from;

	@JsonProperty("to")
	private String to;
}