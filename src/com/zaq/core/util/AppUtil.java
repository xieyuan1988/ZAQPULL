package com.zaq.core.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.zaq.core.MessagePullSocketListener;
import com.zaq.core.common.BeanFactory;
import com.zaq.core.protocol.ZAQprotocolException;

public class AppUtil {
	private static String log4j="log4j.properties";
	private static String config="config.properties";
	private static Properties props ;
	
	public static void init() throws IOException {

		PropertyConfigurator.configure(PathUtil.instance().getPath(log4j)); 
		
		props= new Properties();
		InputStream is = new BufferedInputStream(AppUtil.class.getClassLoader().getResourceAsStream(config));
		props.load(is);

	}

	public static String getPropertity(String string) {
		return props.getProperty(string, "");
	}

	public static Object getBean(String string) {
		
		try {
			return BeanFactory.getBeanFactory().getBean(string);
		} catch (ZAQprotocolException e) {
			e.printStackTrace();
			return null;
		}
	}
}	
