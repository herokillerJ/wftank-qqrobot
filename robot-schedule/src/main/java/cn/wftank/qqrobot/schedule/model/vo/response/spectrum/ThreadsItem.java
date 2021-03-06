package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import cn.wftank.qqrobot.common.deserialize.CustomLocalDateTimeDesSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

public class ThreadsItem{

	@JsonProperty("tracked_post_role_id")
	private String trackedPostRoleId;

	@JsonProperty("is_locked")
	private boolean isLocked;

	@JsonProperty("highlight_role_id")
	private String highlightRoleId;

	@JsonProperty("latest_activity")
	private LatestActivity latestActivity;

	@JsonProperty("is_erased")
	private boolean isErased;

	@JsonProperty("erased_by")
	private Object erasedBy;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("is_sinked")
	private boolean isSinked;

	@JsonProperty("replies_count")
	private int repliesCount;

	@JsonProperty("label")
	private Object label;

	@JsonProperty("type")
	private String type;

	@JsonProperty("is_pinned")
	private boolean isPinned;

	@JsonProperty("time_modified")
	@JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
	private LocalDateTime timeModified;

	@JsonProperty("content_reply_id")
	private String contentReplyId;

	@JsonProperty("aspect")
	private String aspect;

	@JsonProperty("member")
	private Member member;

	@JsonProperty("time_created")
	@JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
	private LocalDateTime timeCreated;

	@JsonProperty("subscription_key")
	private String subscriptionKey;

	@JsonProperty("votes")
	private Votes votes;

	@JsonProperty("id")
	private String id;

	@JsonProperty("views_count")
	private int viewsCount;

	@JsonProperty("channel_id")
	private String channelId;

	@JsonProperty("slug")
	private String slug;

	@JsonProperty("first_tracked_reply")
	private String firstTrackedReply;

	@JsonProperty("media_preview")
	private MediaPreview mediaPreview;

	public String getTrackedPostRoleId(){
		return trackedPostRoleId;
	}

	public boolean isIsLocked(){
		return isLocked;
	}

	public String getHighlightRoleId(){
		return highlightRoleId;
	}

	public LatestActivity getLatestActivity(){
		return latestActivity;
	}

	public boolean isIsErased(){
		return isErased;
	}

	public Object getErasedBy(){
		return erasedBy;
	}

	public String getSubject(){
		return subject;
	}

	public boolean isIsSinked(){
		return isSinked;
	}

	public int getRepliesCount(){
		return repliesCount;
	}

	public Object getLabel(){
		return label;
	}

	public String getType(){
		return type;
	}

	public boolean isIsPinned(){
		return isPinned;
	}

	public LocalDateTime getTimeModified() {
		return timeModified;
	}

	public String getContentReplyId(){
		return contentReplyId;
	}

	public String getAspect(){
		return aspect;
	}

	public Member getMember(){
		return member;
	}

	public LocalDateTime getTimeCreated() {
		return timeCreated;
	}

	public String getSubscriptionKey(){
		return subscriptionKey;
	}

	public Votes getVotes(){
		return votes;
	}

	public String getId(){
		return id;
	}

	public int getViewsCount(){
		return viewsCount;
	}

	public String getChannelId(){
		return channelId;
	}

	public String getSlug(){
		return slug;
	}

	public String getFirstTrackedReply(){
		return firstTrackedReply;
	}

	public MediaPreview getMediaPreview(){
		return mediaPreview;
	}

	@Override
 	public String toString(){
		return 
			"ThreadsItem{" + 
			"tracked_post_role_id = '" + trackedPostRoleId + '\'' + 
			",is_locked = '" + isLocked + '\'' + 
			",highlight_role_id = '" + highlightRoleId + '\'' + 
			",latest_activity = '" + latestActivity + '\'' + 
			",is_erased = '" + isErased + '\'' + 
			",erased_by = '" + erasedBy + '\'' + 
			",subject = '" + subject + '\'' + 
			",is_sinked = '" + isSinked + '\'' + 
			",replies_count = '" + repliesCount + '\'' + 
			",label = '" + label + '\'' + 
			",type = '" + type + '\'' + 
			",is_pinned = '" + isPinned + '\'' + 
			",time_modified = '" + timeModified + '\'' + 
			",content_reply_id = '" + contentReplyId + '\'' + 
			",aspect = '" + aspect + '\'' + 
			",member = '" + member + '\'' + 
			",time_created = '" + timeCreated + '\'' + 
			",subscription_key = '" + subscriptionKey + '\'' + 
			",votes = '" + votes + '\'' + 
			",id = '" + id + '\'' + 
			",views_count = '" + viewsCount + '\'' + 
			",channel_id = '" + channelId + '\'' + 
			",slug = '" + slug + '\'' + 
			",first_tracked_reply = '" + firstTrackedReply + '\'' + 
			",media_preview = '" + mediaPreview + '\'' + 
			"}";
		}
}