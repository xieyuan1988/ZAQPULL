package com.zaq.business.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.zaq.business.dao.BaseDao;
import com.zaq.business.db.DbHelper;
import com.zaq.core.ZAQRouter;
import com.zaq.core.common.Constants;
import com.zaq.core.protocol.JsonPacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.FileMessage;
import com.zaq.core.vo.Message;
import com.zaq.core.vo.Room;
import com.zaq.core.vo.RoomMessage;
import com.zaq.core.vo.SendMessage;
import com.zaq.core.vo.ShortMessage;

public class SendMessageService extends BaseService<SendMessage> {
	private Logger logger = Logger.getLogger(SendMessageService.class);
	private String sqlSendMsgInsert = "INSERT INTO pull_send_message(messageId,userId,readFlag,delFlag,userFullname) VALUES (?,?,?,?,?)";

	private String sqlSendMsgQuery = " select * from  pull_send_message where userId=? and readFlag=? and delFlag=? limit 0,"+Constants.LIMIT;

	private String sqlSMsgUpdate = "UPDATE pull_send_message SET readFlag=1 where receiveId=?";

	/**
	 * 消息 更新成已读状态
	 * @param receiveId  接收消息的ID
	 */
	public void readSMsg(Long receiveId){
		try {
			BaseDao.getInstance().update(sqlSMsgUpdate, receiveId);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 推送数据保存记录
	 */
	public SendMessage save(SendMessage sendMessage){

		Message msg = sendMessage.getMessage();
		Connection conn=null;
		try {
			conn = DbHelper.getSqlConn();
			Long msgId = sendMessage.getMessage().getMessageId();
			
			if (msg instanceof RoomMessage) {
				RoomMessage rm = (RoomMessage) msg;
				if (null == msgId) {
					msgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, MessageService.sqlMsgInsert,
							new Object[] { rm.getSenderId(), rm.getContent(), rm.getSender(), rm.getMsgType(), rm.getSendTime(), rm.getTypeStr(), rm.getMessageUUID(),null,null,rm.getRoom().getId() });
				}

			}else if (msg instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) msg;
				if (null == msgId) {
					msgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, MessageService.sqlMsgInsert,
							new Object[] { sm.getSenderId(), sm.getContent(), sm.getSender(), sm.getMsgType(), sm.getSendTime(), sm.getTypeStr(), sm.getMessageUUID(),null,null,null });
				}

			} else if (msg instanceof FileMessage) {
				FileMessage fm = (FileMessage) msg;
				if (null == msgId) {
					msgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, MessageService.sqlMsgInsert,
							new Object[] { fm.getSenderId(), fm.getContent(), fm.getSender(), fm.getMsgType(), fm.getSendTime(), fm.getTypeStr(), fm.getMessageUUID(),fm.getFileType(),fm.getFileSize(),null });
				}
			}

			long sendMsgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, sqlSendMsgInsert,
					new Object[] { msgId, sendMessage.getUserId(), sendMessage.getReadFlag(), sendMessage.getDelFlag(), sendMessage.getUserFullname() });

			conn.commit();
			sendMessage.getMessage().setMessageId(msgId);
			sendMessage.setReceiveId(sendMsgId);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage(), e);
			}
		} finally {
			try {
				DbUtils.close(conn);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return sendMessage;
	}
	/**
	 * 查询示读的消息
	 * @param userId 后续将分页处理
	 * @return
	 */
	public List<SendMessage> queryUnRead(Long userId) {

		List<SendMessage> sendMessages = null;
		try {
			sendMessages = baseDao.queryForOList(SendMessage.class, sqlSendMsgQuery, userId, SendMessage.FLAG_UNREAD, Constants.FLAG_UNDELETED);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		if(sendMessages.isEmpty()){
			return null;
		}
		
		
		List<SendMessage> retSendMessages = new ArrayList<SendMessage>();//返回值
		
		ResultSet messageRs = null;
		Connection conn = DbHelper.getSqlConn();
		for (SendMessage sMsg : sendMessages) {
			try {
				messageRs = baseDao.queryForObject(conn, MessageService.sqlMsgQuery, sMsg.getMessageId());
				if (null != messageRs) {
					if (messageRs.next()) {
						try {
							sMsg.setMessage((Message) DbHelper.toBean(messageRs, ZAQRouter.getRouter().getByName(messageRs.getString("typeStr$type"))));
							
							if(sMsg.getMessage() instanceof RoomMessage){
								RoomMessage roomMessage=(RoomMessage) sMsg.getMessage();
								Room room=new Room(roomMessage.getRoomId());
								roomMessage.setRoom(room);
							}
							
							retSendMessages.add(sMsg);
						} catch (ZAQprotocolException e) {
							logger.error(e.getMessage(), e);
							break;
						}
					}
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				break;
			}
		}
		try {
			DbUtils.close(conn);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return retSendMessages;
	}

	public static void main(String[] args) throws IOException, SQLException {
		Type type = ParameterizedTypeImpl.make(JsonPacket.class, new Type[] { Message.class }, null);

		AppUtil.init();
		ZAQRouter.getRouter().init();
		SendMessageService messageService = new SendMessageService();
		messageService.queryUnRead(5l);

	}
}
