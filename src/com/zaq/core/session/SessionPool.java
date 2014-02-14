package com.zaq.core.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.zaq.core.common.Constants;
import com.zaq.core.common.ServerHandler;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.AppUser;
/**
 * session池
 * @author zaq
 *
 */
public class SessionPool {
	private static SessionPool instance=new SessionPool();
	private Map<Long, IoSession> userKeyMap=Collections.synchronizedMap(new HashMap<Long, IoSession>());
	public final static String  APPUSER="au";
	public final static String  APPUSERID="auId";
	public final static String  LOGINSTATE="ls";
//	public final static String  MESSAGE_SEND_TAG="msgSendTAG";
	public Logger logger=Logger.getLogger(SessionPool.class);
	
	private SessionPool(){}
	
	public static SessionPool getSessionPool(){
		if(null==instance){
			instance=new SessionPool();
		}
		return instance;
	}
	
	/**
	 * 验证成功建立长连接   一个用户只允许一处登陆
	 * @param ioSession
	 * @param appUser
	 */
	public synchronized void connect(IoSession ioSession,AppUser appUser){
		IoSession session=userKeyMap.get(appUser.getUserId());
		
		if(null!=session){
			ServerHandler.getInstances().processWrite(session, new JsonPacket("您的帐号在异地登陆", Constants.STATE_LOGINOTHER).toSimpleJson());
			disConnect(session);
		}
		
		ioSession.setAttribute(APPUSERID, appUser.getUserId());
		ioSession.setAttribute(APPUSER, appUser);
		ioSession.setAttribute(LOGINSTATE, appUser.getLoginState());
//		ioSession.setAttribute(MESSAGE_SEND_TAG, new AtomicInteger(0));
		
		userKeyMap.put(appUser.getUserId(), ioSession);
	}
	
	/**
	 * 断开事件 remove channel from SCmap while isExist
	 * @param channel
	 */
	public void disConnect(IoSession ioSession){
		if(null==ioSession){
			return;
		}
		
		try {
			if(!ioSession.close(false).awaitUninterruptibly(Long.valueOf(AppUtil.getPropertity("pull.awaitUninterruptibly")))){
				throw new ZAQprotocolException("等待"+AppUtil.getPropertity("pull.awaitUninterruptibly")+"ms后通道关闭异常");
			}
		} catch (Exception e) {
			logger.error("通道关闭异常", e);
		}finally{
//			synchronized (this.getClass())
				{ 
					Long userId=(Long) ioSession.getAttribute(APPUSERID);
					if(null!=userId) {
						System.out.println(userId+"断开连接");
						//清除记录，避免sessionClosed的调用重复remove
						ioSession.removeAttribute(APPUSERID);
//						ioSession.removeAttribute(APPUSER);  可能在断开后还没消息未处理完
//						ioSession.removeAttribute(LOGINSTATE);
						userKeyMap.remove(userId);
					}
				}
		}
		
	}
	/**
	 * 踢除用户
	 * @param userId
	 */
	public void disConnect(Long userId){
		
		IoSession isSession=getSCbyUserId(userId);
		
		if(null==isSession){
			return;
		}
		
		disConnect(isSession);
	}
	
	/**
	 * 按用户查询取socket连接
	 * @param userId
	 * @return
	 */
	public IoSession getSCbyUserId(Long userId){
		IoSession session=userKeyMap.get(userId);
		
		return session;
	}
}
