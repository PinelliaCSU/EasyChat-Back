package com.easychat.controller;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.enums.ResponseCodeEnum;;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisUtils;;import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author gao98
 */
public class ABaseController {
    protected static final String STATUS_SUCCESS="success";
    protected static final String STATUS_ERROR="error";

    @Resource
    RedisUtils redisUtils;

    protected <T>ResponseVO getSuccessResponseVo(T t){
        ResponseVO<T>responseVO=new ResponseVO<>();
        responseVO.setStatus(STATUS_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <T>ResponseVO getBusinessErrorResponseVo(BusinessException e, T t){
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUS_ERROR);
        if(e.getCode() == null){
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        }else{
            vo.setCode(e.getCode());
        }
        vo.setInfo(e.getMessage());
        vo.setData(t);
        return vo;
    }

    protected TokenUserInfoDto getTokenUserInfoDto(HttpServletRequest request){
        String token = request.getHeader("token");
        TokenUserInfoDto tokenUserInfoDto=(TokenUserInfoDto)redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);

        return tokenUserInfoDto;
    }
 }
