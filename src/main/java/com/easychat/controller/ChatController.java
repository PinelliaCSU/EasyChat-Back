package com.easychat.controller;


import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.enums.MessageTypeEnum;
import com.easychat.entity.enums.ResponseCodeEnum;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.service.ChatMessageService;
import com.easychat.service.ChatSessionService;
import com.easychat.service.ChatSessionUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.reflection.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController{
    private static  final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Resource
    private ChatMessageService chatMessageService;
    @Resource
    private ChatSessionService chatSessionService;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    private AppConfig appConfig;

    @RequestMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVO sendMessage(HttpServletRequest request, @NotEmpty String contactId,
                                  @NotEmpty @Max(500) String messageContent,
                                  @NotNull Integer messageType,
                                  Long fileSize,
                                  String fileName,
                                  Integer fileType) throws BusinessException {
        MessageTypeEnum messageTypeEnum  = MessageTypeEnum.getByType(messageType);
        if(messageTypeEnum == null || !ArrayUtils.contains(new Integer[] {MessageTypeEnum.CHAT.getType(),MessageTypeEnum.MEDIA_CHAT.getType()},messageTypeEnum)){
            //有一些聊天类型时不接受的
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContactId(contactId);
        chatMessage.setMessageContent(messageContent);
        chatMessage.setFileSize(fileSize);
        chatMessage.setFileName(fileName);
        chatMessage.setFileType(fileType);

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage,tokenUserInfoDto);
        return getSuccessResponseVo(messageSendDto);
    }

}
