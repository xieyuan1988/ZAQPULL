package com.zaq.business.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.zaq.business.dao.BaseDao;
import com.zaq.business.db.DbHelper;
import com.zaq.core.common.Constants;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.AppUtil;
import com.zaq.core.vo.FileMessage;
import com.zaq.core.vo.Message;
import com.zaq.core.vo.RoomMessage;
import com.zaq.core.vo.ShortMessage;
/**
 * 消息service
 * @author zaq
 *
 */
public class MessageService extends BaseService<Message>{
	private static Logger logger = Logger.getLogger(MessageService.class);
	public static String sqlMsgInsert = "INSERT INTO pull_message(senderId,content,sender,msgType,sendTime,typeStr$type,messageUUID,fileType,fileSize,roomId) " + "VALUES (?,?,?,?,?,?,?,?,?,?)";
	public static String sqlMsgQuery = " SELECT * FROM pull_message WHERE messageId=?";
	public static String sqlRePostQuery = " SELECT COUNT(0) FROM pull_message WHERE senderId=? and roomId=? and content=? and sendTime>? ";
	
	public Message save(Message msg) throws Exception{
		Long msgId=null;
		Connection conn=null;
		try {
			conn = DbHelper.getSqlConn();
			
			if (msg instanceof RoomMessage) {
				RoomMessage rm = (RoomMessage) msg;
					msgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, MessageService.sqlMsgInsert,
							new Object[] { rm.getSenderId(), rm.getContent(), rm.getSender(), rm.getMsgType(), new Date(), rm.getTypeStr(), rm.getMessageUUID(),null,null,rm.getRoom().getId() });

			}else if (msg instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) msg;
					msgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, MessageService.sqlMsgInsert,
							new Object[] { sm.getSenderId(), sm.getContent(), sm.getSender(), sm.getMsgType(), new Date(), sm.getTypeStr(), sm.getMessageUUID(),null,null,null });

			} else if (msg instanceof FileMessage) {
				FileMessage fm = (FileMessage) msg;
					msgId = BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, MessageService.sqlMsgInsert,
							new Object[] { fm.getSenderId(), fm.getContent(), fm.getSender(), fm.getMsgType(), new Date(), fm.getTypeStr(), fm.getMessageUUID(),fm.getFileType(),fm.getFileSize(),null });
			}

			conn.commit();
			msg.setMessageId(msgId);
		} catch (SQLException e) {
			conn.rollback();
			throw new ZAQprotocolException(e.getMessage(), Constants.STATE_ERROR);
		} finally {
			try {
				DbUtils.close(conn);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return msg;
	}

	/**
	 * 是否为重复发送的消息  重新发送的。。先查看数据库6分钟内有无相同的消息
	 * @param message
	 * @return
	 */
	public synchronized boolean isRePost(RoomMessage message) {
		
		try {
			int count= BaseDao.getInstance().count(sqlRePostQuery, message.getSenderId(),message.getRoom().getId(),message.getContent(),getSexBefore());
		
			if(count<=0){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	private static Date getSexBefore(){
		Calendar c = Calendar.getInstance();

        c.add(Calendar.MINUTE, -6);
        						
        return c.getTime();
	}
	public static void main(String[] args) throws Exception {
		AppUtil.init();
		System.out.println(getSexBefore());
		int count= BaseDao.getInstance().count(sqlRePostQuery, "446","1","ccc",getSexBefore());
		System.out.println(count);	
	}
}
