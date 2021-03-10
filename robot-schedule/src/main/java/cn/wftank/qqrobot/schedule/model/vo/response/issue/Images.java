package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Images{

	@JsonProperty("heap_thumb")
	private String heapThumb;

	@JsonProperty("heap_infobox")
	private String heapInfobox;

	@JsonProperty("heap_note")
	private String heapNote;

	@JsonProperty("avatar")
	private String avatar;

	public void setHeapThumb(String heapThumb){
		this.heapThumb = heapThumb;
	}

	public String getHeapThumb(){
		return heapThumb;
	}

	public void setHeapInfobox(String heapInfobox){
		this.heapInfobox = heapInfobox;
	}

	public String getHeapInfobox(){
		return heapInfobox;
	}

	public void setHeapNote(String heapNote){
		this.heapNote = heapNote;
	}

	public String getHeapNote(){
		return heapNote;
	}

	public void setAvatar(String avatar){
		this.avatar = avatar;
	}

	public String getAvatar(){
		return avatar;
	}

	@Override
 	public String toString(){
		return 
			"Images{" + 
			"heap_thumb = '" + heapThumb + '\'' + 
			",heap_infobox = '" + heapInfobox + '\'' + 
			",heap_note = '" + heapNote + '\'' + 
			",avatar = '" + avatar + '\'' + 
			"}";
		}
}