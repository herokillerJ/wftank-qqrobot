package cn.wftank.qqrobot.common.model.vo.translate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransResultItem{

	@JsonProperty("dst")
	private String dst;

	@JsonProperty("src")
	private String src;
}