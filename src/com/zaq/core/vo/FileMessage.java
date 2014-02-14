package com.zaq.core.vo;

import java.io.File;
import java.util.Date;

import com.zaq.core.util.Base64;

/**
 * 文件类型消息
 * @author zaq
 *
 */
public class FileMessage extends Message{
	private String fileType;//文件類型
	private String fileSize;//文件大小
	private String content;//Base64編碼后
	
	public FileMessage(){}
	public FileMessage(Long senderId,String sender,String fileType,File file){
		this.senderId=senderId;
		this.sender=sender;
		this.msgType=MSG_TYPE_PERSONAL;
		this.fileType=fileType;
		this.fileSize=file.length()+"byte";
		this.content=Base64.encodeFromFile(file);
		
		setSendTime(new Date());
	}
	public FileMessage(Long senderId,String sender,String fileType,File file,Short msgType){
		this.senderId=senderId;
		this.sender=sender;
		this.msgType=msgType;
		this.fileSize=file.length()+"byte";
		this.fileType=fileType;
		
		this.content=Base64.encodeFromFile(file);
		setSendTime(new Date());
	}
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
