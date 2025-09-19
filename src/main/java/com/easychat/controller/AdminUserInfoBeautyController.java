package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.service.UserInfoBeautyService;
import com.easychat.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController("adminUserInfoBeautyController")
@RequestMapping("/admin")
public class AdminUserInfoBeautyController extends ABaseController {
    @Resource
    private UserInfoBeautyService userInfoBeautyService;


    @RequestMapping("/loadBeautyAccountList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadBeautyAccountList(UserInfoBeautyQuery userInfoBeautyQuery) {
        userInfoBeautyQuery.setOrderBy("id desc");
        PaginationResultVO resultVO  = userInfoBeautyService.findListByPage(userInfoBeautyQuery);
        return getSuccessResponseVo(resultVO);
    }


    @RequestMapping("/saveBeautyAccount")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveBeautyAccount(UserInfoBeauty beauty) throws BusinessException {
        userInfoBeautyService.saveAccount(beauty);
        return getSuccessResponseVo(null);
    }


}
