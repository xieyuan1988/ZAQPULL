package com.zaq.core.protocol;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.annotations.Expose;
import com.zaq.core.common.Constants;
/**
 * 基础数据包
 * @author zyj
 *
 * @param <T>
 */
public class JsonPacket<T> extends BasePacket{
	public static final String DEFAULT_CLASS="String";
//	private Long userId;
//	private String userName;
	@Expose
	private String clazz$Id;
	@Expose
	private T object;
	@Expose
	private List<T> objects;
	@Expose
	private Short state=Constants.STATE_SUCCESS;	//返回状态 见常量
	
	/**
	 * Gson需要此构造方法   beanUtil需要public
	 */
	@SuppressWarnings("unused")
	public JsonPacket(){}
	
	public JsonPacket(String msg){
		this.clazz$Id=DEFAULT_CLASS;
		this.msg=msg;
//		this.sendTime=System.currentTimeMillis();
	}
	public JsonPacket(String msg,Short state){
		this.clazz$Id=DEFAULT_CLASS;
		this.msg=msg;
		this.state=state;
//		this.sendTime=System.currentTimeMillis();
	}
	public JsonPacket(String msg,Short state,long msgTAG){
		this.clazz$Id=DEFAULT_CLASS;
		this.msg=msg;
		this.state=state;
		this.msgTAG=msgTAG;
//		this.sendTime=System.currentTimeMillis();
	}
	public JsonPacket(T object){
		this.setObject(object);
//		this.sendTime=System.currentTimeMillis();
	}
	public JsonPacket(List<T> objects){
		this.setObjects(objects);
//		this.sendTime=System.currentTimeMillis();
	}
	
	public String getClazz$Id() {
		return clazz$Id;
	}

	public void setClazz$Id(String clazz$Id) {
		this.clazz$Id = clazz$Id;
	}

//	public String getUserName() {
//		return userName;
//	}
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
		if(null!=object){
			clazz$Id=object.getClass().getSimpleName();
		}
	}
	public List<T> getObjects() {
		return objects;
	}
	public void setObjects(List<T> objects) {
		this.objects = objects;
		if(null!=objects&&!objects.isEmpty()){
			clazz$Id=objects.get(0).getClass().getSimpleName();
		}
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Short getState() {
		return state;
	}

	public void setState(Short state) {
		this.state = state;
	}
	/**
	 * 组装简单json对象
	 * @return
	 */
	public String toSimpleJson(){
		return "{\"clazz$Id\":\""+DEFAULT_CLASS+"\",\"msg\":\""+this.msg+"\",\"state\":\""+this.state+"\",\"msgTAG\":\""+this.msgTAG+"\"}";
	}
	
	/**
	 * 组装admin登陆成功的消息
	 * @param onLineUser 在线用户ID组，以|分隔
	 * @return
	 */
	public String toLoginSucSimpleJson(String onLineUser){
		return "{\"clazz$Id\":\""+DEFAULT_CLASS+"\",\"msg\":\""+this.msg+"\",\"state\":\""+
		this.state+"\",\"object\":\""+onLineUser+"\",\"msgTAG\":\""+this.msgTAG+"\"}";
	}
	
	public static void main(String[] args) {
		Pattern clazzPattern=Pattern.compile("\"clazz\\$Id\":\"(\\w+)\"");
		String ss="{\"userName\":\"admin\",\"clazz$Id\":\"Login\",\"sendTime\":1388044054391,\"object\":{\"password\":\"zaq123\"}}";
		Matcher  matcher= clazzPattern.matcher(ss);
		if(matcher.find()){
			System.out.println(matcher.group(1));
		}
		
		System.out.println(new JsonPacket("登陆验证失败：用户名或密码错误", Constants.STATE_LOGINERROR,1).toSimpleJson());
	}

//	public Long getUserId() {
//		return userId;
//	}
//
//	public void setUserId(Long userId) {
//		this.userId = userId;
//	}
}
