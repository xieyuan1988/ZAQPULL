package com.zaq.core.common;


/**
 * 常量表
 * @author zaq
 *
 */
public class Constants {
	
	public static final String SERVER_ENCODE="GBK";//服务的编码方式
	/**
	 * gson对象转换不是同步的-_-!  蛋痛
	 */
//	public static Gson INGson=new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_FULL).create();
//	public static Gson OUTGson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(Constants.DATE_FORMAT_FULL).create();

	
	public static final Short FLAG_DISABLE = 0;

	public static final Short FLAG_ACTIVATION = 1;
	
	public static final Short FLAG_GOLDMANTIS = 3;//集团用户

	public static final Short FLAG_DELETED = 1;

	public static final Short FLAG_UNDELETED = 0;
	public static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
	//返回状态信息
//	public final static Short STATE_HEART=100;
	public final static Short STATE_SUCCESS=200;
	public final static Short STATE_LOGINERROR=401;
	public final static Short STATE_LOGINOTHER=402;//异地登陆
	public final static Short STATE_FORBIT=403;
	public final static Short STATE_UNFOUND=404;
	public final static Short STATE_ERROR=500;
	public final static Short STATE_ERROR_ROOM_CREATE=501;
	public final static Short STATE_ERROR_ROOM_ADDUSER=502;
	
	//分页
	public final static Integer LIMIT=20;
	
	//心跳检测数据
	public static final String HEART_PACKET="Z";
	
	//不用等待的回执
	public static final int TAG_DEFAULT=0;
	
	//消息10秒延时
	public static final int TIMEOUT_DELAY=1000*10;
}
