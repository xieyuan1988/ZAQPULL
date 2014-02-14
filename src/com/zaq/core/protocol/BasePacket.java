package com.zaq.core.protocol;

import com.google.gson.annotations.Expose;
/**
 * 数据包基础类
 * @author zaq
 *
 */
public abstract class BasePacket {
	@Expose
	protected boolean isRequest=false;
	@Expose
	protected long msgTAG;//服务端的回执为发送消息的ID
	@Expose
	protected String msg;
	protected boolean isRePost;//是否为重新超时发送的消息
//	@Expose
//	protected long sendTime;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the msgTAG
	 */
	public long getMsgTAG() {
		return msgTAG;
	}
	/**
	 * @param msgTAG the msgTAG to set
	 */
	public void setMsgTAG(long msgTAG) {
		this.msgTAG = msgTAG;
	}
	public boolean isRequest() {
		return isRequest;
	}
	/**
	 * @param isRequest the isRequest to set
	 */
	public void setRequest(boolean isRequest) {
		this.isRequest = isRequest;
	}
	/**
	 * @return the isRePost
	 */
	public boolean isRePost() {
		return isRePost;
	}
	/**
	 * @param isRePost the isRePost to set
	 */
	public void setRePost(boolean isRePost) {
		this.isRePost = isRePost;
	}
}
