package com.zaq.core.vo;

import com.google.gson.annotations.Expose;
import com.zaq.core.common.Constants;
/**
 * 消息发送bean
 * @author zaq
 *
 */
public class SendMessage<T extends Message>{ 
	public static final Short FLAG_READ = Short.valueOf((short) 1);
	public static final Short FLAG_UNREAD = Short.valueOf((short) 0);
	private Long receiveId;//这是主键 
	@Expose
	private T message;
	@Expose
	private Long userId;	//接收人
	@Expose
	private String userFullname;
	@Expose
	private Short readFlag;
	private Short delFlag;
	private Long messageId;	//消息ID 方便DBUtils的转换

	public SendMessage(){}
	
	public SendMessage<ShortMessage> newShortSendMessage(Long toUserId,String toUserFullname,Message message){
		SendMessage<ShortMessage> sendMessage=new SendMessage<ShortMessage>();
		sendMessage.setUserId(toUserId);
		sendMessage.setUserFullname(toUserFullname);
		sendMessage.setDelFlag(Constants.FLAG_UNDELETED);
		sendMessage.setReadFlag(FLAG_UNREAD);
		sendMessage.setMessage((ShortMessage)message);
		
		return sendMessage;
	}
	
	public static SendMessage<ShortMessage> newShortSendMessage(Long toUserId,String toUserFullname,Long fromUserId,String fromUserFullname,String content){
		SendMessage<ShortMessage> sendMessage=new SendMessage<ShortMessage>();
		sendMessage.setUserId(toUserId);
		sendMessage.setUserFullname(toUserFullname);
		sendMessage.setDelFlag(Constants.FLAG_UNDELETED);
		sendMessage.setReadFlag(FLAG_UNREAD);
		sendMessage.setMessage(new ShortMessage(fromUserId, fromUserFullname, content));
		return sendMessage;
	}
	
	public static SendMessage<ShortMessage> newShortSendMessage(Long toUserId,String toUserFullname,String content){
		SendMessage<ShortMessage> sendMessage=new SendMessage<ShortMessage>();
		sendMessage.setUserId(toUserId);
		sendMessage.setUserFullname(toUserFullname);
		sendMessage.setDelFlag(Constants.FLAG_UNDELETED);
		sendMessage.setReadFlag(FLAG_UNREAD);
		sendMessage.setMessage(new ShortMessage(AppUser.SYSTEM_USER,AppUser.SYSTEM_USER_FULLNAME, content));
		return sendMessage;
	}
	


	public Message getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserFullname() {
		return this.userFullname;
	}

	public void setUserFullname(String userFullname) {
		this.userFullname = userFullname;
	}

	public Short getReadFlag() {
		return this.readFlag;
	}

	public void setReadFlag(Short readFlag) {
		this.readFlag = readFlag;
	}

	public Short getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(Short delFlag) {
		this.delFlag = delFlag;
	}

	public Long getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(Long receiveId) {
		this.receiveId = receiveId;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
}
