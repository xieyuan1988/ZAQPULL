package com.zaq;

import java.io.IOException;

import com.zaq.core.MessagePullSocketListener;
import com.zaq.core.ZAQRouter;
import com.zaq.core.util.AppUtil;
/**
 * 应用启动入口
 * @author zaq
 *
 */
public class PullServerRun {
	public static void main(String[] args) throws IOException {
		AppUtil.init();
    	ZAQRouter.getRouter().init();
    	MessagePullSocketListener.getInstances().init();
	}
}
