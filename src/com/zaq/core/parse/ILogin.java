package com.zaq.core.parse;

import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.vo.AppUser;


public interface ILogin{
	public AppUser login(String packetStr)throws ZAQprotocolException;
}
