package com.easychat.websocket;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.WsInitData;
import com.easychat.entity.enums.MessageTypeEnum;
import com.easychat.entity.enums.UserContactApplyStatusEnum;
import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.mappers.ChatMessageMapper;
import com.easychat.mappers.UserContactApplyMapper;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.JsonUtils;
import com.easychat.utils.StringTools;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
public class ChannelContextUtils {

    private static final Logger logger= LoggerFactory.getLogger(ChannelContextUtils.class);

    private static final ConcurrentHashMap<String,Channel> USER_CONTEXT_MAP=new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP=new ConcurrentHashMap<>();

    @Resource
    RedisComponent redisComponent;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    private ChatMessageMapper<ChatMessage,ChatMessageQuery> chatMessageMapper;
    @Resource
    private UserContactApplyMapper<UserContactApply,UserContactApplyQuery> userContactApplyMapper;


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
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        userInfoMapper.updateByUserId(updateInfo,userId);
        //给用户发消息
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        Long sourceLastOffTime = userInfo.getLastOffTime().getTime();
        Long lastOffTime = sourceLastOffTime;
        if(sourceLastOffTime != null && System.currentTimeMillis()-sourceLastOffTime > Constants.MillisSECOND_THREEDAYS){
            lastOffTime = Constants.MillisSECOND_THREEDAYS;//最多只查询三天以前
        }

        //1.查询会话信息 是所有的，保证不同设备的同步
        ChatSessionUserQuery sessionUserQuery = new ChatSessionUserQuery();
        sessionUserQuery.setUserId(userId);
        sessionUserQuery.setOrderBy("last_receive_time desc");
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.findListByParam(sessionUserQuery);

        WsInitData wsInitData = new WsInitData();
        wsInitData.setChatSessionList(chatSessionUserList);


        //2.查询聊天消息

        //查询联系人，但是不是contactIdList，因为这包括了群组中的其它人，我们应该只接受群组，再加上自己
        List<String> groupIdList = contactIdList.stream().filter(item->item.startsWith(UserContactTypeEnum.GROUP.getPrefix())).collect(Collectors.toList());
        groupIdList.add(userId);

        ChatMessageQuery messageQuery = new ChatMessageQuery();
        messageQuery.setContactIdList(groupIdList);
        messageQuery.setLastReceiveTime(lastOffTime);//最后的离线时间
        List<ChatMessage> chatMessageList = chatMessageMapper.selectList(messageQuery);
        wsInitData.setChatMessageList(chatMessageList);


        //3.查询好友申请
        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setReceiveUserId(userId);
        applyQuery.setLastApplyTimestamp(lastOffTime);
        applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
        Integer applyCount = userContactApplyMapper.selectCount(applyQuery);
        wsInitData.setApplyCount(applyCount);

        //发消息

        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.INIT.getType());
        messageSendDto.setContactId(userId);
        messageSendDto.setExtendData(wsInitData);
        sendMsg(messageSendDto,userId);
    }

    public static void sendMsg(MessageSendDto messageSendDto,String receiveId) {
        if(receiveId == null){
            return;
        }
        Channel sendChannel = USER_CONTEXT_MAP.get(receiveId);
        if(sendChannel == null){
            return;
        }
        //相对于客户端而言，联系人就是发送人。所以这里转一下再发送
        messageSendDto.setContactId(messageSendDto.getSendUserId());
        messageSendDto.setContactName(messageSendDto.getSendUserNickName());
        sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));
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


    public void removeContext(Channel channel) {
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        if(!StringTools.isEmpty(userId)){
            USER_CONTEXT_MAP.remove(userId);
        }
        redisComponent.removeUerHeartBeat(userId);
        //更新用户最后离线时间
        UserInfo userInfo = new UserInfo();
        userInfo.setLastOffTime(new Date());
        userInfoMapper.updateByUserId(userInfo,userId);
    }

}
