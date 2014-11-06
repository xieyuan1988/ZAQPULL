package com.zaq.core.common;

import java.lang.reflect.Type;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.google.gson.Gson;
import com.zaq.business.service.SendMessageService;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.session.SessionPool;
import com.zaq.core.util.AppUtil;
import com.zaq.core.util.ThreadPool;
import com.zaq.core.vo.AppUser;
import com.zaq.core.vo.Message;
import com.zaq.core.vo.RoomMessage;
import com.zaq.core.vo.SendMessage;

/**
 * 发送需要握手确认的消息的业务处理类
 * 
 * @author zaq
 * 
 */
public class CommonSendService {
	private static SendMessageService sendMessageService = (SendMessageService) AppUtil.getBean("sendMessageService");
	private static Logger logger = Logger.getLogger(SendMessageService.class);
	/**
	 * 发送消息的业务处理
	 * @param inStr 接收到的原始信息
	 * @param outGson
	 * @param outType
	 * @param userId
	 *            接收人的ID
	 * @param sendPacket
	 * @param rePull
	 *            失败是否需要重新发送
	 * @param isConcurrence    true,并发发送消息，防止阻塞延迟    调用前先持久化消息message，防止保存重复的消息message
	 *            是否需要克
	 */
	public static void sendAndSaveMessage(String inStr,Gson outGson,  Type outType,  Long userId, JsonPacket<SendMessage> sendPacket,  boolean rePull, boolean isConcurrence) {
		if (isConcurrence) {
			try {
				sendPacket=cloneSendMessageBean(sendPacket);
				Runnable runnable=new SendMSGRunnable(inStr,outGson, outType, sendPacket, rePull, null, userId);
				ThreadPool.executorSendMessage(runnable);
			} catch (Exception e) {
				logger.error("消息包克隆失败", e);
				return;
			}

		} else {
			IoSession ioSession=SessionPool.getSessionPool().getSCbyUserId(userId);
			
			send(ioSession, inStr, outGson, outType, sendPacket, rePull);
		}

	}

	private static void send(final IoSession ioSession,String inStr,Gson outGson,  Type outType, JsonPacket<SendMessage> sendPacket,  boolean rePull){
		if(!sendPacket.isRequest()){//非请求 ，为系统 主动推送
			if(null==sendPacket.getObject().getReceiveId()){
				try {
					saveSendMessage(inStr, sendPacket, rePull);
					sendPacket.setMsgTAG(sendPacket.getObject().getReceiveId());
				} catch (ZAQprotocolException e) {
					sendPacket.setMsgTAG(Constants.TAG_DEFAULT);
				}
			}
		}
		
		if(null!=ioSession){
			final String outStr=outGson.toJson(sendPacket, outType);
			ServerHandler.getInstances().processWrite(ioSession, outStr);
			//给中转服务客户端转送此条消息
			ThreadPool.executorSendMessage(new Runnable() {
				@Override
				public void run() {
					AppUser user= (AppUser) ioSession.getAttribute(SessionPool.APPUSER);
					
					Long tUserId= SessionPool.getTransferAdminId(user.getCompanyId().intValue());
					if(null!=tUserId){
						IoSession tIOSession=SessionPool.getSessionPool().getSCbyUserId(tUserId);
						
						ServerHandler.getInstances().processWrite(tIOSession, outStr);
					}
					
				}
			});
		}
		
	}
	
	/**
	 * 发送消息的业务处理
	 * @param inStr 接收到的原始信息
	 * @param outGson
	 * @param outType
	 * @param session
	 *            接收人的ioSession
	 * @param sendPacket
	 * @param rePull
	 *            失败是否需要重新发送
	 * @param isConcurrence   true,并发发送消息，防止阻塞延迟    调用前先持久化消息message，防止保存重复的消息message
	 *            是否需要克
	 */
	public static void sendAndSaveMessage(String inStr, Gson outGson,  Type outType,  IoSession session, JsonPacket<SendMessage> sendPacket,  boolean rePull, boolean isConcurrence) {
		if (isConcurrence) {
			try {
				sendPacket=cloneSendMessageBean(sendPacket);
				Runnable runnable=new SendMSGRunnable(inStr,outGson, outType, sendPacket, rePull, session, null);
				ThreadPool.executorSendMessage(runnable);
			} catch (Exception e) {
				logger.error("消息包克隆失败", e);
				return;
			}

		} else {
			send(session, inStr, outGson, outType, sendPacket, rePull);
		}

	}

	
	
	private static void saveSendMessage(String inStr, JsonPacket<SendMessage> sendPacket, boolean rePull) throws ZAQprotocolException {

//		if (retWrite) {
//			sendPacket.getObject().setReadFlag(SendMessage.FLAG_READ);
//		} else {
			sendPacket.getObject().setReadFlag(SendMessage.FLAG_UNREAD);
//		}
		// 失败不用再推了吧？
		if (!rePull) {
			sendPacket.getObject().setDelFlag(Constants.FLAG_DELETED);
		}

		if(null==sendPacket.getObject().getReceiveId()){
			SendMessage<RoomMessage> sm = sendMessageService.save(sendPacket.getObject());

			if (null == sm.getReceiveId()) {
				throw new ZAQprotocolException("记录持久化失败" + inStr + "--readFlag:" + sendPacket.getObject().getReadFlag());
			}
		}
//		else {
//			sendMessageService.readSMsg(sendPacket.getObject().getReceiveId());
//		}
		
	}
	
	
	/**
	 * 消息发送包 克隆          note:   BeanUtils.cloneBean 克隆不了泛型  泪奔了一晚上fuck。。。。
	 * @param sendPacket
	 * @return
	 * @throws Exception
	 */
	public static JsonPacket<SendMessage> cloneSendMessageBean(JsonPacket<SendMessage> sendPacket) throws Exception{
		sendPacket = (JsonPacket<SendMessage>) BeanUtils.cloneBean(sendPacket);
		sendPacket.setObject((SendMessage)BeanUtils.cloneBean(sendPacket.getObject()));
		sendPacket.getObject().setMessage((Message)BeanUtils.cloneBean(sendPacket.getObject().getMessage()));
		
		return sendPacket;
	}
	
	/**
	 * 数据推送线程类
	 * @author zaq
	 *
	 */
	private static class SendMSGRunnable implements Runnable{
		private Gson outGson;

		private Type outType;

		private JsonPacket<SendMessage> sendPacket;

		private boolean rePull;
		
		private IoSession session;
		
		private String inStr;

		private Long userId;
		
		public SendMSGRunnable(String inStr,Gson outGson, Type outType, JsonPacket<SendMessage> sendPacket, boolean rePull,IoSession session,Long userId) throws Exception{
			this.outGson=(Gson) BeanUtils.cloneBean(outGson);//toJson方法可是不同步的哦
			this.outType=outType;
			this.sendPacket=sendPacket;
			this.session=session;
			this.rePull=rePull;       
			this.userId=userId;
			this.inStr=inStr;
		}
		
		@Override
		public void run() {
			String outStr =null;
			if(null!=userId){
				session=SessionPool.getSessionPool().getSCbyUserId(userId);
			}
			
			send(session, inStr, outGson, outType, sendPacket, rePull);
		}
		
	}
}
