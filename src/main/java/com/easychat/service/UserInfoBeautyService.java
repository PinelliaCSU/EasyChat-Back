package com.easychat.service;

import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.vo.PaginationResultVO;
import java.util.List;
/**
 * @author 高98
 * @Description: 靓号表对应的Service
 * @date: 2025/07/23
 */

public interface UserInfoBeautyService{

	/**
	 * 根据条件查询列表
	 */
	List<UserInfoBeauty>findListByParam(UserInfoBeautyQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(UserInfoBeautyQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfoBeauty> findListByPage(UserInfoBeautyQuery query );

	/**
	 * 新增
	 */
	Integer add(UserInfoBeauty bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfoBeauty> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(UserInfoBeauty bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserInfoBeauty> listBean);
	/**
	 * 根据Id查询
	 */
	 UserInfoBeauty getById(String id);

	/**
	 * 根据Id查询
	 */
	 Integer updateById(UserInfoBeauty bean , String id);

	/**
	 * 根据Id删除
	 */
	 Integer deleteById(String id);

	/**
	 * 根据UserId查询
	 */
	 UserInfoBeauty getByUserId(String userId);

	/**
	 * 根据UserId查询
	 */
	 Integer updateByUserId(UserInfoBeauty bean , String userId);

	/**
	 * 根据UserId删除
	 */
	 Integer deleteByUserId(String userId);

	/**
	 * 根据Email查询
	 */
	 UserInfoBeauty getByEmail(String email);

	/**
	 * 根据Email查询
	 */
	 Integer updateByEmail(UserInfoBeauty bean , String email);

	/**
	 * 根据Email删除
	 */
	 Integer deleteByEmail(String email);


}