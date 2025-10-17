package com.easychat.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.entity.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Pinellia
 * @Description: 联系人
 * @date: 2025/08/25
 */
public class UserContact implements Serializable{

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 联系人ID或者群组ID
	 */
	private String contactId;

	/**
	 * 联系人类型 0:好友 1:群组
	 */
	private Integer contactType;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm::ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm::ss")
	private Date createTime;

	/**
	 * 状态 0:非好友 1:好友 2:已删除 3:拉黑
	 */
	@JsonIgnore
	private Integer status;

	/**
	 * 最后更新时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm::ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm::ss")
	private Date lastUpdateTime;

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	private String contactName;

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	private Integer sex;


	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactId() {
		return this.contactId;
	}

	public void setContactType(Integer contactType) {
		this.contactType = contactType;
	}

	public Integer getContactType() {
		return this.contactType;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	@Override
	public String toString() {
		return "用户ID:" + (userId == null ? "空" : userId) + ",联系人ID或者群组ID:" + (contactId == null ? "空" : contactId) + ",联系人类型 0:好友 1:群组:" + (contactType == null ? "空" : contactType) + ",创建时间:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",状态 0:非好友 1:好友 2:已删除 3:拉黑:" + (status == null ? "空" : status) + ",最后更新时间:" + (lastUpdateTime == null ? "空" : DateUtils.format(lastUpdateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}

}