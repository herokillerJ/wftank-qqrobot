package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaItem{

	@JsonProperty("publish_start")
	private Object publishStart;

	@JsonProperty("images")
	private Images images;

	@JsonProperty("depot")
	private String depot;

	@JsonProperty("purpose")
	private String purpose;

	@JsonProperty("tag_string")
	private String tagString;

	@JsonProperty("publish_end")
	private Object publishEnd;

	@JsonProperty("distant_source")
	private Object distantSource;

	@JsonProperty("source_stream")
	private SourceStream sourceStream;

	@JsonProperty("type")
	private String type;

	@JsonProperty("source_url")
	private String sourceUrl;

	@JsonProperty("depot_status")
	private String depotStatus;

	@JsonProperty("source_extension")
	private String sourceExtension;

	@JsonProperty("membership.id")
	private String membershipId;

	@JsonProperty("time_modified")
	private String timeModified;

	@JsonProperty("source_duration")
	private Object sourceDuration;

	@JsonProperty("cover_data")
	private Object coverData;

	@JsonProperty("id")
	private String id;

	@JsonProperty("membership.slot")
	private String membershipSlot;

	@JsonProperty("slug")
	private String slug;

	@JsonProperty("source_name")
	private String sourceName;

	@JsonProperty("derived_data")
	private DerivedData derivedData;

	@JsonProperty("distant_id")
	private Object distantId;

	@JsonProperty("status")
	private String status;

	public void setPublishStart(Object publishStart){
		this.publishStart = publishStart;
	}

	public Object getPublishStart(){
		return publishStart;
	}

	public void setImages(Images images){
		this.images = images;
	}

	public Images getImages(){
		return images;
	}

	public void setDepot(String depot){
		this.depot = depot;
	}

	public String getDepot(){
		return depot;
	}

	public void setPurpose(String purpose){
		this.purpose = purpose;
	}

	public String getPurpose(){
		return purpose;
	}

	public void setTagString(String tagString){
		this.tagString = tagString;
	}

	public String getTagString(){
		return tagString;
	}

	public void setPublishEnd(Object publishEnd){
		this.publishEnd = publishEnd;
	}

	public Object getPublishEnd(){
		return publishEnd;
	}

	public void setDistantSource(Object distantSource){
		this.distantSource = distantSource;
	}

	public Object getDistantSource(){
		return distantSource;
	}

	public void setSourceStream(SourceStream sourceStream){
		this.sourceStream = sourceStream;
	}

	public SourceStream getSourceStream(){
		return sourceStream;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setSourceUrl(String sourceUrl){
		this.sourceUrl = sourceUrl;
	}

	public String getSourceUrl(){
		return sourceUrl;
	}

	public void setDepotStatus(String depotStatus){
		this.depotStatus = depotStatus;
	}

	public String getDepotStatus(){
		return depotStatus;
	}

	public void setSourceExtension(String sourceExtension){
		this.sourceExtension = sourceExtension;
	}

	public String getSourceExtension(){
		return sourceExtension;
	}

	public void setMembershipId(String membershipId){
		this.membershipId = membershipId;
	}

	public String getMembershipId(){
		return membershipId;
	}

	public void setTimeModified(String timeModified){
		this.timeModified = timeModified;
	}

	public String getTimeModified(){
		return timeModified;
	}

	public void setSourceDuration(Object sourceDuration){
		this.sourceDuration = sourceDuration;
	}

	public Object getSourceDuration(){
		return sourceDuration;
	}

	public void setCoverData(Object coverData){
		this.coverData = coverData;
	}

	public Object getCoverData(){
		return coverData;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setMembershipSlot(String membershipSlot){
		this.membershipSlot = membershipSlot;
	}

	public String getMembershipSlot(){
		return membershipSlot;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setSourceName(String sourceName){
		this.sourceName = sourceName;
	}

	public String getSourceName(){
		return sourceName;
	}

	public void setDerivedData(DerivedData derivedData){
		this.derivedData = derivedData;
	}

	public DerivedData getDerivedData(){
		return derivedData;
	}

	public void setDistantId(Object distantId){
		this.distantId = distantId;
	}

	public Object getDistantId(){
		return distantId;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"MediaItem{" + 
			"publish_start = '" + publishStart + '\'' + 
			",images = '" + images + '\'' + 
			",depot = '" + depot + '\'' + 
			",purpose = '" + purpose + '\'' + 
			",tag_string = '" + tagString + '\'' + 
			",publish_end = '" + publishEnd + '\'' + 
			",distant_source = '" + distantSource + '\'' + 
			",source_stream = '" + sourceStream + '\'' + 
			",type = '" + type + '\'' + 
			",source_url = '" + sourceUrl + '\'' + 
			",depot_status = '" + depotStatus + '\'' + 
			",source_extension = '" + sourceExtension + '\'' + 
			",membership.id = '" + membershipId + '\'' + 
			",time_modified = '" + timeModified + '\'' + 
			",source_duration = '" + sourceDuration + '\'' + 
			",cover_data = '" + coverData + '\'' + 
			",id = '" + id + '\'' + 
			",membership.slot = '" + membershipSlot + '\'' + 
			",slug = '" + slug + '\'' + 
			",source_name = '" + sourceName + '\'' + 
			",derived_data = '" + derivedData + '\'' + 
			",distant_id = '" + distantId + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}