package com.zaq.core.parse.jsonparse;

import static com.zaq.core.common.CommonSendService.sendAndSaveMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.google.gson.reflect.TypeToken;
import com.zaq.business.service.MessageService;
import com.zaq.business.service.RoomService;
import com.zaq.core.common.Constants;
import com.zaq.core.parse.BaseJsonParse;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.session.SessionPool;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.AppUser;
import com.zaq.core.vo.Message;
import com.zaq.core.vo.Room;
import com.zaq.core.vo.RoomMessage;
import com.zaq.core.vo.SendMessage;
/**
 * 聊天室操作解析
 * @author zaq
 *
 */
public class RoomParse extends BaseJsonParse<JsonPacket<Room>>{
	private MessageService messageService= (MessageService) AppUtil.getBean("messageService");
	private RoomService roomService=(RoomService) AppUtil.getBean("roomService");
	private static Type outType=new TypeToken<JsonPacket<SendMessage<RoomMessage>>>(){}.getType();
	
	public RoomParse(IoSession ioSession, String jsonPacketStr) {
		super(ioSession, jsonPacketStr);
	}

	
	@Override
	public void initParse() throws ZAQprotocolException {
		
	}



	@Override
	public void parse() throws ZAQprotocolException{
		msgTAG=packet.getMsgTAG();
		
		Room room=packet.getObject();
		RoomMessage message=new RoomMessage();
		message.setSendTime(new Date());
		
		SendMessage<RoomMessage> sendMessage=new SendMessage<RoomMessage>();
		
		sendMessage.setMessage(message);
		sendMessage.setDelFlag(Constants.FLAG_UNDELETED);
		sendMessage.setReadFlag(SendMessage.FLAG_UNREAD);
		
		JsonPacket sendPacket=new JsonPacket<SendMessage<RoomMessage>>(sendMessage);
		
		sendPacket.setMsgTAG(msgTAG);
		
		if(room.getAddUserFullNames().size()!=room.getAddUserId().size()){
				throw new ZAQprotocolException("请不要发送错误数据,OK?", Constants.STATE_ERROR);
		}
		
		if(null==room.getId()){
				/**
				 *  标示: msgType=MSG_TYPE_IM_ROOM_CREATE 是个请求
				 */
			try {
				boolean flag=false;//是否有自己
				for(Long userId: room.getAddUserId()){
					if(userId.longValue()==appUser.getUserId()){
						flag=true;
					}
				}
				if(!flag){
					room.getAddUserFullNames().add(appUser.getFullname());
					room.getAddUserId().add(appUser.getUserId());
				}
				
				room.setUserIdCreate(appUser.getUserId());
				room.setTitle("聊天组("+((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname()+")");
				room=roomService.createRoom(room);
			} catch (Exception e) {
 				throw new ZAQprotocolException("创建聊天室失败", e, Constants.STATE_ERROR_ROOM_CREATE);
			}

			//返回创建成功的信息给创建人
			//设置默认title
			
			message.setRoom(room);
			message.setMsgType(Message.MSG_TYPE_IM_ROOM_CREATE);//设置返回的消息类型
			message.setSenderId((Long)ioSession.getAttribute(SessionPool.APPUSERID));
			message.setSender(((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname());
			message.setContent("创建聊天室成功");
			sendMessage.setUserId((Long)ioSession.getAttribute(SessionPool.APPUSERID));
			sendMessage.setUserFullname(((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname());
			sendPacket.setRequest(true);//是个请求
			sendAndSaveMessage(getPacketVal(),OUTGson,outType,this.ioSession,sendPacket,false,false);
			
		}else if(room.getAddUserId().isEmpty()){
				/**
				 * 标示：msgType=MSG_TYPE_IM_ROOM_QUERY  是个请求
				 */
					room=roomService.getRoom(room.getId());
					if(null!=room){//返回聊天室信息给查询人    
						message.setRoom(room);
						message.setMsgType(Message.MSG_TYPE_IM_ROOM_QUERY);//设置返回的消息类型
						message.setSenderId(AppUser.SYSTEM_USER);
						message.setSender(AppUser.SYSTEM_USER_FULLNAME);
						message.setContent("聊天室成员:"+getUserLink(room.getAddUserFullNames()));
						sendMessage.setUserId((Long)ioSession.getAttribute(SessionPool.APPUSERID));
						sendMessage.setUserFullname(((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname());
						sendPacket.setRequest(true);//是个请求
						sendAndSaveMessage(getPacketVal(),OUTGson,outType,this.ioSession,sendPacket,true,false);
					}else{
						//返回查询失败错误信息
//						ServerHandler.getInstances().processWrite(ioSession, new JsonPacket("查询失败或该聊天室不在在", Constants.STATE_ERROR).toSimpleJson());
						throw new ZAQprotocolException("查询失败或该聊天室不在在");
					}
			}else if(!room.getAddUserId().isEmpty()){
				Room roomOld=roomService.getRoom(room.getId());
				/**
				 * 标示：msgType=MSG_TYPE_IM_ROOM_ADDUSER  是个请求
				 */
				if(roomService.addUserToRoom(null,room.getId(), room.getAddUserId(),room.getAddUserFullNames())){
					//给每一个聊天室内的人发送新加入人的信息   								
					message.setRoom(room);
					message.setMsgType(Message.MSG_TYPE_IM_ROOM_ADDUSER);//设置返回的消息类型
					message.setSenderId((Long)ioSession.getAttribute(SessionPool.APPUSERID));
					message.setSender(((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname());
					
					message.setContent(((AppUser)ioSession.getAttribute(SessionPool.APPUSER)).getFullname()+"邀请："+getUserLink(room.getAddUserFullNames())+"加入了聊天室");
				
					//先保存消息获取ID
					try {
						messageService.save(message);
					} catch (Exception e) {
						logger.error("记录message持久化失败" + getPacketVal() ,e );
					}
					for(int i=0;i<roomOld.getAddUserId().size();i++){//群通知，包括自己
						if(appUser.getUserId().longValue()==roomOld.getAddUserId().get(i).longValue()){
							sendPacket.setRequest(true);//是个请求
						}
						sendMessage.setUserId(roomOld.getAddUserId().get(i));
						sendMessage.setUserFullname(roomOld.getAddUserFullNames().get(i));
						
						sendAndSaveMessage(getPacketVal(),OUTGson,outType,roomOld.getAddUserId().get(i), sendPacket, true,true);
					}
					
				}else{
//					sendPacket.setState(Constants.STATE_ERROR);
//					sendPacket.setMsg("加入失败");
//					message.setSenderId((Long)ioSession.getAttribute(SessionPool.APPUSERID));
//					
//					sendMessage(OUTGson,outType,this.ioSession,sendPacket,false);
//					ServerHandler.getInstances().processWrite(ioSession, new JsonPacket("聊天室添加人失败", Constants.STATE_ERROR_ROOM_ADDUSER).toSimpleJson());
					throw new ZAQprotocolException("聊天室添加人失败", Constants.STATE_ERROR_ROOM_ADDUSER);
				}
			}

	}
	
	/**
	 * 连接人名
	 * @param userFullNames
	 * @return
	 */
	private static  String getUserLink(List<String> userFullNames){
		StringBuilder stringBuilder=new StringBuilder();
		
		for(String u:userFullNames){
			stringBuilder.append(u).append(",");
			
		}
		
		if(stringBuilder.length()!=0){
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		}
		
		return stringBuilder.toString();
	}
	
}

