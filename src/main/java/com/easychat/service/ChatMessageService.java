package com.easychat.service;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;

import java.util.List;
/**
 * @author Pinellia
 * @Description: 聊天消息表对应的Service
 * @date: 2025/10/12
 */

public interface ChatMessageService{

	/**
	 * 根据条件查询列表
	 */
	List<ChatMessage>findListByParam(ChatMessageQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(ChatMessageQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatMessage> findListByPage(ChatMessageQuery query );

	/**
	 * 新增
	 */
	Integer add(ChatMessage bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatMessage> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(ChatMessage bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ChatMessage> listBean);
	/**
	 * 根据MessageId查询
	 */
	 ChatMessage getByMessageId(Long messageId);

	/**
	 * 根据MessageId查询
	 */
	 Integer updateByMessageId(ChatMessage bean , Long messageId);

	/**
	 * 根据MessageId删除
	 */
	 Integer deleteByMessageId(Long messageId);

	 MessageSendDto saveMessage(ChatMessage message, TokenUserInfoDto tokenUserInfoDto) throws BusinessException;
}