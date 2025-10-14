package com.easychat.websocket;


import com.easychat.entity.dto.MessageSendDto;
import com.easychat.utils.JsonUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


//消息处理器
@Component("messageHandler")
public class MessageHandler {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ChannelContextUtils channelContextUtils;

    private static  final String MESSAGE_TOPIC = "message.topic";

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @PostConstruct
    public void listenMessage(){
        RTopic rTopic = redissonClient.getTopic(MESSAGE_TOPIC);
        rTopic.addListener(MessageSendDto.class,(MessageSendDto,sendDto)->{
           logger.info("收到消息{}", JsonUtils.convertObj2Json(MessageSendDto));
        });
    }

    public void sendMessage(MessageSendDto sendDto) {
        RTopic rTopic = redissonClient.getTopic(MESSAGE_TOPIC);
        rTopic.publish(sendDto);
    }
}
