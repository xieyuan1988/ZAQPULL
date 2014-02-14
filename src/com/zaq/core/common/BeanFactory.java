package com.zaq.core.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zaq.business.service.AppUserService;
import com.zaq.business.service.MessageService;
import com.zaq.business.service.RoomService;
import com.zaq.business.service.SendMessageService;
import com.zaq.core.protocol.ZAQprotocolException;
/**
 * bean 工厂 ，后续用注解扫描
 * @author zaq
 *
 */
public class BeanFactory {
	private static Map<String, Object> beanCahaceMap=new HashMap<String, Object>();
	private static BeanFactory instance=new BeanFactory();
	private BeanFactory(){}
	
	public static BeanFactory getBeanFactory(){
		
		if(null==instance){
			instance=new BeanFactory();
		}
		
		return instance;
	}
	
	public  Object getBean(String beanName) throws ZAQprotocolException{
		if(StringUtils.isEmpty(beanName)){
			return null;
		}
		
		if(beanName.equals("appUserService")){
			if(null==beanCahaceMap.get(beanName)){
				beanCahaceMap.put(beanName, new AppUserService());
			}
			return beanCahaceMap.get(beanName);
		}else if(beanName.equals("sendMessageService")){
			if(null==beanCahaceMap.get(beanName)){
				beanCahaceMap.put(beanName, new SendMessageService());
			}
			return beanCahaceMap.get(beanName);
		}else if(beanName.equals("roomService")){
			if(null==beanCahaceMap.get(beanName)){
				beanCahaceMap.put(beanName, new RoomService());
			}
			return beanCahaceMap.get(beanName);
		}else if(beanName.equals("messageService")){
			if(null==beanCahaceMap.get(beanName)){
				beanCahaceMap.put(beanName, new MessageService());
			}
			return beanCahaceMap.get(beanName);
		}else{
			throw new ZAQprotocolException("没有找到类："+beanName);
		}
	}
	
}
