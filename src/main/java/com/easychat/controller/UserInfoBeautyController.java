package com.easychat.controller;

import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.easychat.service.UserInfoBeautyService;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.vo.ResponseVO;
import java.util.List;
/**
 * @author Pinellia
 * @Description: 靓号表的Controller类
 * @date: 2025/07/23
 */

@RestController
@RequestMapping("/userInfoBeauty")
public class UserInfoBeautyController extends ABaseController{

	@Resource
	private UserInfoBeautyService userInfoBeautyService;

	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoBeautyQuery query) {
		return getSuccessResponseVo(userInfoBeautyService.findListByPage(query));
	}
	/**
	 * 新增
	 */
	@RequestMapping("add")
	public ResponseVO add(UserInfoBeauty bean){
		return getSuccessResponseVo(userInfoBeautyService.add(bean));
	 }
	/**
	 * 批量新增
	 */
	@RequestMapping("addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfoBeauty> listBean){
		return getSuccessResponseVo(userInfoBeautyService.addBatch(listBean));
	 }
	/**
	 * 新增或者修改
	 */
	@RequestMapping("addOrUpdate")
	public ResponseVO addOrUpdate(UserInfoBeauty bean){
		return getSuccessResponseVo(userInfoBeautyService.addOrUpdate(bean));
	 }
	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdate(@RequestBody List<UserInfoBeauty> listBean){
		return getSuccessResponseVo(userInfoBeautyService.addOrUpdateBatch(listBean));
	 }
	/**
	 * 根据Id查询
	 */
	@RequestMapping("getUserInfoBeautyById")
	 public ResponseVO getUserInfoBeautyById(Integer id){
		return getSuccessResponseVo(this.userInfoBeautyService.getById(id));
	 }
	/**
	 * 根据Id更新
	 */
	@RequestMapping("updateUserInfoBeautyById")
	 public ResponseVO updateUserInfoBeautyById(UserInfoBeauty bean,Integer id){
		return getSuccessResponseVo(this.userInfoBeautyService.updateById(bean,id));
	 }
	/**
	 * 根据Id删除
	 */
	@RequestMapping("deleteUserInfoBeautyById")
	 public ResponseVO deleteUserInfoBeautyById(Integer id){
		return getSuccessResponseVo(this.userInfoBeautyService.deleteById(id));
	 }
	/**
	 * 根据UserId查询
	 */
	@RequestMapping("getUserInfoBeautyByUserId")
	 public ResponseVO getUserInfoBeautyByUserId(String userId){
		return getSuccessResponseVo(this.userInfoBeautyService.getByUserId(userId));
	 }
	/**
	 * 根据UserId更新
	 */
	@RequestMapping("updateUserInfoBeautyByUserId")
	 public ResponseVO updateUserInfoBeautyByUserId(UserInfoBeauty bean,String userId){
		return getSuccessResponseVo(this.userInfoBeautyService.updateByUserId(bean,userId));
	 }
	/**
	 * 根据UserId删除
	 */
	@RequestMapping("deleteUserInfoBeautyByUserId")
	 public ResponseVO deleteUserInfoBeautyByUserId(String userId){
		return getSuccessResponseVo(this.userInfoBeautyService.deleteByUserId(userId));
	 }
	/**
	 * 根据Email查询
	 */
	@RequestMapping("getUserInfoBeautyByEmail")
	 public ResponseVO getUserInfoBeautyByEmail(String email){
		return getSuccessResponseVo(this.userInfoBeautyService.getByEmail(email));
	 }
	/**
	 * 根据Email更新
	 */
	@RequestMapping("updateUserInfoBeautyByEmail")
	 public ResponseVO updateUserInfoBeautyByEmail(UserInfoBeauty bean,String email){
		return getSuccessResponseVo(this.userInfoBeautyService.updateByEmail(bean,email));
	 }
	/**
	 * 根据Email删除
	 */
	@RequestMapping("deleteUserInfoBeautyByEmail")
	 public ResponseVO deleteUserInfoBeautyByEmail(String email){
		return getSuccessResponseVo(this.userInfoBeautyService.deleteByEmail(email));
	 }

}