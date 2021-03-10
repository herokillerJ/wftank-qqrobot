package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Data{

	@JsonProperty("estimatedrows")
	private boolean estimatedrows;

	@JsonProperty("rowcount")
	private int rowcount;

	@JsonProperty("offset")
	private int offset;

	@JsonProperty("pagecount")
	private int pagecount;

	@JsonProperty("startrow")
	private int startrow;

	@JsonProperty("totalrows")
	private int totalrows;

	@JsonProperty("pagesize")
	private int pagesize;

	@JsonProperty("resultset")
	private List<ResultsetItem> resultset;

	@JsonProperty("page")
	private int page;

	public void setEstimatedrows(boolean estimatedrows){
		this.estimatedrows = estimatedrows;
	}

	public boolean isEstimatedrows(){
		return estimatedrows;
	}

	public void setRowcount(int rowcount){
		this.rowcount = rowcount;
	}

	public int getRowcount(){
		return rowcount;
	}

	public void setOffset(int offset){
		this.offset = offset;
	}

	public int getOffset(){
		return offset;
	}

	public void setPagecount(int pagecount){
		this.pagecount = pagecount;
	}

	public int getPagecount(){
		return pagecount;
	}

	public void setStartrow(int startrow){
		this.startrow = startrow;
	}

	public int getStartrow(){
		return startrow;
	}

	public void setTotalrows(int totalrows){
		this.totalrows = totalrows;
	}

	public int getTotalrows(){
		return totalrows;
	}

	public void setPagesize(int pagesize){
		this.pagesize = pagesize;
	}

	public int getPagesize(){
		return pagesize;
	}

	public void setResultset(List<ResultsetItem> resultset){
		this.resultset = resultset;
	}

	public List<ResultsetItem> getResultset(){
		return resultset;
	}

	public void setPage(int page){
		this.page = page;
	}

	public int getPage(){
		return page;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"estimatedrows = '" + estimatedrows + '\'' + 
			",rowcount = '" + rowcount + '\'' + 
			",offset = '" + offset + '\'' + 
			",pagecount = '" + pagecount + '\'' + 
			",startrow = '" + startrow + '\'' + 
			",totalrows = '" + totalrows + '\'' + 
			",pagesize = '" + pagesize + '\'' + 
			",resultset = '" + resultset + '\'' + 
			",page = '" + page + '\'' + 
			"}";
		}
}