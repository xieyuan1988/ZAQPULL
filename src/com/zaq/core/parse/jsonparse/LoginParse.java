package com.zaq.core.parse.jsonparse;

import static com.zaq.core.common.CommonSendService.sendAndSaveMessage;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zaq.business.service.AppUserService;
import com.zaq.business.service.SendMessageService;
import com.zaq.core.common.Constants;
import com.zaq.core.parse.ILogin;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.session.SessionPool;
import com.zaq.core.util.AppUtil;
import com.zaq.core.util.ThreadPool;
import com.zaq.core.vo.AppUser;
import com.zaq.core.vo.Login;
import com.zaq.core.vo.Message;
import com.zaq.core.vo.RoomMessage;
import com.zaq.core.vo.SendMessage;
import com.zaq.core.vo.ShortMessage;
/**
 * 登陆验证解析
 * @author zaq
 *
 */
public class LoginParse implements ILogin{
	private Type type=new TypeToken<JsonPacket<Login>>(){}.getType();//登陆类型
	
	private SendMessageService sendMessageService=(SendMessageService) AppUtil.getBean("sendMessageService");
	private AppUserService appUserService=(AppUserService) AppUtil.getBean("appUserService");
	
	private static LoginParse instance=new LoginParse();
	/**
	 * 单例
	 */
	private LoginParse(){}
	
	public static LoginParse getLoginParse(){
		if(null==instance){
			instance=new LoginParse();
		}
		return instance;
	}
	
	@Override
	public AppUser login(String packetStr) throws ZAQprotocolException {
		Gson INGson=new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_FULL).create();
		
		JsonPacket<Login> jsonPacket= INGson.fromJson(packetStr,type);
		
		String userName= jsonPacket.getObject().getUserName();
		String password= jsonPacket.getObject().getPassword();
		Short loginState= jsonPacket.getObject().getState();
		final AppUser appUser=appUserService.login(userName,password);
		appUser.setLoginState(loginState);
		
		ThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				sendUnRead(appUser.getUserId());
			}
		});
		
		return appUser;
	}
	/**
	 *  推送未读消息   一条条的推。。。。
	 * @param userId
	 */
	public void sendUnRead(Long userId){
		List<SendMessage> listUnRead=sendMessageService.queryUnRead(userId);
		if(null==listUnRead){
			return;
		}
		Gson OUTGson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(Constants.DATE_FORMAT_FULL).create();

		for(SendMessage sendMessage:listUnRead){
			JsonPacket<SendMessage> packet=new JsonPacket<SendMessage>(sendMessage);
			packet.setMsgTAG(sendMessage.getReceiveId());//放置回执单
			if(sendMessage.getMessage() instanceof RoomMessage){
				sendAndSaveMessage(null,OUTGson, SendMessageParse.roomType, userId, packet, true, false);
			}else if(sendMessage.getMessage() instanceof ShortMessage){
				sendAndSaveMessage(null,OUTGson, SendMessageParse.shortType, userId, packet, true, false);
			}
			
		}
		//继续推未读消息
		if(listUnRead.size()>=Constants.LIMIT){
			try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
			if(null!=SessionPool.getSessionPool().getSCbyUserId(userId)&&SessionPool.getSessionPool().getSCbyUserId(userId).isConnected()){
				sendUnRead(userId);
			}
		}
		
	}
}
