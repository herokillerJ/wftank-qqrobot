package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sizes{

	@JsonProperty("heap_thumb")
	private HeapThumb heapThumb;

	@JsonProperty("heap_infobox")
	private HeapInfobox heapInfobox;

	@JsonProperty("heap_note")
	private HeapNote heapNote;

	@JsonProperty("avatar")
	private Avatar avatar;

	public void setHeapThumb(HeapThumb heapThumb){
		this.heapThumb = heapThumb;
	}

	public HeapThumb getHeapThumb(){
		return heapThumb;
	}

	public void setHeapInfobox(HeapInfobox heapInfobox){
		this.heapInfobox = heapInfobox;
	}

	public HeapInfobox getHeapInfobox(){
		return heapInfobox;
	}

	public void setHeapNote(HeapNote heapNote){
		this.heapNote = heapNote;
	}

	public HeapNote getHeapNote(){
		return heapNote;
	}

	public void setAvatar(Avatar avatar){
		this.avatar = avatar;
	}

	public Avatar getAvatar(){
		return avatar;
	}

	@Override
 	public String toString(){
		return 
			"Sizes{" + 
			"heap_thumb = '" + heapThumb + '\'' + 
			",heap_infobox = '" + heapInfobox + '\'' + 
			",heap_note = '" + heapNote + '\'' + 
			",avatar = '" + avatar + '\'' + 
			"}";
		}
}