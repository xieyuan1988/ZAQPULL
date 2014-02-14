package com.zaq.core.parse;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaq.core.common.Constants;
import com.zaq.core.common.ServerHandler;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.session.SessionPool;
import com.zaq.core.vo.AppUser;

/**
 * 解析总接口
 * @author zyj
 *
 */
public abstract class Iparse implements Runnable{
	protected Logger logger=Logger.getLogger(this.getClass());
	protected  Gson INGson=new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_FULL).create();
	protected  Gson OUTGson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(Constants.DATE_FORMAT_FULL).create();
	protected String packetVal;
	protected IoSession ioSession;
	protected AppUser appUser;
	protected Long msgTAG;
	
	public Iparse(IoSession ioSession,String jsonPacketStr){
		this.packetVal=jsonPacketStr;
		this.ioSession=ioSession;
	}	
	
	/**
	 * 处理前初始化  可重写
	 */
	public abstract void initParse() throws ZAQprotocolException ;
	
	/**
	 * 处理前事件 可重写
	 */
	public abstract void beforeParse() throws ZAQprotocolException;
	/**
	 * 处理
	 */
	public abstract void parse() throws ZAQprotocolException;
	/**
	 * 处理后事件 可重写
	 */	
	public abstract void afterParse() throws ZAQprotocolException;
	/**
	 * 马上发送回执
	 */
	public abstract void sendTagNow();
	
	/**
	 * 处理模版
	 */
//	@Override
	public final void run(){
		try {
			if(null==ioSession.getAttribute(SessionPool.APPUSER)){
				throw new ZAQprotocolException("系统异常 session 已失效 或还未进行验证", Constants.STATE_ERROR);
			}else{
				appUser=(AppUser)ioSession.getAttribute(SessionPool.APPUSER);
			}
			initParse();
			beforeParse();
			parse();
			afterParse();
		} catch (ZAQprotocolException e) {
			ServerHandler.getInstances().processWrite(ioSession, new JsonPacket(e.getMessage(), e.getState(),msgTAG).toSimpleJson());
		}catch (Exception e) {
			logger.error("系统异常", e);
			ServerHandler.getInstances().processWrite(ioSession, new JsonPacket(e.getMessage(), Constants.STATE_ERROR,msgTAG).toSimpleJson());
		}
	}
	
	public String getPacketVal() {
		return packetVal;
	}
	public void setPacketVal(String packetVal) {
		this.packetVal = packetVal;
	}
	
}
