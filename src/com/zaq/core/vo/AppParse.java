package com.zaq.core.vo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
/**
 * 解析bean
 * @author zaq
 *
 */
public class AppParse {
	protected Long parseId;
	protected String clazz;
	protected String method;//待用
	protected AppFunction appFunction;

	public AppParse() {
	}

	public AppParse(String clazz, String method) {
		this.clazz = clazz;
		this.method = method;
	}

	public AppParse(Long parseId) {
		this.parseId = parseId;
	}

	public AppFunction getAppFunction() {
		return this.appFunction;
	}

	public void setAppFunction(AppFunction in_appFunction) {
		this.appFunction = in_appFunction;
	}

	public Long getFunctionId() {
		return getAppFunction() == null ? null : getAppFunction().getFunctionId();
	}

	public void setFunctionId(Long aValue) {
		if (aValue == null) {
			this.appFunction = null;
		} else if (this.appFunction == null) {
			this.appFunction = new AppFunction(aValue);
		} else {
			this.appFunction.setFunctionId(aValue);
		}
	}

	public boolean equals(Object object) {
		if (!(object instanceof AppParse)) {
			return false;
		}
		AppParse rhs = (AppParse) object;
		return new EqualsBuilder().append(this.parseId, rhs.parseId).append(this.clazz, rhs.clazz).append(this.method, rhs.method).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(-82280557, -700257973).append(this.parseId).append(this.clazz).append(this.method).toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this).append("parseId", this.parseId).append("clazz", this.clazz).append("method", this.method).toString();
	}

	public Long getParseId() {
		return parseId;
	}

	public void setParseId(Long parseId) {
		this.parseId = parseId;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
