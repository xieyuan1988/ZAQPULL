package com.zaq.core.vo;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.annotations.Expose;
/**
 * 用户bean
 * @author zaq
 *
 */
public class AppUser {
	public static Long SYSTEM_USER = new Long(-1L);
	public static String SYSTEM_USER_FULLNAME = "系统";
	
	public static Short STATUUS_UNUSE=0;	//禁用
	public static Short STATUUS_USE=1;		//激活
	public static Short STATUUS_LEAVE=2;	//注销
	@Expose
	protected Long userId;					//推送服务器中用户的数字ID  ,客户端系统必须在注册的时候记录记忆该ID对应上自己系统中的用户
	@Expose
	private Long companyId;					//所属公司的ID 和帐号名 关联为唯一键
	@Expose
	protected String username;				//帐号名
	@Expose
	protected String password;

	@Expose
	protected String email;
//
//	@Expose
//	protected Department department;

	@Expose
	protected String position;

	@Expose
	protected String phone;
//
	@Expose
	protected String mobile;
//	
	@Expose
	protected String address;

	@Expose
	protected String photo;

	@Expose
	protected Short status;//  1=激活  0=禁用  2=离职 

	@Expose
	protected String fullname;

	@Expose
	protected Short delFlag;
	@Expose
	protected Date createTime;
	
//	private Set<AppRole> roles;
//	private Set<String> rights = new HashSet();

	protected transient Short loginState;//状态
	
//	public Set<String> getRights() {
//		return this.rights;
//	}
//
//	public String getFunctionRights() {
//		StringBuffer sb = new StringBuffer();
//
//		Iterator it = this.rights.iterator();
//
//		while (it.hasNext()) {
//			sb.append((String) it.next()).append(",");
//		}
//
//		if (this.rights.size() > 0) {
//			sb.deleteCharAt(sb.length() - 1);
//		}
//
//		return sb.toString();
//	}
//
//	public void setRights(Set<String> rights) {
//		this.rights = rights;
//	}

	public AppUser() {
	}

	public AppUser(Long in_userId) {
		setUserId(in_userId);
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long aValue) {
		this.userId = aValue;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String aValue) {
		this.username = aValue;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String aValue) {
		this.password = aValue;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String aValue) {
		this.email = aValue;
	}

//	public Department getDepartment() {
//		return this.department;
//	}
//
//	public void setDepartment(Department department) {
//		this.department = department;
//	}


	public String getPosition() {
		return this.position;
	}

	public void setPosition(String aValue) {
		this.position = aValue;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String aValue) {
		this.phone = aValue;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String aValue) {
		this.mobile = aValue;
	}


	public String getAddress() {
		return this.address;
	}

	public void setAddress(String aValue) {
		this.address = aValue;
	}

	public String getPhoto() {
		return this.photo;
	}

	public void setPhoto(String aValue) {
		this.photo = aValue;
	}


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short aValue) {
		this.status = aValue;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String aValue) {
		this.fullname = aValue;
	}

	public Short getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(Short delFlag) {
		this.delFlag = delFlag;
	}

	public String getFirstKeyColumnName() {
		return "userId";
	}
//
//	public Set<AppRole> getRoles() {
//		return this.roles;
//	}
//
//	public void setRoles(Set<AppRole> roles) {
//		this.roles = roles;
//	}
//       
	public boolean isEnabled() {
		return this.status.shortValue() == 1||this.status.shortValue() == 3;
	}

	public String getId() {
		return this.userId.toString();
	}

	public String getBusinessEmail() {
		return this.email;
	}

	public String getFamilyName() {
		return this.fullname;
	}

	public String getGivenName() {
		return this.fullname;
	}

	public Short getLoginState() {
		return loginState;
	}

	public void setLoginState(Short loginState) {
		this.loginState = loginState;
	}
	
	
	/**
	 * 重写hashCode和equals方法
	 */
	@Override
	public int hashCode() {
		return userId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return userId==((AppUser)obj).getUserId();
	}
	/**
	 * @return the companyId
	 */
	public Long getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
}
