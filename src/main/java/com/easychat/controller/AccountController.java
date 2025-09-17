package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisComponent;
import com.easychat.redis.RedisUtils;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("accountController")
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController{

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisComponent redisComponent;


    @RequestMapping("/checkCode")
    public ResponseVO checkCode(){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100,42);
        String code = captcha.text();
        String checkCodeKey = UUID.randomUUID().toString();

        logger.info("验证码是{}",code);
        //每一个key都是固定的前缀加上随机的key值，防止被覆盖
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey,code,Constants.REDIS_TIME_1MIN * 5);
        String checkCodeBase64 = captcha.toBase64();
        Map<String,String> result = new HashMap<>();
        result.put("checkCode",checkCodeBase64);
        result.put("checkCodeKey",checkCodeKey);
        return getSuccessResponseVo(result);
    }

    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty String checkCodeKey,
                               @NotEmpty @Email String email,
                               @NotEmpty String password,
                               @NotEmpty String nickname,
                               @NotEmpty String checkCode) throws BusinessException {
        try{
            if(!checkCode.equalsIgnoreCase((String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey))){
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.Register(email,nickname,password);//注册
            return getSuccessResponseVo(null);//注册不需要返回什么东西
        }finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
        }
    }
    @RequestMapping("/login")
    public ResponseVO login(@NotNull String checkCodeKey,
                            @NotNull @Email String email,
                            @NotEmpty String password,
                            @NotEmpty String checkCode) throws BusinessException {
        try{
            if(!checkCode.equalsIgnoreCase((String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey))){
                throw new BusinessException("图片验证码不正确");
            }

            UserInfoVO userInfoVO = userInfoService.Login(email,password);//登录
            return getSuccessResponseVo(userInfoVO);
        }finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
        }
    }

    @GlobalInterceptor
    @RequestMapping("/getSysSetting")
    public ResponseVO login() throws BusinessException {
        return getSuccessResponseVo(redisComponent.getSysSetting());
    }
}
