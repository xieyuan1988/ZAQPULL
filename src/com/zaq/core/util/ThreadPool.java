package com.zaq.core.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 控制线程量
 * @author zhangyj
 * @date Mar 28, 2012
 */
public class ThreadPool {

	private static Executor executor;//消息解析线程池
	private static Executor executorSendMessage;//消息分发线程池
	
	static {
		executor = Executors.newFixedThreadPool(Integer.valueOf(AppUtil.getPropertity("ThreadPool.size.parse")));
		executorSendMessage = Executors.newFixedThreadPool(Integer.valueOf(AppUtil.getPropertity("ThreadPool.size.messageSend")));
	}

	public static void executorSendMessage(Runnable run) {
		executorSendMessage.execute(run);
	}
	public static void execute(Runnable run) {
		executor.execute(run);
	}
}
