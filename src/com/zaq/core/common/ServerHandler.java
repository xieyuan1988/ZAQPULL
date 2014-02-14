package com.zaq.core.common;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.zaq.business.service.SendMessageService;
import com.zaq.core.ZAQRouter;
import com.zaq.core.parse.ILogin;
import com.zaq.core.parse.Iparse;
import com.zaq.core.parse.jsonparse.LoginParse;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.session.SessionPool;
import com.zaq.core.util.AppUtil;
import com.zaq.core.util.ThreadPool;
import com.zaq.core.vo.AppUser;

/**
 * Mina消息处理
 * @author zaq
 *
 */
public class ServerHandler extends IoFilterAdapter implements IoHandler {
	private static SendMessageService sendMessageService = (SendMessageService) AppUtil.getBean("sendMessageService");
	private static Logger logger=Logger.getLogger(ServerHandler.class);
	private SessionPool sessionPool=SessionPool.getSessionPool();
    private static ServerHandler samplMinaServerHandler = null;
	private final Pattern clazzPattern=Pattern.compile("\"clazz\\$Id\":\"(\\w+)\"");//动作匹配
	private ILogin login=LoginParse.getLoginParse();
//	private final static String DISCONNECT="远程主机强迫关闭了一个现有的连接。";

	private AtomicInteger out=new AtomicInteger(0);
	private AtomicInteger in=new AtomicInteger(0);
	
    public static ServerHandler getInstances() {
        if (null == samplMinaServerHandler) {
            samplMinaServerHandler = new ServerHandler();
        }
        return samplMinaServerHandler;
    }

    private ServerHandler() {

    }

    // 当连接后打开时触发此方法，一般此方法与 sessionCreated sessionOpened会被同时触发
    // 当一个新客户端连接后触发此方法.
    public void sessionCreated(IoSession ioSession) throws Exception {
//    	logger.info("ioSession.isBothIdle():"+ioSession.isBothIdle()+"ioSession.isClosing():"+ioSession.isClosing()+"ioSession.isConnected():"+ioSession.isConnected()+"ioSession.isReaderIdle():"+ioSession.isReaderIdle()+"ioSession.isReadSuspended():"+ioSession.isReadSuspended()+"ioSession.isWriterIdle():"+ioSession.isWriterIdle()+"ioSession.isWriteSuspended():"+ioSession.isWriteSuspended());
    	logger.info("sessionCreated");
    	
    }
    public void sessionOpened(IoSession ioSession) throws Exception {

    	logger.info("sessionOpened");
    }
    public void sessionClosed(IoSession ioSession) {
    	logger.info("sessionClosed");
    	sessionPool.disConnect(ioSession);//自动断开
    }
    
    public void messageReceived(IoSession ioSession, Object obj)
            throws Exception {    
    	if(obj instanceof String){
    		logger.info("服务端接收到消息："+in.getAndIncrement()+"个："+obj);
        	try {
    			parseJsonPacket((String)obj,ioSession);
    		} catch (ZAQprotocolException e) {
    			processWrite(ioSession, new JsonPacket(e.getMessage(), e.getState()).toSimpleJson());
    		}
    	}else{
    		doTag(ioSession, (Long)obj);
    	}
    	
    }

    public void exceptionCaught(IoSession ioSession, Throwable e)
            throws Exception {
    	logger.info("exceptionCaught:"+e.getMessage());
		processWrite(ioSession, new JsonPacket("系统异常："+e.getMessage(), Constants.STATE_ERROR).toSimpleJson());

//    	if(DISCONNECT.equals(e.getMessage())){
    		sessionPool.disConnect(ioSession);//自动断开
//    	}
    }

    // 当消息传送到客户端后触发
    public void messageSent(IoSession ioSession, Object jsonVal) throws Exception {
    	logger.info(((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname()+"-客户端接收到消息："+out.getAndIncrement()+"个："+jsonVal);
    }

    // 当连接空闲时触发此方法.心跳检测
    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus arg1) throws Exception {
//    	if(processWrite(ioSession, new JsonPacket("", Constants.STATE_HEART).toSimpleJson())){
//    		logger.info("sessionIdle:"+((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname());
//    	}else{
//    		System.out.println("我要看你断开");
//    	}
    	ioSession.write(Constants.HEART_PACKET);
    }
    
    /**
     * 处理回执
     * @param ioSession
     * @param msgTag
     */
    public void doTag(IoSession ioSession,long msgTag){
    	logger.info("服务端接收到:"+((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname()+"的回执："+msgTag);
    	sendMessageService.readSMsg(msgTag);//更新为已读
    }
    
	/**
	 * 推送信息 到操作系统的缓存
	 * @param userId
	 * @param jsonVal
	 */
	public void  processWrite(IoSession ioSession,String jsonVal){
		if(null!=ioSession){
			try {
				ioSession.write(jsonVal);
//				future.awaitUninterruptibly(Long.valueOf(AppUtil.getPropertity("pull.awaitUninterruptibly"))); // 等待发送数据操作完成
//				if(future.isWritten()) // 数据已经被提交到操作系统的缓冲区
				
			} catch (Exception e) {
				sessionPool.disConnect(ioSession);
			}
		}
	}

	/**
	 * 推送信息
	 * @param userId
	 * @param jsonVal
	 */
//	public boolean processWrite(Long userId,String jsonVal,int msgTag){
//		IoSession ioSession=sessionPool.getSCbyUserId(userId);
//		if(null!=ioSession){
//			return processWrite(ioSession,jsonVal,msgTag);
//		}
//		return false;
//	}
    
	/**
	 * 解析JSON数据包
	 * @param getJsonVal
	 * @throws ZAQprotocolException 
	 */
	private void parseJsonPacket(final String getJsonVal,final IoSession ioSession) throws ZAQprotocolException {
		String clazz="";
		Matcher matcher= clazzPattern.matcher(getJsonVal);
		if(matcher.find()){
			clazz=matcher.group(1);
		}else {
			throw new ZAQprotocolException("传送协议错误----或clazz$Id为空",Constants.STATE_ERROR);
		}
		
		if(clazz.equals("Login")){//登陆
			
			ThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					AppUser appUser=null;
					try {
						appUser = login.login(getJsonVal);
					} catch (ZAQprotocolException e) {
						processWrite(ioSession, new JsonPacket(e.getMessage(), e.getState()).toSimpleJson());
						return;
					}
					if(null!=appUser){
						sessionPool.connect(ioSession, appUser);
						processWrite(ioSession, new JsonPacket("登陆成功", Constants.STATE_SUCCESS).toSimpleJson());
					}else {
						processWrite(ioSession, new JsonPacket("登陆验证失败", Constants.STATE_LOGINERROR).toSimpleJson());
						sessionPool.disConnect(ioSession);//自动断开
					}
					
				}
			});
			
		}else{
			//路由解析
			Iparse parse= ZAQRouter.getRouter().getParseThread(ioSession,clazz,getJsonVal);
			ThreadPool.execute(parse);
//			parse.run();
		}
		
	}
}