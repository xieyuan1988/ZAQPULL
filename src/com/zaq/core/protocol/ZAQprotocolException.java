package com.zaq.core.protocol;

import org.apache.log4j.Logger;

import com.zaq.core.common.Constants;

/**
 * 我的协议异常处理类
 * @author zyj
 *
 */
public class ZAQprotocolException  extends Exception {
	private static Logger logger=Logger.getLogger(ZAQprotocolException.class);
	private static final long serialVersionUID = -7495476589370015758L;

	private Short state;
	
	public ZAQprotocolException(Short state) {
        super();
        this.state=state;
    }
	public ZAQprotocolException(String msg) {
        super(msg);
        this.state=Constants.STATE_ERROR;
        logger.info(msg, this);
    }
    public ZAQprotocolException(String msg,Short state) {
        super(msg);
        this.state=state;
        logger.info(msg, this);
    }
    
    public ZAQprotocolException(String msg,Throwable e,Short state) {
        super(msg+e.getMessage(),e);
        this.state=state;;
        logger.info(msg, this);
    }

	public Short getState() {
		return state;
	}

	public void setState(Short state) {
		this.state = state;
	}
}