package com.zaq.core.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
/**
 * 聊天室
 * @author zaq
 *
 */
public class Room {
	@Expose
	private Long id;		//id    ID为空时为新创建的聊天室  
	private Date timeCreate;//创建时间
	@Expose
	private String photo;
	@Expose
	private String title;	//标题
	@Expose
	private List<Long> addUserId=new ArrayList<Long>();//新加入的人    ID不为空	人为空时查询聊天室 信息   不为空时添加新的成员
	@Expose
	private List<String> addUserFullNames=new ArrayList<String>();				//同卡Id
	@Expose
	private Long userIdCreate;//创建人
	
	public Room(){
		
	}
	
	public Room(Long roomId){
		this.id=roomId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getTimeCreate() {
		return timeCreate;
	}
	public void setTimeCreate(Date timeCreate) {
		this.timeCreate = timeCreate;
	}
 	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the userIdCreate
	 */
	public Long getUserIdCreate() {
		return userIdCreate;
	}
	/**
	 * @param userIdCreate the userIdCreate to set
	 */
	public void setUserIdCreate(Long userIdCreate) {
		this.userIdCreate = userIdCreate;
	}
	/**
	 * @return the addUserId
	 */
	public List<Long> getAddUserId() {
		return addUserId;
	}
	/**
	 * @param addUserId the addUserId to set
	 */
	public void setAddUserId(List<Long> addUserId) {
		this.addUserId = addUserId;
	}
	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}
	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * @return the addUserFullNames
	 */
	public List<String> getAddUserFullNames() {
		return addUserFullNames;
	}

	/**
	 * @param addUserFullNames the addUserFullNames to set
	 */
	public void setAddUserFullNames(List<String> addUserFullNames) {
		this.addUserFullNames = addUserFullNames;
	}

}
