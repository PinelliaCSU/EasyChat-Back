package com.easychat.service.impl;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.enums.MessageTypeEnum;
import com.easychat.entity.enums.UserContactStatusEnum;
import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.mappers.ChatSessionUserMapper;
import com.easychat.mappers.UserContactMapper;
import com.easychat.websocket.MessageHandler;
import org.springframework.stereotype.Service;
import java.util.List;
import javax.annotation.Resource;
import com.easychat.entity.enums.PageSize;
import com.easychat.service.ChatSessionUserService;
/**
 * @author 竝inellia
 * @Description: 会话用户对应的ServiceImpl
 * @date: 2025/10/12
 */

@Service("chatSessionUserService")
public class ChatSessionUserServiceImpl implements ChatSessionUserService{

	@Resource
	private ChatSessionUserMapper<ChatSessionUser,ChatSessionUserQuery> chatSessionUserMapper;
	@Resource
	private MessageHandler messageHandler;
	@Resource
	private UserContactMapper<UserContact,UserContactQuery> userContactMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ChatSessionUser>findListByParam(ChatSessionUserQuery query){
		return this.chatSessionUserMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ChatSessionUserQuery query){
		return this.chatSessionUserMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<ChatSessionUser> list = this.findListByParam(query);
		PaginationResultVO<ChatSessionUser> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(ChatSessionUser bean){
		return this.chatSessionUserMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ChatSessionUser> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionUserMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(ChatSessionUser chatSessionUser){
		return this.chatSessionUserMapper.insertOrUpdate(chatSessionUser);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ChatSessionUser> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionUserMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据UserIdAndContactId查询
	 */
	@Override
	 public ChatSessionUser getByUserIdAndContactId(String userId,String contactId){
		return this.chatSessionUserMapper.selectByUserIdAndContactId(userId,contactId);
	 }
	/**
	 * 根据UserIdAndContactId更新
	 */
	@Override
	 public Integer updateByUserIdAndContactId(ChatSessionUser bean , String userId,String contactId){
		return this.chatSessionUserMapper.updateByUserIdAndContactId(bean,userId,contactId);
	 }
	/**
	 * 根据UserIdAndContactId删除
	 */
	@Override
	 public Integer deleteByUserIdAndContactId(String userId,String contactId){
		return this.chatSessionUserMapper.deleteByUserIdAndContactId(userId,contactId);
	 }

	@Override
	public Integer updateByParam(ChatSessionUser bean, ChatSessionUserQuery query) {
		return this.updateByParam(bean,query);
	}

	@Override
	public void updateRedundantInfo(String contactName,String contactId){
		ChatSessionUser updateInfo = new ChatSessionUser();
		updateInfo.setContactName(contactName);

		ChatSessionUserQuery chatSessionUserQuery = new ChatSessionUserQuery();
		chatSessionUserQuery.setContactId(contactId);
		this.chatSessionUserMapper.updateByParam(updateInfo,chatSessionUserQuery);//自己添加的方法，也许会出错

		UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
		//需要注意的是，群组与用户修改昵称时不一样
		if(contactTypeEnum == UserContactTypeEnum.GROUP){
			//修改群昵称，发送ws消息，为了实时更新
			MessageSendDto messageSendDto = new MessageSendDto();
			messageSendDto.setContactType(contactTypeEnum.getType());//根据前缀找到类型
			messageSendDto.setContactId(contactId);
			messageSendDto.setExtendData(contactName);
			messageSendDto.setMessageType(MessageTypeEnum.CONTACT_NAME_UPDATE.getType());
			messageHandler.sendMessage(messageSendDto);
		}else{
			UserContactQuery userContactQuery = new UserContactQuery();
			userContactQuery.setContactType(contactTypeEnum.getType());
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			List<UserContact> userContactList = userContactMapper.selectList(userContactQuery);//找到这个人的所有好友。每一个都需要发
			for(UserContact userContact:userContactList){
				MessageSendDto messageSendDto = new MessageSendDto();
				messageSendDto.setContactType(contactTypeEnum.getType());//根据前缀找到类型
				messageSendDto.setContactId(userContact.getUserId());
				messageSendDto.setExtendData(contactName);
				messageSendDto.setMessageType(MessageTypeEnum.CONTACT_NAME_UPDATE.getType());
				messageSendDto.setSendUserId(contactId);
				messageSendDto.setSendUserNickName(contactName);
				messageHandler.sendMessage(messageSendDto);
			}
		}
	}
}