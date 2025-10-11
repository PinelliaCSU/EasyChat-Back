package com.easychat.websocket;

import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ChannelContextUtils {

    private static final Logger logger= LoggerFactory.getLogger(ChannelContextUtils.class);

    private static final ConcurrentHashMap<String,Channel> USER_CONTEXT_MAP=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP=new ConcurrentHashMap<>();

    @Resource
    RedisComponent redisComponent;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    //建立用户ID与网络连接通道的映射关系
    public void addContext(String userId, Channel channel) {
        // 获取通道的唯一ID
        String channelId = channel.id().toString();
        logger.info("add channel id:{}",channelId);
        // 声明AttributeKey变量
        AttributeKey attributeKey = null;
        //AttributeKey
        //Netty提供的用于在Channel上存储自定义属性的键,每个AttributeKey都有一个唯一的名字用于类型安全地访问Channel上的属性
        //检查AttributeKey是否已存在
        if(!AttributeKey.exists(channelId)){
            // 不存在则创建新的
            attributeKey = AttributeKey.newInstance(channelId);
        }else{
            // 存在则获取已存在的实例
            attributeKey = AttributeKey.valueOf(channelId);
        }

        // 将userId设置为通道的属性
        channel.attr(attributeKey).set(userId);

        List<String> contactIdList = redisComponent.getUserContactList(userId);//获取联系人列表
        //可能有多个群组和多个联系人，去判断是否是群组
        for (String contactId : contactIdList) {
            if(contactId.startsWith(UserContactTypeEnum.GROUP.getPrefix())){
                add2Group(contactId,channel);//是群组，加入到群组中
            }
        }

        USER_CONTEXT_MAP.put(userId, channel);
        redisComponent.saveUerHeartBeat(userId);

        //更新用户最后连续时间
        UserInfo userInfo = new UserInfo();
        userInfo.setLastLoginTime(new Date());
        userInfoMapper.updateByUserId(userInfo,userId);


    }

    private void add2Group(String groupId, Channel channel) {
        ChannelGroup group = GROUP_CONTEXT_MAP.get(groupId);
        if(group == null){
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, group);
        }
        if(channel == null){
            return;
        }
        group.add(channel);
    }


}
