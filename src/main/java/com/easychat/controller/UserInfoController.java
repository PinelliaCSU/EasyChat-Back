package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.utils.CopyTools;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.easychat.service.UserInfoService;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.ResponseVO;
import java.util.List;
/**
 * @author 高98
 * @Description: 用户信息的Controller类
 * @date: 2025/07/23
 */

@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController{

	@Resource
	private UserInfoService userInfoService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query) {
		return getSuccessResponseVo(userInfoService.findListByPage(query));
	}
	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(UserInfo bean){
		return getSuccessResponseVo(userInfoService.add(bean));
	 }
	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfo> listBean){
		return getSuccessResponseVo(userInfoService.addBatch(listBean));
	 }
	/**
	 * 新增或者修改
	 */
	@RequestMapping("addOrUpdate")
	public ResponseVO addOrUpdate(UserInfo bean){
		return getSuccessResponseVo(userInfoService.addOrUpdate(bean));
	 }
	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdate(@RequestBody List<UserInfo> listBean){
		return getSuccessResponseVo(userInfoService.addOrUpdateBatch(listBean));
	 }
	/**
	 * 根据UserId查询
	 */
	@RequestMapping("getUserInfoByUserId")
	 public ResponseVO getUserInfoByUserId(String userId){
		return getSuccessResponseVo(this.userInfoService.getByUserId(userId));
	 }
	/**
	 * 根据UserId更新
	 */
	@RequestMapping("updateUserInfoByUserId")
	 public ResponseVO updateUserInfoByUserId(UserInfo bean,String userId){
		return getSuccessResponseVo(this.userInfoService.updateByUserId(bean,userId));
	 }
	/**
	 * 根据UserId删除
	 */
	@RequestMapping("deleteUserInfoByUserId")
	 public ResponseVO deleteUserInfoByUserId(String userId){
		return getSuccessResponseVo(this.userInfoService.deleteByUserId(userId));
	 }
	/**
	 * 根据Email查询
	 */
	@RequestMapping("getUserInfoByEmail")
	 public ResponseVO getUserInfoByEmail(String email){
		return getSuccessResponseVo(this.userInfoService.getByEmail(email));
	 }
	/**
	 * 根据Email更新
	 */
	@RequestMapping("updateUserInfoByEmail")
	 public ResponseVO updateUserInfoByEmail(UserInfo bean,String email){
		return getSuccessResponseVo(this.userInfoService.updateByEmail(bean,email));
	 }
	/**
	 * 根据Email删除
	 */
	@RequestMapping("deleteUserInfoByEmail")
	 public ResponseVO deleteUserInfoByEmail(String email){
		return getSuccessResponseVo(this.userInfoService.deleteByEmail(email));
	 }


	 @RequestMapping("getUserInfo")
	 @GlobalInterceptor
	public ResponseVO getUserInfo(HttpServletRequest request){
		 TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		 UserInfo uerInfo = userInfoService.getByUserId(tokenUserInfoDto.getUserId());
		 UserInfoVO userInfoVO = CopyTools.copy(uerInfo, UserInfoVO.class);

		 userInfoVO.setAdmin(tokenUserInfoDto.getAdmin());


		 return getSuccessResponseVo(userInfoVO);
	 }
}