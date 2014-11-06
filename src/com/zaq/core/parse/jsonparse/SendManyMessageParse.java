package com.zaq.core.parse.jsonparse;

import static com.zaq.core.common.CommonSendService.sendAndSaveMessage;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;

import com.google.gson.reflect.TypeToken;
import com.zaq.business.service.MessageService;
import com.zaq.core.common.Constants;
import com.zaq.core.parse.BaseJsonParse;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.FileMessage;
import com.zaq.core.vo.Message;
import com.zaq.core.vo.SendManyMessage;
import com.zaq.core.vo.SendMessage;
import com.zaq.core.vo.ShortMessage;
/**
 * 群发消息
 * @author zaq
 *
 */
public class SendManyMessageParse extends BaseJsonParse<JsonPacket<SendManyMessage<Message>>>{
	private MessageService messageService= (MessageService) AppUtil.getBean("messageService");
	private static Type ManyShortType=new TypeToken<JsonPacket<SendManyMessage<ShortMessage>>>(){}.getType();
	private static Type ManyFileType=new TypeToken<JsonPacket<SendManyMessage<FileMessage>>>(){}.getType();

	private Type outType;
	
	public SendManyMessageParse(IoSession ioSession, String jsonPacketStr) {
		super(ioSession, jsonPacketStr);
	}

	@Override
	public void initParse() throws ZAQprotocolException {
		String typeStr=null;
			Matcher matcher= SendMessageParse.typePattern.matcher(getPacketVal());
		if(matcher.find()){
			typeStr=matcher.group(1);
		}else {
			throw new ZAQprotocolException("传送协议错误---推送的message消息---typeStr$type不能为空",Constants.STATE_ERROR);
		}
		
		if(typeStr.equals(ShortMessage.class.getSimpleName())){
			this.type=ManyShortType;
			outType= SendMessageParse.shortType;
		}else if(typeStr.equals(FileMessage.class.getSimpleName())){
			this.type=ManyFileType;
			outType=SendMessageParse.fileType;
		}
	}
	
	@Override
	public void parse() {
		sendTagNow();
		
		packet.getObject().getMessage().setSendTime(new Date());
		List<Long> receviceUserIds=packet.getObject().getUserIds();
		SendMessage<Message> sendMessage=new SendMessage<Message>();
		sendMessage.setDelFlag(Constants.FLAG_UNDELETED);
		sendMessage.setReadFlag(SendMessage.FLAG_UNREAD);
		sendMessage.setMessage(packet.getObject().getMessage());
		
		if(StringUtils.isEmpty(sendMessage.getMessage().getMessageUUID())){//中转的服务器客户端可能转发他人消息
			sendMessage.getMessage().setSenderId(appUser.getUserId());
			sendMessage.getMessage().setSender(appUser.getFullname());
		}

		//先保存消息获取ID
		try {
			messageService.save(sendMessage.getMessage());
		} catch (Exception e) {
			logger.error("记录message持久化失败" + getPacketVal() ,e );
		}
		
		for(int i=0;i<receviceUserIds.size();i++){
			sendMessage.setUserFullname(packet.getObject().getUserFullnames().get(i));
			sendMessage.setUserId(receviceUserIds.get(i));
			
//			JsonPacket<SendMessage> packet=new JsonPacket<SendMessage>(sendMessage);
			JsonPacket packet=new JsonPacket<SendMessage<Message>>(sendMessage);
//			packet.setState(Constants.STATE_SUCCESS);
//			String pullVal=OUTGson.toJson(packet,outType);
//			
//			if(ServerHandler.getInstances().processWrite(receviceUserIds.get(i), pullVal)){
//				sendMessage.setReadFlag(SendMessage.FLAG_READ);
//			}
//			
//			sendMessageService.save(sendMessage);
			sendAndSaveMessage(getPacketVal(),OUTGson, outType, receviceUserIds.get(i), packet, true,true);
		}
		
		
		
		
	}

}
