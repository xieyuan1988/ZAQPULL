package com.zaq.core.vo;

import com.google.gson.annotations.Expose;


/**
 * socket登陆信息
 * @author zyj
 *
 */
public class Login {
	public static Short STATE_HIDE=0;//隐身
	public static Short STATE_ONLINE=1;//在线
	public static Short STATE_BUSY=2;//忙碌
	public static Short STATE_LEAVE=3;//离开
	@Expose
	private Short state;
	@Expose
	private String userName;
	private String password;
	
	private Long companyId;			//所属公司的ID
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Short getState() {
		return state;
	}
	public void setState(Short state) {
		this.state = state;
	}
}
