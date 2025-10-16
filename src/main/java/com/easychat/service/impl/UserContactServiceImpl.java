package com.easychat.service.impl;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.enums.*;
import com.easychat.entity.po.*;
import com.easychat.entity.query.*;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.*;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactApplyService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUtils;
import com.easychat.websocket.MessageHandler;
import jodd.util.ArraysUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.easychat.service.UserContactService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 高98
 * @Description: 联系人对应的ServiceImpl
 * @date: 2025/08/25
 */

@Service("userContactService")
public class UserContactServiceImpl implements UserContactService{

	@Resource
	private UserContactMapper<UserContact,UserContactQuery> userContactMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;

	@Resource
	private RedisComponent redisComponent;
	@Resource
	private ChatSessionMapper<ChatSession,ChatSessionQuery> chatSessionMapper;
	@Resource
	private ChatSessionUserMapper<ChatSessionUser,ChatSessionUserQuery> chatSessionUserMapper;
	@Resource
	private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
    @Resource
    private MessageHandler messageHandler;
	@Resource
	private ChannelContextUtils channelContextUtils;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserContact>findListByParam(UserContactQuery query){
		return this.userContactMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserContactQuery query){
		return this.userContactMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserContact> findListByPage(UserContactQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserContact> list = this.findListByParam(query);
		PaginationResultVO<UserContact> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(UserContact bean){
		return this.userContactMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserContact> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(UserContact userContact){
		return this.userContactMapper.insertOrUpdate(userContact);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserContact> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userContactMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据UserIdAndContactId查询
	 */
	@Override
	 public UserContact getByUserIdAndContactId(String userId,String contactId){
		return this.userContactMapper.selectByUserIdAndContactId(userId,contactId);
	 }
	/**
	 * 根据UserIdAndContactId更新
	 */
	@Override
	 public Integer updateByUserIdAndContactId(UserContact bean , String userId,String contactId){
		return this.userContactMapper.updateByUserIdAndContactId(bean,userId,contactId);
	 }
	/**
	 * 根据UserIdAndContactId删除
	 */
	@Override
	 public Integer deleteByUserIdAndContactId(String userId,String contactId){
		return this.userContactMapper.deleteByUserIdAndContactId(userId,contactId);
	 }

	@Override
	public Integer updateByParam(UserContact userContact, UserContactQuery userContactQuery) {
		return this.updateByParam(userContact, userContactQuery);
	}

	@Override
	public UserContactSearchResultDto searchContact(String userId, String contactId) {
		UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);//根据前缀确定是那种联系类型
		if(typeEnum==null){
			return null;
		}
		UserContactSearchResultDto resultDto = new UserContactSearchResultDto();
		switch (typeEnum){
			case USER:
				UserInfo userInfo = this.userInfoMapper.selectByUserId(contactId);
				if(userInfo==null){
					return null;
				}
				resultDto = CopyTools.copy(userInfo,UserContactSearchResultDto.class);
				break;
			case GROUP:
				GroupInfo groupInfo = this.groupInfoMapper.selectByGroupId(contactId);
				if(groupInfo==null){
					return null;
				}
				resultDto.setNickName(groupInfo.getGroupName());
				break;

		}
		resultDto.setContactType(typeEnum.toString());
		resultDto.setContactId(contactId);
		//如果查询的是自己
		if(userId.equals(contactId)){
			resultDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		}
		//查询是否是好友
		UserContact userContact = this.userContactMapper.selectByUserIdAndContactId(userId,contactId);
		resultDto.setStatus(userContact == null ? null : userContact.getStatus());

		return resultDto;
	}




	@Override
	public void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) throws BusinessException {
		//先去判断是否是群组
		if(UserContactTypeEnum.GROUP.getType().equals(contactType)){
			UserContactQuery userContactQuery = new UserContactQuery();
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			Integer count =  userContactMapper.selectCount(userContactQuery);

			SysSettingDto sysSettingDto = redisComponent.getSysSetting();
			if(count > sysSettingDto.getMaxGroupCount()){
				throw new BusinessException("该群聊成员已满，无法加入！");
			}
		}

		Date curDate = new Date();
		//同意，双方添加好友
		List<UserContact> contactList = new ArrayList<>();
		//申请人添加对方
		UserContact userContact = new UserContact();
		userContact.setUserId(applyUserId);
		userContact.setContactId(contactId);
		userContact.setContactType(contactType);
		userContact.setCreateTime(curDate);
		userContact.setLastUpdateTime(curDate);
		userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		contactList.add(userContact);
		//如果是申请好友，接收人添加申请人，群组不用添加对方为好友
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
			userContact = new UserContact();
			userContact.setUserId(applyUserId);
			userContact.setContactId(contactId);
			userContact.setContactType(contactType);
			userContact.setCreateTime(curDate);
			userContact.setLastUpdateTime(curDate);
			userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		}
		//批量插入
		userContactMapper.insertOrUpdate(contactList);

		//添加缓存
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
			redisComponent.addUserContact(receiveUserId,applyUserId);
		}
		redisComponent.addUserContact(applyUserId,contactId);//如果是好友，两个人都要互相存储
		//创建会话，发送消息

		String sessionId = null;
		List<ChatSessionUser> chatSessionUserList = new ArrayList<>();
		if(UserContactTypeEnum.USER.getType().equals(contactType)){
			sessionId = StringTools.getChatSessionId4User(new String[]{applyUserId,contactId});

			//创建会话
			ChatSession chatSession = new ChatSession();
			chatSession.setSessionId(sessionId);
			chatSession.setLastMessage(applyInfo);
			chatSession.setLastReceiveTime(curDate.getTime());

			this.chatSessionMapper.insertOrUpdate(chatSession);
			//创建申请人session

			ChatSessionUser applySessionUser = new ChatSessionUser();
			applySessionUser.setUserId(applyUserId);
			applySessionUser.setContactId(contactId);
			applySessionUser.setSessionId(sessionId);
			UserInfo contactUser = this.userInfoMapper.selectByUserId(contactId);
			applySessionUser.setContactName(contactUser.getNickName());
			chatSessionUserList.add(applySessionUser);

			//创建接收人session
			ChatSessionUser contactSessionUser = new ChatSessionUser();
			contactSessionUser.setUserId(contactId);
			contactSessionUser.setContactId(applyUserId);
			contactSessionUser.setSessionId(sessionId);
			UserInfo applyUserInfo = this.userInfoMapper.selectByUserId(applyUserId);
			contactSessionUser.setContactName(applyUserInfo.getNickName());
			this.chatSessionUserMapper.insertOrUpdateBatch(chatSessionUserList);
			//记录消息表
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setSessionId(sessionId);
			chatMessage.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
			chatMessage.setMessageContent(applyInfo);
			chatMessage.setSendUserId(applyUserId);
			chatMessage.setSendUserNickName(applyUserInfo.getNickName());
			chatMessage.setSendTime(curDate.getTime());
			chatMessage.setContactId(contactId);
			chatMessage.setContactType(UserContactTypeEnum.USER.getType());
			chatMessageMapper.insert(chatMessage);

			MessageSendDto messageSendDto = CopyTools.copy(chatMessage,MessageSendDto.class);
			//发送给申请还有接受的人
			messageHandler.sendMessage(messageSendDto);

			messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND_SELF.getType());
			messageSendDto.setContactId(applyUserId);
			messageSendDto.setExtendData(contactUser);
			messageHandler.sendMessage(messageSendDto);
		}else{
			sessionId = StringTools.getChatSession4Group(contactId);

			ChatSessionUser chatSessionUser = new ChatSessionUser();
			chatSessionUser.setUserId(applyUserId);
			chatSessionUser.setContactId(contactId);
			GroupInfo groupInfo = this.groupInfoMapper.selectByGroupId(contactId);
			chatSessionUser.setContactName(groupInfo.getGroupName());
			chatSessionUser.setSessionId(sessionId);
			this.chatSessionUserMapper.insertOrUpdate(chatSessionUser);



			UserInfo applyUSerInfo = this.userInfoMapper.selectByUserId(applyUserId);
			String sendMessage = String.format(MessageTypeEnum.ADD_GROUP.getInitMessage(),applyUSerInfo.getNickName());

			//增加session信息
			ChatSession chatSession = new ChatSession();
			chatSession.setSessionId(sessionId);
			chatSession.setLastReceiveTime(curDate.getTime());
			chatSession.setLastMessage(sendMessage);
			//增加聊天消息
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setSessionId(sessionId);
			chatMessage.setMessageType(MessageTypeEnum.ADD_GROUP.getType());
			chatMessage.setMessageContent(sendMessage);
			chatMessage.setSendTime(curDate.getTime());
			chatMessage.setContactId(contactId);
			chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
			chatMessage.setStatus(MessageSatusEnum.SENDED.getStatus());
			this.chatMessageMapper.insert(chatMessage);

			redisComponent.addUserContact(applyUserId,contactId);
			channelContextUtils.addUser2Group(applyUserId,groupInfo.getGroupId());


			//发送消息
			MessageSendDto messageSendDto  = CopyTools.copy(chatMessage,MessageSendDto.class);
			messageSendDto.setContactId(contactId);
			//获取群人数
			UserContactQuery userContactQuery = new UserContactQuery();
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			Integer memberCount = this.userContactMapper.selectCount(userContactQuery);
			messageSendDto.setMemberCount(memberCount);
			messageSendDto.setContactName(groupInfo.getGroupName());

			messageHandler.sendMessage(messageSendDto);

		}//对于不同的类型，生成不同的sessionId


	}

	@Override
	public void removeUserContact(String userId, String contactId, UserContactStatusEnum statusEnum) {
		//移除好友
		UserContact userContact = new UserContact();
		userContact.setStatus(statusEnum.getStatus());
		userContactMapper.updateByUserIdAndContactId(userContact,userId,contactId);

		//将好友中的也移除自己
		UserContact friendContact = new UserContact();
		if(UserContactStatusEnum.DEL == statusEnum){
			friendContact.setStatus(UserContactStatusEnum.DEL_BE.getStatus());
		}else if(UserContactStatusEnum.BLACKLIST == statusEnum){
			friendContact.setStatus(UserContactStatusEnum.BLACKLIST_BE.getStatus());
		}

		userContactMapper.updateByUserIdAndContactId(friendContact,contactId,userId);
		//TODO 从我的好友列表缓存中删除好友
		//TODO 从好友列表缓存删除我
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addContact4Robot(String userId) {
		Date curDate = new Date();
		SysSettingDto sysSettingDto = redisComponent.getSysSetting();
		String contactId = sysSettingDto.getRobotUid();
		String contactName = sysSettingDto.getRobotNickName();
		String sendMessage = sysSettingDto.getRobotWelcome();
		sendMessage = StringTools.cleanHtmlTag(sendMessage);
		//增加机器人好友
		UserContact userContact = new UserContact();
		userContact.setUserId(userId);
		userContact.setContactId(contactId);
		userContact.setContactType(UserContactTypeEnum.USER.getType());
		userContact.setCreateTime(curDate);
		userContact.setLastUpdateTime(curDate);
		userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		userContactMapper.insert(userContact);
		//增加会话信息
		String sessionId = StringTools.getChatSessionId4User(new String[]{userId,contactId});
		ChatSession chatSession = new ChatSession();
		chatSession.setLastMessage(sendMessage);
		chatSession.setSessionId(sessionId);
		chatSession.setLastReceiveTime(curDate.getTime());
		this.chatSessionMapper.insert(chatSession);
		//增加会话人信息
		ChatSessionUser chatSessionUser = new ChatSessionUser();
		chatSessionUser.setUserId(userId);
		chatSessionUser.setContactId(contactId);
		chatSessionUser.setContactName(contactName);
		chatSessionUser.setSessionId(sessionId);
		this.chatSessionUserMapper.insert(chatSessionUser);

		//增加聊天消息
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setSessionId(sessionId);
		chatMessage.setMessageType(MessageTypeEnum.CHAT.getType());
		chatMessage.setMessageContent(sendMessage);
		chatMessage.setSendUserId(contactId);
		chatMessage.setSendUserNickName(contactName);
		chatMessage.setSendTime(curDate.getTime());
		chatMessage.setContactId(contactId);
		chatMessage.setContactType(UserContactTypeEnum.USER.getType());
		chatMessage.setStatus(MessageSatusEnum.SENDED.getStatus());
		this.chatMessageMapper.insert(chatMessage);
	}


}