package com.zaq.business.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.zaq.business.dao.BaseDao;
import com.zaq.business.db.DbHelper;
import com.zaq.core.common.Constants;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.AppUtil;
import com.zaq.core.util.ThreadPool;
import com.zaq.core.vo.Room;
import com.zaq.core.vo.RoomUser;

/**
 * 聊天室管理service
 * @author zaq
 *
 */
public class RoomService extends BaseService<Room>{
	private static Logger logger=Logger.getLogger(RoomService.class);
	public static final String ROOM_INSERT=" INSERT INTO pull_room (timeCreate,title,userIdCreate) VALUES (?,?,?) ";
	public static final String ROOM_USER_INSERT=" INSERT INTO pull_room_user (roomId,userFullName,userId,state,timeImport) VALUES (?,?,?,?,?) ";
	public static final String ROOM_QUERY=" SELECT * FROM pull_room WHERE id = ? ";
	public static final String ROOM_USER_QUERY=" SELECT * FROM pull_room_user WHERE roomId = ? ";

	/**
	 * 往聊天室里面加人
	 * @param roomId
	 * @param userIds
	 * @return
	 */
	public boolean addUserToRoom(Connection conn, Long roomId,List<Long> userIds,List<String> userNames){
		try {
			if(null==conn){
				conn = DbHelper.getSqlConn();
			}
			
			for(int i=0;i<userIds.size();i++){
				BaseDao.getInstance().storeInfo(conn,ROOM_USER_INSERT, roomId,userNames.get(i),userIds.get(i),RoomUser.STATE_DEFAULT,new Date());
			}
			
			conn.commit();
			return true;
			
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
		return false;
	}
	
	public boolean addUserToRoomNotCommit(Connection conn, Long roomId,List<Long> userIds,List<String> userNames) throws SQLException{
			if(null==conn){
				conn = DbHelper.getSqlConn();
			}
			
			Set<String> hasAddUserId=new HashSet<String>();
			
			for(int i=0;i<userIds.size();i++){
				
				if(!hasAddUserId.contains(userIds.get(i).toString())){
					BaseDao.getInstance().storeInfo(conn,ROOM_USER_INSERT, roomId,userNames.get(i),userIds.get(i),RoomUser.STATE_DEFAULT,new Date());
					hasAddUserId.add(userIds.get(i).toString());
				}
			}
			
			return true;
	}
	
	/**
	 * 查询聊天室信息
	 * @param roomId
	 * @return
	 */
	public Room getRoom(Long roomId){
		Room room=null;
		try {
			room = BaseDao.getInstance().queryForObject(Room.class, ROOM_QUERY, roomId);
			
			List<RoomUser> roomUsers=BaseDao.getInstance().queryForOList(RoomUser.class, ROOM_USER_QUERY, roomId);
			
			for(RoomUser ru:roomUsers){
				room.getAddUserFullNames().add(ru.getUserFullName());
				room.getAddUserId().add(ru.getUserId());
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return room;
//		Room room=new Room(1l); 
//		room.getAddUserId().add(365l);
//		room.getAddUserId().add(254l);
//		room.getAddUserId().add(256l);
//		room.getAddUserId().add(446l);
//		room.getAddUserId().add(102l);
//		room.getAddUserId().add(341l);
//		room.getAddUserId().add(300l);
//		
//		room.getAddUserFullNames().add("刘政");
//		room.getAddUserFullNames().add("费志浩");
//		room.getAddUserFullNames().add("居哥");
//		room.getAddUserFullNames().add("章英杰");
//		room.getAddUserFullNames().add("系统管理员");
//		room.getAddUserFullNames().add("罗时迁");
//		room.getAddUserFullNames().add("熊平");
		
//		return room;
	}
	/**
	 * 创建聊天室
	 * @param room
	 * @return 成功返回roomId 
	 */
	public Room createRoom(Room room)throws Exception{
//		room.setId(1l);
//		room.setTitle("聊天组");
//		
//		return room;
		Connection conn=null;
		try {
			conn = DbHelper.getSqlConn();
			Long pk=BaseDao.getInstance().storeInfoAndGetGeneratedKey(conn, ROOM_INSERT,new Date(), room.getTitle(),room.getUserIdCreate());
			addUserToRoomNotCommit(conn, pk, room.getAddUserId(), room.getAddUserFullNames());
			conn.commit();
			room.setId(pk);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage(), e);
			}
			throw new ZAQprotocolException("数据异常"+e.getMessage(),e,Constants.STATE_ERROR);
		} finally {
			try {
				DbUtils.close(conn);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return room;
	}
}
