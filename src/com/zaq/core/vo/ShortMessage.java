package com.zaq.core.vo;

import java.util.Date;

import com.google.gson.annotations.Expose;
/**
 * 短消息bean
 * @author zaq
 *
 */
public class ShortMessage extends Message{

	@Expose
	private String content;
	
	public ShortMessage(){}
	
	public ShortMessage(Long senderId,String sender,String content){
		this.senderId=senderId;
		this.sender=sender;
		this.msgType=MSG_TYPE_PERSONAL;
		this.content=content;
		setSendTime(new Date());
	}
	public ShortMessage(Long senderId,String sender,String content,Short msgType){
		this.senderId=senderId;
		this.sender=sender;
		this.msgType=msgType;
		this.content=content;
		setSendTime(new Date());
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
