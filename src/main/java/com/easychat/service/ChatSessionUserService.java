package com.easychat.service;

import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.vo.PaginationResultVO;
import java.util.List;
/**
 * @author Pinellia
 * @Description: 会话用户对应的Service
 * @date: 2025/10/12
 */

public interface ChatSessionUserService{

	/**
	 * 根据条件查询列表
	 */
	List<ChatSessionUser>findListByParam(ChatSessionUserQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(ChatSessionUserQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserQuery query );

	/**
	 * 新增
	 */
	Integer add(ChatSessionUser bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSessionUser> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(ChatSessionUser bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<ChatSessionUser> listBean);
	/**
	 * 根据UserIdAndContactId查询
	 */
	 ChatSessionUser getByUserIdAndContactId(String userId,String contactId);

	/**
	 * 根据UserIdAndContactId查询
	 */
	 Integer updateByUserIdAndContactId(ChatSessionUser bean , String userId,String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	 Integer deleteByUserIdAndContactId(String userId,String contactId);

	 Integer updateByParam(ChatSessionUser bean,ChatSessionUserQuery query);

	 public void updateRedundantInfo(String contactName,String contactId);
}