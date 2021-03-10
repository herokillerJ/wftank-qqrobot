package cn.wftank.qqrobot.schedule.model.vo.request.issue;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IssueCouncilReq {
	private int pagesize;
	private int page;
	private String sort;
	private String moduleUrl;
}
