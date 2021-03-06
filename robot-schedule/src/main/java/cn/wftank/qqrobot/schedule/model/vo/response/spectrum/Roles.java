package cn.wftank.qqrobot.schedule.model.vo.response.spectrum;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Roles{

	@JsonProperty("1")
	private List<String> jsonMember1;

	@JsonProperty("9711")
	private List<String> jsonMember9711;

	@JsonProperty("40477")
	private List<Object> jsonMember40477;

	@JsonProperty("41405")
	private List<String> jsonMember41405;

	@JsonProperty("5")
	private List<String> jsonMember5;

	@JsonProperty("40528")
	private List<String> jsonMember40528;

	@JsonProperty("16441")
	private List<String> jsonMember16441;

	@JsonProperty("52678")
	private List<String> jsonMember52678;

	@JsonProperty("8")
	private List<String> jsonMember8;

	@JsonProperty("1570")
	private List<String> jsonMember1570;

	@JsonProperty("12931")
	private List<String> jsonMember12931;

	public List<String> getJsonMember1(){
		return jsonMember1;
	}

	public List<String> getJsonMember9711(){
		return jsonMember9711;
	}

	public List<Object> getJsonMember40477(){
		return jsonMember40477;
	}

	public List<String> getJsonMember41405(){
		return jsonMember41405;
	}

	public List<String> getJsonMember5(){
		return jsonMember5;
	}

	public List<String> getJsonMember40528(){
		return jsonMember40528;
	}

	public List<String> getJsonMember16441(){
		return jsonMember16441;
	}

	public List<String> getJsonMember52678(){
		return jsonMember52678;
	}

	public List<String> getJsonMember8(){
		return jsonMember8;
	}

	public List<String> getJsonMember1570(){
		return jsonMember1570;
	}

	public List<String> getJsonMember12931(){
		return jsonMember12931;
	}

	@Override
 	public String toString(){
		return 
			"Roles{" + 
			"1 = '" + jsonMember1 + '\'' + 
			",9711 = '" + jsonMember9711 + '\'' + 
			",40477 = '" + jsonMember40477 + '\'' + 
			",41405 = '" + jsonMember41405 + '\'' + 
			",5 = '" + jsonMember5 + '\'' + 
			",40528 = '" + jsonMember40528 + '\'' + 
			",16441 = '" + jsonMember16441 + '\'' + 
			",52678 = '" + jsonMember52678 + '\'' + 
			",8 = '" + jsonMember8 + '\'' + 
			",1570 = '" + jsonMember1570 + '\'' + 
			",12931 = '" + jsonMember12931 + '\'' + 
			"}";
		}
}