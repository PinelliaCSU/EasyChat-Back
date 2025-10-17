package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.enums.GroupStatusEnum;
import com.easychat.entity.enums.UserContactStatusEnum;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.GroupInfoVO;
import com.easychat.exception.BusinessException;
import com.easychat.service.UserContactService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestMapping;
import com.easychat.service.GroupInfoService;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.ResponseVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * @author Pinellia
 * @Description: 的Controller类
 * @date: 2025/08/25
 */

@RestController("groupInfoController")
@RequestMapping("/group")
@Validated
public class GroupInfoController extends ABaseController{

	@Resource
	private GroupInfoService groupInfoService;
	@Resource
	private UserContactService userContactService;

	//头像之所以要两个，是由于可能用户会传送一个极其巨大的图片，可能要处理
	@RequestMapping("/saveGroup")
	@GlobalInterceptor
	public ResponseVO saveGroup(HttpServletRequest request, String GroupId,
								@NotEmpty String GroupName,
								String GroupNotice,
								@NotNull Integer JoinType,
								MultipartFile avatarFile,
								MultipartFile avatarCover) throws BusinessException, IOException {

		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setGroupId(GroupId);
		groupInfo.setGroupName(GroupName);
		groupInfo.setGroupNotice(GroupNotice);
		groupInfo.setJoinType(JoinType);
		groupInfo.setGroupOwnerId(tokenUserInfoDto.getUserId());
		this.groupInfoService.saveGroup(groupInfo,avatarFile,avatarCover);

		return getSuccessResponseVo(null);
	}


	@RequestMapping("/loadMyGroup")
	@GlobalInterceptor
	public ResponseVO loadMyGroup(HttpServletRequest request){

		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
		groupInfoQuery.setGroupOwnerId(tokenUserInfoDto.getUserId());
		groupInfoQuery.setOrderBy("create_time desc");
		List<GroupInfo> groupInfoList = this.groupInfoService.findListByParam(groupInfoQuery);

		return getSuccessResponseVo(groupInfoList);
	}

	@RequestMapping("/getGroupInfo")
	@GlobalInterceptor
	public ResponseVO getGroupInfo(HttpServletRequest request , @NotEmpty String GroupId) throws BusinessException {

		GroupInfo groupInfo =  getGroupDetailCommon(request, GroupId);

		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setContactId(GroupId);
		Integer memberCount = this.userContactService.findCountByParam(userContactQuery);
		groupInfo.setMemberCount(memberCount);
		return getSuccessResponseVo(groupInfo);
	}

	private GroupInfo getGroupDetailCommon(HttpServletRequest request , @NotEmpty String GroupId) throws BusinessException {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

		UserContact userContact = this.userContactService.getByUserIdAndContactId(tokenUserInfoDto.getUserId(),GroupId);
		if(userContact == null || !UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus())){
			throw new BusinessException("你不在该群聊或者该群聊已被解散！");
		}

		GroupInfo groupInfo = this.groupInfoService.getByGroupId(GroupId);
		if(groupInfo == null || !GroupStatusEnum.NORMAL.getStatus().equals(groupInfo.getStatus())){
			throw new BusinessException("该群聊已被解散或不存在！");
		}
		return groupInfo;
	}


	@RequestMapping("/getGroupInfo4Chat")
	@GlobalInterceptor
	public ResponseVO getGroupInfo4Chat(HttpServletRequest request,@NotEmpty String GroupId) throws BusinessException {

		GroupInfo groupInfo = getGroupDetailCommon(request,GroupId);

		UserContactQuery userContactQuery = new UserContactQuery();
		userContactQuery.setContactId(GroupId);
		userContactQuery.setQueryUserInfo(true);
		userContactQuery.setOrderBy("create_time asc");
		userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
		List<UserContact> userContactList = this.userContactService.findListByParam(userContactQuery);

		GroupInfoVO groupInfoVO = new GroupInfoVO();
		groupInfoVO.setGroupInfo(groupInfo);
		groupInfoVO.setUserContactList(userContactList);
		return getSuccessResponseVo(groupInfoVO);
	}


}