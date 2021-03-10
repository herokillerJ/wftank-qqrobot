package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ResultsetItem{

	@JsonProperty("contribution_count")
	private int contributionCount;

	@JsonProperty("expires")
	private String expires;

	@JsonProperty("module")
	private Module module;

	@JsonProperty("reproduced_goal_complete")
	private boolean reproducedGoalComplete;

	@JsonProperty("reporter")
	private Reporter reporter;

	@JsonProperty("label")
	private Label label;

	@JsonProperty("title")
	private String title;

	@JsonProperty("time_modified")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timeModified;

	@JsonProperty("bookmark")
	private Object bookmark;

	@JsonProperty("module_id")
	private String moduleId;

	@JsonProperty("can_reproduce_count")
	private int canReproduceCount;

	@JsonProperty("reporter_id")
	private String reporterId;

	@JsonProperty("invalid_count")
	private String invalidCount;

	@JsonProperty("time_created")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timeCreated;

	@JsonProperty("id")
	private String id;

	@JsonProperty("vote_count")
	private int voteCount;

	@JsonProperty("vote")
	private Object vote;

	@JsonProperty("label_id")
	private String labelId;

	@JsonProperty("status")
	private String status;

}