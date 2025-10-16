package com.easychat.entity.po;

import java.io.Serializable;

/**
 * @author 竝inellia
 * @Description: 会话用户
 * @date: 2025/10/12
 */
public class ChatSessionUser implements Serializable{

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 联系人ID
	 */
	private String contactId;

	/**
	 * 会话ID
	 */
	private String sessionId;

	/**
	 * 联系人名称
	 */
	private String contactName;

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getLastReceiveMessage() {
		return lastReceiveMessage;
	}

	public void setLastReceiveMessage(String lastReceiveMessage) {
		this.lastReceiveMessage = lastReceiveMessage;
	}

	private String lastMessage;

	private String lastReceiveMessage;

	private Integer memberCount;

	public Integer getContactType() {
		return contactType;
	}

	public void setContactType(Integer contactType) {
		this.contactType = contactType;
	}

	private Integer contactType;

	public Long getLastReceiveTime() {
		return lastReceiveTime;
	}

	public void setLastReceiveTime(Long lastReceiveTime) {
		this.lastReceiveTime = lastReceiveTime;
	}

	private Long lastReceiveTime;

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

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

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactName() {
		return this.contactName;
	}

	@Override
	public String toString() {
		return "用户ID:" + (userId == null ? "空" : userId) + ",联系人ID:" + (contactId == null ? "空" : contactId) + ",会话ID:" + (sessionId == null ? "空" : sessionId) + ",联系人名称:" + (contactName == null ? "空" : contactName);
	}

}