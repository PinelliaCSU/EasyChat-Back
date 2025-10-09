package com.easychat.websocket.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//心跳处理器
public class HandlerHeartBeat extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(HandlerHeartBeat.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            if(e.state() == IdleState.READER_IDLE){
                logger.info("心跳超时");
            }else if(e.state() == IdleState.WRITER_IDLE){
                ctx.writeAndFlush("heart");
            }


        }
    }
}
