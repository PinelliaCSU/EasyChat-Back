package com.easychat.service;

import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.enums.UserContactStatusEnum;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;

import java.util.List;
/**
 * @author Pinellia
 * @Description: 联系人对应的Service
 * @date: 2025/08/25
 */

public interface UserContactService{

	/**
	 * 根据条件查询列表
	 */
	List<UserContact>findListByParam(UserContactQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(UserContactQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserContact> findListByPage(UserContactQuery query );

	/**
	 * 新增
	 */
	Integer add(UserContact bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContact> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(UserContact bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserContact> listBean);
	/**
	 * 根据UserIdAndContactId查询
	 */
	 UserContact getByUserIdAndContactId(String userId,String contactId);

	/**
	 * 根据UserIdAndContactId查询
	 */
	 Integer updateByUserIdAndContactId(UserContact bean , String userId,String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	 Integer deleteByUserIdAndContactId(String userId,String contactId);

	 Integer updateByParam(UserContact userContact,UserContactQuery userContactQuery);

	 UserContactSearchResultDto searchContact(String userId, String contactId);



	void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) throws BusinessException;

	void removeUserContact(String userId, String contactId, UserContactStatusEnum statusEnum);

	void addContact4Robot(String userId);
}