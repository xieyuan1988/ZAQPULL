package com.zaq.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.zaq.core.common.Constants;
import com.zaq.core.common.MyProtocalCodecFactory;
import com.zaq.core.common.ServerHandler;
import com.zaq.core.util.AppUtil;
/**
 * scoket推送监听器
 * @author zaq
 *
 */
public class MessagePullSocketListener{
	private static Logger logger=Logger.getLogger(MessagePullSocketListener.class);
    private static MessagePullSocketListener mainServer = null;
    private SocketAcceptor acceptor = new NioSocketAcceptor();
    private DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

    public static MessagePullSocketListener getInstances() {
        if (null == mainServer) {
            mainServer = new MessagePullSocketListener();
        }
        return mainServer;
    }

    private MessagePullSocketListener() {}
    
    public void init(){

        chain.addLast("codec", new ProtocolCodecFilter(
                new MyProtocalCodecFactory(Charset.forName(Constants.SERVER_ENCODE))));
        
        // 定义SLF4J 日志级别 Look: http://riddickbryant.iteye.com/blog/564330  
//        LoggingFilter loggingFilter = new LoggingFilter();  
//        loggingFilter.setSessionCreatedLogLevel(LogLevel.NONE);// 一个新的session被创建时触发  
//        loggingFilter.setSessionOpenedLogLevel(LogLevel.NONE);// 一个新的session打开时触发  
//        loggingFilter.setSessionClosedLogLevel(LogLevel.NONE);// 一个session被关闭时触发  
//        loggingFilter.setMessageReceivedLogLevel(LogLevel.NONE);// 接收到数据时触发  
//        loggingFilter.setMessageSentLogLevel(LogLevel.NONE);// 数据被发送后触发  
//        loggingFilter.setSessionIdleLogLevel(LogLevel.INFO);// 一个session空闲了一定时间后触发  
//        loggingFilter.setExceptionCaughtLogLevel(LogLevel.INFO);// 当有异常抛出时触发  
//        chain.addLast("logger", loggingFilter);  
        
        // 连接池设置  
        // get a reference to the filter chain from the acceptor  
        chain.addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
        
        
        // 设置数据将被读取的缓冲区大小  
        acceptor.getSessionConfig().setReadBufferSize(1024*Integer.valueOf(AppUtil.getPropertity("byteBuffer.allocate")));  
        // 10秒内没有读写就设置为空闲通道  
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Integer.valueOf(AppUtil.getPropertity("IdleStatus.BOTH_IDLE")));  

        acceptor.setHandler(ServerHandler.getInstances());
        
        try {
            acceptor.bind(new InetSocketAddress(Integer.valueOf(AppUtil.getPropertity("app.pullSocketPort"))));
        } catch (IOException e) {
            logger.error("系统异常退出："+e.getMessage(), e);
            System.exit(0);
        }
        
        logger.info("服务开启成功。。。。端口号:"+AppUtil.getPropertity("app.pullSocketPort"));
    
    }

}