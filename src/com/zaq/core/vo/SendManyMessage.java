package com.zaq.core.vo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
/**
 * 消息发送bean
 * @author zaq
 *
 */
public class SendManyMessage<T extends Message>{ 
	@Expose
	private T message;
	@Expose
	private List<Long> userIds=new ArrayList<Long>();;	//多个接收人ID 以
	@Expose
	private List<String> userFullnames=new ArrayList<String>();//多个接收人姓名

	public SendManyMessage(){}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public List<String> getUserFullnames() {
		return userFullnames;
	}

	public void setUserFullnames(List<String> userFullnames) {
		this.userFullnames = userFullnames;
	}

}
