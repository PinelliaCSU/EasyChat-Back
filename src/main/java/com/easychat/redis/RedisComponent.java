package com.easychat.redis;


import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import jdk.nashorn.internal.parser.Token;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    public Long getUerHeartBeat(String userId){
        return  (Long) redisUtils.get(Constants.REDIS_KEY_WS_USER_HEART_BEAT);//心跳检测
    }

    public void saveUerHeartBeat(String userId){
        redisUtils.setex(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId,System.currentTimeMillis(),Constants.REDIS_KEY_EXPIRES_HEART_BEAT);
    }
    public void removeUerHeartBeat(String userId){
        redisUtils.delete(Constants.REDIS_KEY_WS_USER_HEART_BEAT + userId);
    }

    //在redis中存储，分为两个，一个是Token，一个是UserId
    public void saveTokenUserInfoDto(TokenUserInfoDto tokenUserInfoDto){
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN + tokenUserInfoDto.getToken(), tokenUserInfoDto,Constants.REDIS_KEY_EXPIRES_DAY * 2);
        redisUtils.setex(Constants.REDIS_KEY_WS_TOKEN_USERID + tokenUserInfoDto.getUserId(), tokenUserInfoDto.getToken(),Constants.REDIS_KEY_EXPIRES_DAY * 2);
    }

    public TokenUserInfoDto getTokenUserInfoDto(String token){
        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
        return tokenUserInfoDto;
    }


    public SysSettingDto getSysSetting(){
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if(sysSettingDto == null){
            sysSettingDto = new SysSettingDto();
        }

        return sysSettingDto;
    }

    public void saveSysSetting(SysSettingDto sysSettingDto){
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingDto);
    }

    //清空联系人
    public void cleanUserContact(String userId){
        redisUtils.delete(Constants.REDIS_KEY_USER_CONTACT + userId);
    }

    //批量添加联系人
    public void addUSerContactBatch(String userId, List<String> contactList){
        redisUtils.lpushAll(Constants.REDIS_KEY_USER_CONTACT + userId,contactList,Constants.REDIS_KEY_EXPIRES_DAY * 2);
    }

    //获取联系人
    public List<String> getUserContactList(String userId){
        return (List<String>) redisUtils.getQueueList(Constants.REDIS_KEY_USER_CONTACT + userId);
    }
}
