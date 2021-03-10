package cn.wftank.qqrobot.schedule.model.vo.response.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reporter{

	@JsonProperty("account_id")
	private String accountId;

	@JsonProperty("role")
	private Object role;

	@JsonProperty("id")
	private String id;

	@JsonProperty("account")
	private Account account;

	public void setAccountId(String accountId){
		this.accountId = accountId;
	}

	public String getAccountId(){
		return accountId;
	}

	public void setRole(Object role){
		this.role = role;
	}

	public Object getRole(){
		return role;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setAccount(Account account){
		this.account = account;
	}

	public Account getAccount(){
		return account;
	}

	@Override
 	public String toString(){
		return 
			"Reporter{" + 
			"account_id = '" + accountId + '\'' + 
			",role = '" + role + '\'' + 
			",id = '" + id + '\'' + 
			",account = '" + account + '\'' + 
			"}";
		}
}