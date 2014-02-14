package com.zaq.business.service;

import org.apache.log4j.Logger;

import com.zaq.business.dao.BaseDao;

/**
 * 
 * @author 业务基类
 *
 * @param <T>
 */
public abstract class BaseService<T> {
	protected BaseDao baseDao=BaseDao.getInstance();  
}
