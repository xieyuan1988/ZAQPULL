package com.zaq.core.parse.jsonparse;

import static com.zaq.core.common.CommonSendService.sendAndSaveMessage;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.session.IoSession;

import com.google.gson.reflect.TypeToken;
import com.zaq.business.service.MessageService;
import com.zaq.business.service.RoomService;
import com.zaq.core.common.Constants;
import com.zaq.core.parse.BaseJsonParse;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.FileMessage;
import com.zaq.core.vo.Room;
import com.zaq.core.vo.RoomMessage;
import com.zaq.core.vo.SendMessage;
import com.zaq.core.vo.ShortMessage;
/**
 * 消息分析中转器
 * @author zaq
 *
 */
public class SendMessageParse extends BaseJsonParse<JsonPacket<SendMessage>>{
	public static final Pattern typePattern=Pattern.compile("\"typeStr\\$type\":\"(\\w+)\"");//动作匹配
	public static Type shortType=new TypeToken<JsonPacket<SendMessage<ShortMessage>>>(){}.getType();
	public static Type fileType=new TypeToken<JsonPacket<SendMessage<FileMessage>>>(){}.getType();
	public static Type roomType=new TypeToken<JsonPacket<SendMessage<RoomMessage>>>(){}.getType();

	private RoomService roomService=(RoomService) AppUtil.getBean("roomService");
	private MessageService messageService= (MessageService) AppUtil.getBean("messageService");
	public SendMessageParse(IoSession ioSession,String jsonPacketStr) {
		super(ioSession,jsonPacketStr);
	}
	
	@Override
	public void initParse() throws ZAQprotocolException {
		String typeStr=null;
			Matcher matcher= typePattern.matcher(getPacketVal());
		if(matcher.find()){
			typeStr=matcher.group(1);
		}else {
			throw new ZAQprotocolException("传送协议错误---推送的message消息---typeStr$type不能为空",Constants.STATE_ERROR);
		}
		
		if(typeStr.equals(RoomMessage.class.getSimpleName())){
			this.type=roomType;
		}else if(typeStr.equals(ShortMessage.class.getSimpleName())){
			this.type=shortType;
		}else if(typeStr.equals(FileMessage.class.getSimpleName())){
			this.type=fileType;
		}
	}
	
	@Override
	public void parse() {
		sendTagNow();
		//暂不支持 packet.getObjects()
		if(null==packet.getObject()){
			return ;
		}
		//记录接收推送日志
		packet.getObject().setReadFlag(SendMessage.FLAG_UNREAD);
		packet.getObject().setDelFlag(Constants.FLAG_UNDELETED);
		packet.getObject().getMessage().setSenderId(appUser.getUserId());
		packet.getObject().getMessage().setSender(appUser.getFullname());
		packet.getObject().getMessage().setSendTime(new Date());
		packet.setState(Constants.STATE_SUCCESS);
		logger.info("推送记录接收到的消息:"+getPacketVal());
		
		if(type==roomType){
			RoomMessage message=(RoomMessage) packet.getObject().getMessage();
			
			//重新发送的。。先查看数据库5分钟内有无相同的消息
			if(packet.isRePost()&&messageService.isRePost(message)){
				return;//无需要再次发送
			}
			
			Room room=roomService.getRoom(message.getRoom().getId());
			//发送给聊天室内的每一个
//			for(Long userId : room.getAddUserId()){
//				sendMessage(OUTGson, type, userId, packet, true);
//			}
			//先保存消息获取ID
			try {
				packet.getObject().setMessage(messageService.save(message));
			} catch (Exception e) {
				logger.error("记录message持久化失败" + getPacketVal() ,e );
			}//先保存消息获取ID
			
			for(int i=0;i<room.getAddUserId().size();i++){
				if(room.getAddUserId().get(i).longValue()==appUser.getUserId().longValue()){
					continue;
				}
				packet.getObject().setUserId(room.getAddUserId().get(i));
				packet.getObject().setUserFullname(room.getAddUserFullNames().get(i));
				sendAndSaveMessage(getPacketVal(),OUTGson, type, room.getAddUserId().get(i), packet, true,true);
			}
			
		}else{
//		   if(ServerHandler.getInstances().processWrite(packet.getObject().getUserId(), OUTGson.toJson(packet, type))){
//	        	packet.getObject().setReadFlag(SendMessage.FLAG_READ);
//	        	//TODO 记录推送成功日志
//	        	logger.info("推送接收到的消息success:"+getPacketVal());
//	        }
			sendAndSaveMessage(getPacketVal(),OUTGson, type, packet.getObject().getUserId(), packet, true,false);
		}
		
      
		
//		//持久化接收到的消息
//		SendMessage sm= sendMessageService.save(packet.getObject());
//		if(null==sm.getReceiveId()){
//			//TODO 记录持久化失败
//			logger.error("记录持久化失败"+getPacketVal()+"--readFlag:"+packet.getObject().getReadFlag());
//		}
		
	}

}
