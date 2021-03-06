 package com.zaq.core.vo;
 
 import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
 /**
  * 权限bean
  * @author zaq
  *
  */
 public class AppFunction 
 {
   protected Long functionId;
   protected String funKey;
   protected String funName;
   protected Set<AppParse> appParses = new HashSet();
 
   public AppFunction()
   {
   }
 
   public AppFunction(String funKey, String funName)
   {
     this.funKey = funKey;
     this.funName = funName;
   }
 
   public Set<AppParse> getAppParses() {
	return appParses;
	}
	
	public void setAppParses(Set<AppParse> appParses) {
		this.appParses = appParses;
	}

public AppFunction(Long in_functionId)
   {
     setFunctionId(in_functionId);
   }
 
   public Long getFunctionId()
   {
     return this.functionId;
   }
 
   public void setFunctionId(Long aValue)
   {
     this.functionId = aValue;
   }
 
   public String getFunKey()
   {
     return this.funKey;
   }
 
   public void setFunKey(String aValue)
   {
     this.funKey = aValue;
   }
 
   public String getFunName()
   {
     return this.funName;
   }
 
   public void setFunName(String aValue)
   {
     this.funName = aValue;
   }
 
   public boolean equals(Object object)
   {
     if (!(object instanceof AppFunction)) {
       return false;
     }
     AppFunction rhs = (AppFunction)object;
     return new EqualsBuilder()
       .append(this.functionId, rhs.functionId)
       .append(this.funKey, rhs.funKey)
       .append(this.funName, rhs.funName)
       .isEquals();
   }
 
   public int hashCode()
   {
     return new HashCodeBuilder(-82280557, -700257973)
       .append(this.functionId)
       .append(this.funKey)
       .append(this.funName)
       .toHashCode();
   }
 
   public String toString()
   {
     return new ToStringBuilder(this)
       .append("functionId", this.functionId)
       .append("funKey", this.funKey)
       .append("funName", this.funName)
       .toString();
   }
 }

