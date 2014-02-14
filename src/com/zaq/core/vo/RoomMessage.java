package com.zaq.core.vo;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.zaq.core.common.Constants;
import com.zaq.core.parse.jsonparse.SendMessageParse;
import com.zaq.core.protocol.JsonPacket;

/**
 * 聊天室短消息
 * @author zaq
 *
 */
public class RoomMessage extends ShortMessage{
	private Long roomId;//聊天室ID DBUtils数据库获取用
	@Expose
	private Room room;

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	/**
	 * @return the room
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public static void main(String[] args) {
		SendMessage<RoomMessage> message=new SendMessage<RoomMessage>();
		RoomMessage roomMessage=new RoomMessage();
		roomMessage.setRoom(new Room(1l));
		message.setMessage(roomMessage);
		
		Gson OUTGson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(Constants.DATE_FORMAT_FULL).create();

		JsonPacket<SendMessage<RoomMessage>> packet=new JsonPacket<SendMessage<RoomMessage>>(message);
		
		Type type=SendMessageParse.roomType;
		
		System.out.println(OUTGson.toJson(packet, type));
	}
	
}
