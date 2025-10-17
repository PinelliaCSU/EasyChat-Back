package com.easychat.service.impl;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.enums.ResponseCodeEnum;
import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.ChatMessageMapper;
import com.easychat.redis.RedisComponent;
import org.springframework.stereotype.Service;
import java.util.List;
import javax.annotation.Resource;
import com.easychat.entity.enums.PageSize;
import com.easychat.service.ChatMessageService;
/**
 * @author 竝inellia
 * @Description: 聊天消息表对应的ServiceImpl
 * @date: 2025/10/12
 */

@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService{

	@Resource
	private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
	@Resource
	private RedisComponent redisComponent;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ChatMessage>findListByParam(ChatMessageQuery query){
		return this.chatMessageMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ChatMessageQuery query){
		return this.chatMessageMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<ChatMessage> findListByPage(ChatMessageQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<ChatMessage> list = this.findListByParam(query);
		PaginationResultVO<ChatMessage> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(ChatMessage bean){
		return this.chatMessageMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ChatMessage> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.chatMessageMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(ChatMessage chatMessage){
		return this.chatMessageMapper.insertOrUpdate(chatMessage);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ChatMessage> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.chatMessageMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据MessageId查询
	 */
	@Override
	 public ChatMessage getByMessageId(Long messageId){
		return this.chatMessageMapper.selectByMessageId(messageId);
	 }
	/**
	 * 根据MessageId更新
	 */
	@Override
	 public Integer updateByMessageId(ChatMessage bean , Long messageId){
		return this.chatMessageMapper.updateByMessageId(bean,messageId);
	 }
	/**
	 * 根据MessageId删除
	 */
	@Override
	 public Integer deleteByMessageId(Long messageId){
		return this.chatMessageMapper.deleteByMessageId(messageId);
	 }

	@Override
	public MessageSendDto saveMessage(ChatMessage chatmessage, TokenUserInfoDto tokenUserInfoDto) throws BusinessException {
		//不是机器人，判断好友状态
		if(!Constants.ROBOT_UID.equals(tokenUserInfoDto.getUserId())){
			List<String> contactList = redisComponent.getUserContactList(tokenUserInfoDto.getUserId());
			if(!contactList.contains(chatmessage.getContactId())){
				UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(chatmessage.getContactId());
				if(userContactTypeEnum.USER == userContactTypeEnum){
					throw new BusinessException(ResponseCodeEnum.CODE_902);//是好友，但是找不到
				}else{
					throw new BusinessException(ResponseCodeEnum.CODE_903);//是群组，但是找不到
				}
			}
		}


		return null;
	}

}