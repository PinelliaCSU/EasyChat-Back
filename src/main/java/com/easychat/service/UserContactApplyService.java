package com.easychat.service;

import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;

import java.util.List;
/**
 * @author 高98
 * @Description: 联系人申请对应的Service
 * @date: 2025/08/25
 */

public interface UserContactApplyService{

	/**
	 * 根据条件查询列表
	 */
	List<UserContactApply>findListByParam(UserContactApplyQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(UserContactApplyQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery query );

	/**
	 * 新增
	 */
	Integer add(UserContactApply bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserContactApply> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(UserContactApply bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserContactApply> listBean);
	/**
	 * 根据ApplyId查询
	 */
	 UserContactApply getByApplyId(Integer applyId);

	/**
	 * 根据ApplyId查询
	 */
	 Integer updateByApplyId(UserContactApply bean , Integer applyId);

	/**
	 * 根据ApplyId删除
	 */
	 Integer deleteByApplyId(Integer applyId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询
	 */
	 UserContactApply getByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId查询
	 */
	 Integer updateByApplyUserIdAndReceiveUserIdAndContactId(UserContactApply bean , String applyUserId,String receiveUserId,String contactId);

	/**
	 * 根据ApplyUserIdAndReceiveUserIdAndContactId删除
	 */
	 Integer deleteByApplyUserIdAndReceiveUserIdAndContactId(String applyUserId,String receiveUserId,String contactId);

	 Integer updateByParam(UserContactApply t,UserContactApplyQuery p);

	 void dealWithApply(String userId,Integer applyId,Integer status) throws BusinessException;

	Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String applyInfo) throws BusinessException;
}