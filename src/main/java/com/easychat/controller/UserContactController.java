package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.enums.PageSize;
import com.easychat.entity.enums.ResponseCodeEnum;
import com.easychat.entity.enums.UserContactStatusEnum;
import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.exception.BusinessException;
import com.easychat.service.UserContactApplyService;
import com.easychat.service.UserContactService;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.sun.javafx.scene.control.behavior.PaginationBehavior;
import jodd.util.ArraysUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/contact")
public class UserContactController extends ABaseController{
    @Resource
    private UserContactService userContactService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserContactApplyService userContactApplyService;


    @RequestMapping("/search")
    @GlobalInterceptor
    public ResponseVO search(HttpServletRequest request, @NotEmpty String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContactSearchResultDto resultDto = userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);
        return getSuccessResponseVo(resultDto);
    }

    @RequestMapping("/applyAdd")
    @GlobalInterceptor
    public ResponseVO applyAdd(HttpServletRequest request, @NotEmpty String contactId,String applyInfo) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        Integer joinType = userContactService.applyAdd(tokenUserInfoDto,contactId,applyInfo);

        return getSuccessResponseVo(joinType);
    }


    @RequestMapping("/loadApply")
    @GlobalInterceptor
    public ResponseVO loadApply(HttpServletRequest request, Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setOrderBy("create_time desc");
        applyQuery.setReceiveUserId(tokenUserInfoDto.getUserId());
        applyQuery.setPageNo(pageNo);
        applyQuery.setPageSize(PageSize.SIZE15.getSize());
        applyQuery.setQueryContactInfo(true);
        PaginationResultVO resultVO = userContactApplyService.findListByPage(applyQuery);
        return getSuccessResponseVo(resultVO);
    }


    @RequestMapping("/dealWithApply")
    @GlobalInterceptor
    public ResponseVO dealWithApply(HttpServletRequest request, @NotEmpty Integer applyId,@NotNull Integer status) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.userContactApplyService.dealWithApply(tokenUserInfoDto.getUserId(),applyId,status);

        return getSuccessResponseVo(null);
    }

    @RequestMapping("/loadContact")
    @GlobalInterceptor
    public ResponseVO loadContact(HttpServletRequest request, @NotEmpty String contactType) throws BusinessException {

        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByName(contactType);
        if(contactTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        TokenUserInfoDto userInfoDto = getTokenUserInfoDto(request);
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setUserId(userInfoDto.getUserId());
        userContactQuery.setContactType(contactTypeEnum.getType());
        if(contactTypeEnum.USER == contactTypeEnum){
            userContactQuery.setQueryContactUserInfo(true);
        } else if (contactTypeEnum.GROUP == contactTypeEnum) {
            userContactQuery.setQueryGroupInfo(true);
            userContactQuery.setExcludeMyGroup(true);
        }
        userContactQuery.setOrderBy("last_update_time desc");
        userContactQuery.setStatusArray(new Integer[]{
                UserContactStatusEnum.FRIEND.getStatus(),
                UserContactStatusEnum.DEL_BE.getStatus(),
                UserContactStatusEnum.BLACKLIST_BE.getStatus()
        });


        List<UserContact> contactList = userContactService.findListByParam(userContactQuery);
        return getSuccessResponseVo(contactList);
    }

    //获取联系人信息，不一定是好友
    @RequestMapping("/getContactInfo")
    @GlobalInterceptor
    public ResponseVO getContactInfo(HttpServletRequest request, @NotEmpty String contactId) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        UserInfo userInfo = userInfoService.getByUserId(tokenUserInfoDto.getUserId());
        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);
        userInfoVO.setContactStatus(UserContactStatusEnum.NOT_FRIEND.getStatus());


        UserContact userContact = userContactService.getByUserIdAndContactId(tokenUserInfoDto.getUserId(),contactId);
        if(userContact != null) {
            userInfoVO.setContactStatus(UserContactStatusEnum.FRIEND.getStatus());
        }
        return getSuccessResponseVo(userInfoVO);
    }

    //获取联系人好友，一定要是好友
    @RequestMapping("/getContactUserInfo")
    @GlobalInterceptor
    public ResponseVO getContactUserInfo(HttpServletRequest request, @NotEmpty String contactId) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        UserContact userContact = userContactService.getByUserIdAndContactId(tokenUserInfoDto.getUserId(),contactId);
        if(userContact == null || !ArraysUtil.contains(new Integer[]{
                UserContactStatusEnum.FRIEND.getStatus(),
                UserContactStatusEnum.DEL_BE.getStatus(),
                UserContactStatusEnum.BLACKLIST_BE.getStatus()
                },
                userContact.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserInfo userInfo = userInfoService.getByUserId(tokenUserInfoDto.getUserId());
        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);
        userInfoVO.setContactStatus(UserContactStatusEnum.NOT_FRIEND.getStatus());

        return getSuccessResponseVo(userInfoVO);
    }



    //获取联系人好友，一定要是好友
    @RequestMapping("/delContact")
    @GlobalInterceptor
    public ResponseVO delContact(HttpServletRequest request, @NotEmpty String contactId) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        userContactService.removeUserContact(tokenUserInfoDto.getUserId(),contactId,UserContactStatusEnum.DEL);
        return getSuccessResponseVo(null);
    }

    @RequestMapping("/addContact2BlackList")
    @GlobalInterceptor
    public ResponseVO addContact2BlackList(HttpServletRequest request, @NotEmpty String contactId) throws BusinessException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        userContactService.removeUserContact(tokenUserInfoDto.getUserId(),contactId,UserContactStatusEnum.BLACKLIST);
        return getSuccessResponseVo(null);
    }

}
