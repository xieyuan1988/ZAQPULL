package com.zaq.core.parse;

import java.lang.reflect.Type;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;

import com.google.gson.JsonParseException;
import com.zaq.core.common.Constants;
import com.zaq.core.protocol.BasePacket;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.TypeUtil;
/**
 * 处理分析器基类
 * @author zaq
 *
 * @param <T>
 */
public abstract class BaseJsonParse<T extends BasePacket> extends Iparse{
	protected Type type;//=new TypeToken<T>(){}.getType();
	protected T packet; 
//	public BaseJsonParse(){}

	public BaseJsonParse(IoSession ioSession,String jsonPacketStr){
		super(ioSession, jsonPacketStr);
	}	
	@Override
	public void initParse() throws ZAQprotocolException {}
	@Override
	public final void beforeParse() throws ZAQprotocolException{
//		type=((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if(null==type){
			type=TypeUtil.getSuperclassTypeParameter(this.getClass());
		}

		if(StringUtils.isEmpty(packetVal)){
			throw new ZAQprotocolException("请在创建运行"+this.getClass()+"前调用 setJsonVal(String jsonVal)方法",Constants.STATE_UNFOUND);
		}
		try {
			packet= INGson.fromJson(packetVal,type);
		} catch (Exception e) {
			throw new ZAQprotocolException(e.getMessage(),e,Constants.STATE_ERROR);
		}
	};
	@Override
	public void sendTagNow() {
		msgTAG=packet.getMsgTAG();
		//发送接收成功的回执
		if(packet.getMsgTAG()!=0){
			ioSession.write(msgTAG);
		}
	}
	@Override
	public void afterParse(){};
}
