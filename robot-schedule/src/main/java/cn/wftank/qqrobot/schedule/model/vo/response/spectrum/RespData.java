package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import java.time.LocalDateTime;
import java.util.List;

import cn.wftank.qqrobot.common.deserialize.CustomLocalDateTimeDesSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class RespData<T>{

	@JsonProperty("threads_count")
	private int threadsCount;

	@JsonProperty("latest_timestamp")
	@JsonDeserialize(using = CustomLocalDateTimeDesSerializer.class)
	private LocalDateTime latestTimestamp;

	@JsonProperty("threads")
	private List<T> threads;

	@JsonProperty("latest")
	private int latest;

	public int getThreadsCount(){
		return threadsCount;
	}

	public void setThreadsCount(int threadsCount) {
		this.threadsCount = threadsCount;
	}

	public LocalDateTime getLatestTimestamp() {
		return latestTimestamp;
	}

	public void setLatestTimestamp(LocalDateTime latestTimestamp) {
		this.latestTimestamp = latestTimestamp;
	}

	public List<T> getThreads() {
		return threads;
	}

	public void setThreads(List<T> threads) {
		this.threads = threads;
	}

	public void setLatest(int latest) {
		this.latest = latest;
	}

	public int getLatest(){
		return latest;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"threads_count = '" + threadsCount + '\'' + 
			",latest_timestamp = '" + latestTimestamp + '\'' + 
			",threads = '" + threads + '\'' + 
			",latest = '" + latest + '\'' + 
			"}";
		}
}