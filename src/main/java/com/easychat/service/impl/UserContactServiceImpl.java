package com.easychat.service.impl;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.enums.*;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.*;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.GroupInfoMapper;
import com.easychat.mappers.UserContactApplyMapper;
import com.easychat.mappers.UserContactMapper;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactApplyService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import jodd.util.ArraysUtil;
import org.springframework.stereotype.Service;

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
	private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;
	@Resource
	private RedisComponent redisComponent;
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
	@Transactional(rollbackFor = Exception.class)
	public Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo) throws BusinessException {
		UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
		if(typeEnum==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		//申请人
		String applyUserId = tokenUserInfoDto.getUserId();
		//默认申请信息
		applyInfo = StringTools.isEmpty(applyInfo)? String.format(Constants.APPLY_INFO_TEMPLATE,tokenUserInfoDto.getNickName()) :applyInfo;

		Long curTime = System.currentTimeMillis();

		Integer joinType = null;
		String receiveUserId = contactId;

		//查询对方好友是否添加，如果已拉黑则无法被添加
		UserContact userContact = userContactMapper.selectByUserIdAndContactId(applyUserId,contactId);
		if(userContact != null &&
				ArraysUtil.contains(new Integer[]{UserContactStatusEnum.BLACKLIST_BE.getStatus(),
						UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus()},userContact.getStatus())){
			throw new BusinessException("对方已将你拉黑，无法添加");
		}
		if(UserContactTypeEnum.GROUP == typeEnum){
			GroupInfo groupInfo = this.groupInfoMapper.selectByGroupId(contactId);
			if(groupInfo==null || GroupStatusEnum.DISSOLUTION.getStatus().equals(groupInfo.getStatus())){
				throw new BusinessException("群聊已被解散或不存在");
			}

			receiveUserId = groupInfo.getGroupOwnerId();
			joinType = groupInfo.getJoinType();
		}else {
			UserInfo userInfo = this.userInfoMapper.selectByUserId(contactId);
			if(userInfo == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			joinType = userInfo.getJoinType();
		}

		//直接加入不用记录申请记录
		if(JoinTypeEnum.JOIN.getType().equals(joinType)){
			this.addContact(applyUserId,receiveUserId,contactId,typeEnum.getType(),applyInfo);//添加联系人
			return joinType;
		}

		UserContactApply dbApply = this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
		if(dbApply==null){
			UserContactApply contactApply = new UserContactApply();
			contactApply.setApplyUserId(applyUserId);
			contactApply.setContactType(typeEnum.getType());
			contactApply.setReceiveUserId(receiveUserId);
			contactApply.setContactId(contactId);
			contactApply.setLastApplyTime(curTime);
			contactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			contactApply.setApplyInfo(applyInfo);
			this.userContactApplyMapper.insert(contactApply);
		}else{
			//更新状态
			UserContactApply contactApply = new UserContactApply();
			contactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
			contactApply.setLastApplyTime(curTime);
			contactApply.setApplyInfo(applyInfo);
			this.userContactApplyMapper.updateByApplyId(contactApply,dbApply.getApplyId());
		}

		if(dbApply == null || !UserContactApplyStatusEnum.INIT.getStatus().equals(dbApply.getStatus())){
			//TODO 发送ws消息
		}

		return joinType;
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

		//TODO 添加缓存

		//TODO 创建会话，发送消息
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




}