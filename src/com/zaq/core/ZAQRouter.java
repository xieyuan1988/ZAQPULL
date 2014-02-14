package com.zaq.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.zaq.core.common.Constants;
import com.zaq.core.parse.Iparse;
import com.zaq.core.protocol.ZAQprotocolException;
/**
 * 分发处理器
 * @author zaq
 *
 */
public class ZAQRouter {
	private Logger logger=Logger.getLogger(ZAQRouter.class);
	private static Properties properties;
	
	private String FILE_NAME="router.properties";
	private String DEFAULT_CLASS_PATH;
	
	private static ZAQRouter router=new ZAQRouter();
	private ZAQRouter(){};
	
	private Map<String, Class> classChache=new HashMap<String, Class>();
	
	public static ZAQRouter getRouter(){
		if(null==router){
			 router=new ZAQRouter();
		}
		return router;
	}
	
	public void init(){
		String thizClassFullName=ZAQRouter.class.getName();
		DEFAULT_CLASS_PATH=thizClassFullName.substring(0, thizClassFullName.lastIndexOf("."))+".parse.jsonparse.";
		logger.info("默认解析路径为："+DEFAULT_CLASS_PATH);
		InputStream inStream=this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		properties=new Properties();
		try {
			properties.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Object o: properties.keySet()){
			
			try {
				classChache.put((String)o, Class.forName((String)properties.get(o)));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
	}
	
	public Class getByName(String className) throws ZAQprotocolException{
		Class c=classChache.get(className);
		if(null!=c){
			return c;
		}else{
			throw new ZAQprotocolException("系统未配置请求协议", Constants.STATE_UNFOUND);
		}
		
	}
	
	/**
	 * 获取类全名
	 * @param clazz 类简称
	 * @return
	 */
	private String getActionName(String clazz){
		if(null==properties.get(clazz)){
			return DEFAULT_CLASS_PATH+clazz+"Parse";
		}else {
			return (String)properties.get(clazz);
		}
	}
	/**
	 * 创建处理线程
	 * @param clazz
	 * @param getJsonVal
	 * @return
	 * @throws ZAQprotocolException
	 */
	@Deprecated
	public Iparse getParseThread(String clazz,String getJsonVal) throws ZAQprotocolException{
		
		clazz=getActionName(clazz);
		
		Iparse instance=null;
		try {
			instance=(Iparse)Class.forName(clazz).getConstructor(String.class).newInstance(getJsonVal);//newInstance();
		} catch (Exception e) {
			throw new ZAQprotocolException("找不到协议class："+clazz,e,Constants.STATE_UNFOUND);
		}
		
		return instance;
		
	}
	/**
	 * 创建处理线程
	 * @param ioSession
	 * @param clazz
	 * @param getJsonVal
	 * @return
	 * @throws ZAQprotocolException
	 */
	public Iparse getParseThread(IoSession ioSession,String clazz,String getJsonVal) throws ZAQprotocolException{
		Class c=classChache.get(clazz);
		if(null==c){
			clazz=getActionName(clazz);
			try {
				classChache.put(clazz, Class.forName(clazz));
				c=classChache.get(clazz);
			} catch (ClassNotFoundException e) {
				throw new ZAQprotocolException("找不到协议class："+clazz,e,Constants.STATE_UNFOUND);
			}
		}
		
		Iparse instance=null;
			try {
				instance=(Iparse)c.getConstructor(IoSession.class,String.class).newInstance(ioSession,getJsonVal);
			} catch (Exception e) {
				throw new ZAQprotocolException("创建class："+c.getName()+"实类失败",e,Constants.STATE_ERROR);
			}
		
		return instance;
		
	}
	
	public static void main(String[] args) throws ZAQprotocolException {
		String jsonTest="{\"clazz$Id\":\"Login\",\"object\":{\"password\":\"zaq123\",\"userName\":\"admin\"},\"sendTime\":1388382879934}";
		ZAQRouter router=new ZAQRouter();
		router.init();
		Iparse iparse=router.getParseThread("Login", jsonTest);
	}

}
