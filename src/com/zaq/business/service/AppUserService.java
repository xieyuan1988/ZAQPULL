package com.zaq.business.service;

import java.security.MessageDigest;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.zaq.core.common.Constants;
import com.zaq.core.protocol.ZAQprotocolException;
import com.zaq.core.util.Base64;
import com.zaq.core.vo.AppUser;
/**
 * 系统用户service
 * @author zaq
 *
 */
public class AppUserService extends BaseService<AppUser>{
	/**
	 * 登陆 method
	 * @param userName
	 * @param password
	 * @param companyId 所属公司的ID 和帐号名 关联为唯一键
	 * @return
	 */
	public AppUser login(String userName, String password,Long companyId) throws ZAQprotocolException{
		if(StringUtils.isEmpty(userName)||StringUtils.isEmpty(password)||null==companyId){
			throw new ZAQprotocolException("企业号或用户名或密码不能为空", Constants.STATE_LOGINERROR);
		}
		
		String sql="select * from app_user au where au.username=? and companyId=?";  
	    AppUser appUser;
		try {
			appUser = baseDao.queryForObject(AppUser.class,sql, new Object[]{userName,companyId});
		} catch (SQLException e) {
			throw new ZAQprotocolException("系统异常："+e.getMessage(), Constants.STATE_ERROR);
		}          
		
		
		if (null==appUser) {
			throw new ZAQprotocolException("用户名或密码错误", Constants.STATE_LOGINERROR);
		}
		
		String newPassword = encryptSha256(password);

//		if (!appUser.getPassword().equalsIgnoreCase(
//				newPassword)) {
//			throw new ZAQprotocolException("用户名或密码错误", Constants.STATE_LOGINERROR);
//		}
		
		if (appUser.getStatus().shortValue() != 1
				&& appUser.getStatus().shortValue() != 3)
			throw new ZAQprotocolException("用户已禁用或已注销", Constants.STATE_LOGINERROR);
		
		return appUser;
	}
	
	private static synchronized String encryptSha256(String inputStr) {
	     try {
	         MessageDigest md = MessageDigest.getInstance("SHA-256");
	   
	         byte[] digest = md.digest(inputStr.getBytes("UTF-8"));
	   
	         return new String(Base64.encodeBytes(digest));
	       }
	       catch (Exception e)
	       {
	       }
	       return null;
	     }
	
	public static void main(String[] args) {
		System.out.println(encryptSha256("123456"));
	}
}
