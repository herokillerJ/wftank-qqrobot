package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaPreview{

	@JsonProperty("thumbnail")
	private Thumbnail thumbnail;

	@JsonProperty("type")
	private String type;

	public Thumbnail getThumbnail(){
		return thumbnail;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"MediaPreview{" + 
			"thumbnail = '" + thumbnail + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}