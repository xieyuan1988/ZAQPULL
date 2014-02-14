package com.zaq.core.vo;

import java.util.Date;

import com.google.gson.annotations.Expose;
/**
 * 聊天室内的人
 * @author zaq
 *
 */
public class RoomUser {
	@Expose
	private Long roomId;//聊天室ID
	@Expose
	private Long userId;//其中的一个人
	@Expose
	private String userFullName;//其中的一个人
	@Expose
	private Short state;//设置消息状态
	@Expose
	private Date timeImport;//加入聊天室时间
	
	public static Short STATE_DEFAULT=0;//默认接收消息
	public static Short STATE_SHIELD=1;//屏蔽消息
	
	
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Short getState() {
		return state;
	}
	public void setState(Short state) {
		this.state = state;
	}
	public Date getTimeImport() {
		return timeImport;
	}
	public void setTimeImport(Date timeImport) {
		this.timeImport = timeImport;
	}
	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}
	/**
	 * @param userFullName the userFullName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
}
