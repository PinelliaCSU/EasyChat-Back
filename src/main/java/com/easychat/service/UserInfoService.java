package com.easychat.service;

import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Pinellia
 * @Description: 用户信息对应的Service
 * @date: 2025/07/23
 */

public interface UserInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo>findListByParam(UserInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
    Integer findCountByParam(UserInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query );

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);
	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);
	/**
	 * 新增或修改
	 */
	Integer addOrUpdate(UserInfo bean);
	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);
	/**
	 * 根据UserId查询
	 */
	 UserInfo getByUserId(String userId);

	/**
	 * 根据UserId查询
	 */
	 Integer updateByUserId(UserInfo bean , String userId);

	/**
	 * 根据UserId删除
	 */
	 Integer deleteByUserId(String userId);

	/**
	 * 根据Email查询
	 */
	 UserInfo getByEmail(String email);

	/**
	 * 根据Email查询
	 */
	 Integer updateByEmail(UserInfo bean , String email);

	/**
	 * 根据Email删除
	 */
	 Integer deleteByEmail(String email);

	 /*
	 *注册
	 *
	  */

	 void Register(String email, String nickname, String password) throws BusinessException;

	 UserInfoVO Login(String email, String password) throws BusinessException;

	 void updateUserInfo(UserInfo userInfo, MultipartFile avatarFile,MultipartFile avatarCover) throws IOException;

	 void updateUserStatus(Integer status,String userId) throws BusinessException;

	 void forceOffLine(String userId);
}