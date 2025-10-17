package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.enums.*;
import com.easychat.entity.po.*;
import com.easychat.entity.query.*;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.*;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionService;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUtils;
import com.easychat.websocket.MessageHandler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.easychat.service.GroupInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Pinellia
 * @Description: 对应的ServiceImpl
 * @date: 2025/08/25
 */

@Service("groupInfoService")
public class GroupInfoServiceImpl implements GroupInfoService{

	@Resource
	private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;

	@Resource
	private RedisComponent redisComponent;
	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
	@Resource
	private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
	@Resource
	private ChatSessionUserMapper<ChatSessionUser,ChatSessionUserQuery> chatSessionUserMapper;
	@Resource
	private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
	@Resource
	private MessageHandler messageHandler;

	@Resource
	private AppConfig appConfig;
	@Resource
	private ChannelContextUtils channelContextUtils;
	@Resource
	private ChatSessionUserService chatSessionUserService;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<GroupInfo>findListByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<GroupInfo> list = this.findListByParam(query);
		PaginationResultVO<GroupInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(GroupInfo bean){
		return this.groupInfoMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<GroupInfo> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(GroupInfo groupInfo){
		return this.groupInfoMapper.insertOrUpdate(groupInfo);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<GroupInfo> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据GroupId查询
	 */
	@Override
	 public GroupInfo getByGroupId(String groupId){
		return this.groupInfoMapper.selectByGroupId(groupId);
	 }
	/**
	 * 根据GroupId更新
	 */
	@Override
	 public Integer updateByGroupId(GroupInfo bean , String groupId){
		return this.groupInfoMapper.updateByGroupId(bean,groupId);
	 }
	/**
	 * 根据GroupId删除
	 */
	@Override
	 public Integer deleteByGroupId(String groupId){
		return this.groupInfoMapper.deleteByGroupId(groupId);
	 }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws BusinessException, IOException {
		Date curDate = new Date();

		if(StringTools.isEmpty(groupInfo.getGroupId())){
			GroupInfoQuery query=new GroupInfoQuery();
			query.setGroupOwnerId(groupInfo.getGroupId());
			//对于群组的个数进行判断
			Integer count = this.groupInfoMapper.selectCount(query);
			SysSettingDto sysSettingDto = redisComponent.getSysSetting();
			if(count >= sysSettingDto.getMaxGroupCount()){
				throw new BusinessException("最多只能支持创建" + sysSettingDto.getMaxGroupCount() + "个群聊!");
			}
			//头像不能为空
			if(avatarFile == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}

			groupInfo.setCreateTime(curDate);
			groupInfo.setGroupId(StringTools.getGroupId());
			this.groupInfoMapper.insert(groupInfo);
			//将群组添加为联系人
			UserContact userContact = new UserContact();
			userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			userContact.setContactType(UserContactTypeEnum.GROUP.getType());
			userContact.setContactId(groupInfo.getGroupId());
			userContact.setUserId(groupInfo.getGroupOwnerId());
			userContact.setCreateTime(curDate);
			userContact.setLastUpdateTime(curDate);
			this.userContactMapper.insert(userContact);

			// 创建会话
			String sessionId = StringTools.getChatSession4Group(groupInfo.getGroupId());
			ChatSession chatSession = new ChatSession();
			chatSession.setSessionId(sessionId);
			chatSession.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
			chatSession.setLastReceiveTime(curDate.getTime());
			this.chatSessionMapper.insert(chatSession);


			ChatSessionUser chatSessionUser = new ChatSessionUser();
			chatSessionUser.setUserId(groupInfo.getGroupOwnerId());
			chatSessionUser.setContactId(groupInfo.getGroupId());
			chatSessionUser.setContactName(groupInfo.getGroupName());
			chatSessionUser.setSessionId(sessionId);
			this.chatSessionUserMapper.insert(chatSessionUser);

			//创建消息
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setSessionId(sessionId);
			chatMessage.setMessageType(MessageTypeEnum.GROUP_CREATE.getType());
			chatMessage.setMessageContent(MessageTypeEnum.GROUP_CREATE.getInitMessage());
			chatMessage.setSendTime(curDate.getTime());
			chatMessage.setContactId(groupInfo.getGroupId());
			chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
			chatMessage.setStatus(MessageSatusEnum.SENDED.getStatus());
			chatMessageMapper.insert(chatMessage);

			//将群组添加到联系人
			redisComponent.addUserContact(groupInfo.getGroupId(), groupInfo.getGroupOwnerId());
			//将联系人的通道添加到群组通道
			channelContextUtils.addUser2Group(groupInfo.getGroupOwnerId(), groupInfo.getGroupId());
			//发送ws消息
			chatSessionUser.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
			chatSessionUser.setLastReceiveTime(curDate.getTime());
			chatSessionUser.setMemberCount(1);//也就是自己

			MessageSendDto messageSendDto  = CopyTools.copy(chatMessage, MessageSendDto.class);
			messageSendDto.setExtendData(chatSessionUser);
			messageSendDto.setLastMessage(chatSession.getLastMessage());

			messageHandler.sendMessage(messageSendDto);


		}else{
			GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
			if(!dbInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())){//必须要判断群组是否属于同一个人，防止有人直接走接口
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			this.groupInfoMapper.updateByGroupId(groupInfo,groupInfo.getGroupId());
			// 更新相关表冗余信息
			String contactNameUpdate = null;
			if(!dbInfo.getGroupName().equals(groupInfo.getGroupName())){
				contactNameUpdate = groupInfo.getGroupName();
			}
			if(contactNameUpdate == null){
				return;
			}

			chatSessionUserService.updateRedundantInfo(contactNameUpdate,groupInfo.getGroupId());


		}
		if(avatarFile == null){
			return;
		}
		String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
		File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
		if(!targetFileFolder.exists()){
			targetFileFolder.mkdirs();//不存在就去创建
		}
		String filename = targetFileFolder.getPath() + "/" + groupInfo.getGroupId() + Constants.IMAGE_SUFFIX;
		avatarFile.transferTo(new File(filename));
		avatarCover.transferTo(new File(filename + Constants.COVER_IMAGE_SUFFIX));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dissolutionGroup(String groupOwnerId, String groupId) throws BusinessException {
		GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupId);
		if(dbInfo == null || !dbInfo.getGroupOwnerId().equals(groupOwnerId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		//删除群组
		GroupInfo updateGroupInfo = new GroupInfo();
		updateGroupInfo.setStatus(GroupStatusEnum.DISSOLUTION.getStatus());
		this.groupInfoMapper.updateByGroupId(updateGroupInfo,groupId);
		//更新联系人信息
		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setContactId(groupId);
		userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType());

		UserContact updateuserContact = new UserContact();
		updateuserContact.setStatus(UserContactStatusEnum.DEL.getStatus());
		this.userContactMapper.updateByParam(updateuserContact,userContactQuery);

		//TODO 移除相关群员的联系人缓存

		//TODO 更新会话信息，记录群消息，发送解散群消息
	}

}