package com.easychat.service;

import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * @author Pinellia
 * @Description: 对应的Service
 * @date: 2025/08/25
 */

public interface GroupInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<GroupInfo>findListByParam(GroupInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(GroupInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery query );

	/**
	 * 新增
	 */
	Integer add(GroupInfo bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<GroupInfo> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(GroupInfo bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<GroupInfo> listBean);
	/**
	 * 根据GroupId查询
	 */
	 GroupInfo getByGroupId(String groupId);

	/**
	 * 根据GroupId查询
	 */
	 Integer updateByGroupId(GroupInfo bean , String groupId);

	/**
	 * 根据GroupId删除
	 */
	 Integer deleteByGroupId(String groupId);

	 void saveGroup(GroupInfo groupInfo , MultipartFile avatarFile, MultipartFile avatarCover) throws BusinessException, IOException;

	 void dissolutionGroup(String groupOwnerId,String groupId) throws BusinessException;
}