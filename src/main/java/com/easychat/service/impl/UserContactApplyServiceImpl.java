package com.easychat.service.impl;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.enums.*;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.*;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.GroupInfoMapper;
import com.easychat.mappers.UserContactApplyMapper;
import com.easychat.mappers.UserContactMapper;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactService;
import com.easychat.utils.StringTools;
import com.easychat.websocket.MessageHandler;
import jodd.util.ArraysUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.easychat.service.UserContactApplyService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Pinellia
 * @Description: 联系人申请对应的ServiceImpl
 * @date: 2025/08/25
 */

@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService{

	@Resource
	private UserContactApplyMapper<UserContactApply,UserContactApplyQuery> userContactApplyMapper;

	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
	@Resource
	private UserContactService userContactService;
	@Resource
	private MessageHandler messageHandler;
	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserContactApply>findListByParam(UserContactApplyQuery query){
		return this.userContactApplyMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserContactApplyQuery query){
		return this.userContactApplyMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserContactApply> list = this.findListByParam(query);
		PaginationResultVO<UserContactApply> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(UserContactApply bean){
		return this.userContactApplyMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserContactApply> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(UserContactApply userContactApply){
		return this.userContactApplyMapper.insertOrUpdate(userContactApply);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserContactApply> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userContactApplyMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据ApplyId查询
	 */
	@Override
	 public UserContactApply getByApplyId(Integer applyId){
		return this.userContactApplyMapper.selectByApplyId(applyId);
	 }
	/**
	 * 根据ApplyId更新
	 */
	@Override
	 public Integer updateByApplyId(UserContactApply bean , Integer applyId){
		return this.userContactApplyMapper.updateByApplyId(bean,applyId);
	 }
	/**
	 * 根据ApplyId删除
	 */
	@Override
	 public Integer deleteByApplyId(Integer applyId){
		return this.userContactApplyMapper.deleteByApplyId(applyId);
	 }
	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询
	 */
	@Override
	 public UserContactApply getByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId){
		return this.userContactApplyMapper.selectByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
	 }
	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId更新
	 */
	@Override
	 public Integer updateByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean , String applyUserId,String receiveUserId,String contactId){
		return this.userContactApplyMapper.updateByApplyUserIdAndReceiveUserIdAndContactId(bean,applyUserId,receiveUserId,contactId);
	 }
	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	@Override
	 public Integer deleteByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId){
		return this.userContactApplyMapper.deleteByApplyUserIdAndReceiveUserIdAndContactId(applyUserId,receiveUserId,contactId);
	 }

	@Override
	public Integer updateByParam(UserContactApply t, UserContactApplyQuery p) {
		return this.userContactApplyMapper.updateByParam(t,p);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealWithApply(String userId, Integer applyId, Integer status) throws BusinessException {
		UserContactApplyStatusEnum statusEnum = UserContactApplyStatusEnum.getByStatus(status);
		if(statusEnum==null || UserContactApplyStatusEnum.INIT.getStatus() == status){
			throw  new BusinessException(ResponseCodeEnum.CODE_600);//不存在或者状态还是初始的，直接报出参数错误
		}
		UserContactApply applyInfo = this.userContactApplyMapper.selectByApplyId(applyId);
		if(applyInfo==null || !userId.equals(applyInfo.getReceiveUserId())){//一定要接收人与处理人一致，都是防止后台接口的错误漏洞
			throw  new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserContactApply updateInfo = new UserContactApply();
		updateInfo.setStatus(statusEnum.getStatus());
		updateInfo.setLastApplyTime(System.currentTimeMillis());

		//为了防止并发操作，也就是防止一直改来改去，只能由未处理的状态改为其他的状态
		UserContactApplyQuery applyQuery = new UserContactApplyQuery();
		applyQuery.setApplyId(applyId);
		applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());

		Integer count = userContactApplyMapper.updateByParam(updateInfo,applyQuery);
		if(count == 0){
			throw  new BusinessException(ResponseCodeEnum.CODE_600);
		}

//		TODO 不知道为什么缺少一个方法,自己补充了一个,可能会失败2025.8.29


		if(UserContactApplyStatusEnum.PASS.getStatus().equals(status)){
			userContactService.addContact(applyInfo.getApplyUserId(),applyInfo.getReceiveUserId(),applyInfo.getContactId(),applyInfo.getContactType(),applyInfo.getApplyInfo());
			return;
		}
		if(UserContactApplyStatusEnum.BLACKLIST.getStatus().equals(status)){
			Date curDate = new Date();
			UserContact userContact = new UserContact();
			userContact.setUserId(applyInfo.getApplyUserId());
			userContact.setContactId(applyInfo.getContactId());
			userContact.setContactType(applyInfo.getContactType());
			userContact.setCreateTime(curDate);
			userContact.setStatus(UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus());
			userContact.setLastUpdateTime(curDate);
			userContactMapper.insertOrUpdate(userContact);
			
		}
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
			userContactService.addContact(applyUserId,receiveUserId,contactId,typeEnum.getType(),applyInfo);//添加联系人
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
			// 发送ws消息,关键在于跨服务器时，消息的同步，不能只考虑只有一个服务器的情况
			//于是我们使用 redisson（就是把服务器连上redis（当然也可以很多，也是一个集群，不过是运维层面的事情），实现消息分发，每个服务器都选择分发一次）
			MessageSendDto messageSendDto = new MessageSendDto();
			messageSendDto.setMessageType(MessageTypeEnum.CONTACT_APPLY.getType());
			messageSendDto.setMessageContent(applyInfo);
			messageSendDto.setContactId(contactId);
			messageHandler.sendMessage(messageSendDto);//发送消息
		}

		return joinType;
	}


}