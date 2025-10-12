package com.easychat.service;

import com.easychat.entity.query.ChatSessionQuery;
import com.easychat.entity.po.ChatSession;
import com.easychat.entity.vo.PaginationResultVO;
import java.util.List;
/**
 * @author 竝inellia
 * @Description: 会话信息对应的Service
 * @date: 2025/10/12
 */

public interface ChatSessionService{

	/**
	 * 根据条件查询列表
	 */
	List<ChatSession>findListByParam(ChatSessionQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(ChatSessionQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSession> findListByPage(ChatSessionQuery query );

	/**
	 * 新增
	 */
	Integer add(ChatSession bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSession> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(ChatSession bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ChatSession> listBean);
	/**
	 * 根据SessionId查询
	 */
	 ChatSession getBySessionId(String sessionId);

	/**
	 * 根据SessionId查询
	 */
	 Integer updateBySessionId(ChatSession bean , String sessionId);

	/**
	 * 根据SessionId删除
	 */
	 Integer deleteBySessionId(String sessionId);


}