package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HeapInfobox{

	@JsonProperty("mode")
	private String mode;

	@JsonProperty("width")
	private int width;

	@JsonProperty("delete")
	private String delete;

	@JsonProperty("height")
	private int height;

	public void setMode(String mode){
		this.mode = mode;
	}

	public String getMode(){
		return mode;
	}

	public void setWidth(int width){
		this.width = width;
	}

	public int getWidth(){
		return width;
	}

	public void setDelete(String delete){
		this.delete = delete;
	}

	public String getDelete(){
		return delete;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	@Override
 	public String toString(){
		return 
			"HeapInfobox{" + 
			"mode = '" + mode + '\'' + 
			",width = '" + width + '\'' + 
			",delete = '" + delete + '\'' + 
			",height = '" + height + '\'' + 
			"}";
		}
}