package com.zaq.core.vo;
/**
 * 公司信息
 * @author zaq
 *
 */
public class Company {
	public static final int YH=1;
	private Long companyId;
	private String name;
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
