package com.easychat.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NettyWebSocketStarter {


    private static final Logger logger = LoggerFactory.getLogger(NettyWebSocketStarter.class);
    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static void main(String[] args) throws InterruptedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            //设置处理器
                            ChannelPipeline pipeline = channel.pipeline();
                            //对http协议的支持，使用http的解码、编码器
                            pipeline.addLast(new HttpServerCodec());
                            //聚合解码 保证接受到的http请求的完整性
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //心跳,读超时时间，写超时时间，所有类型的超时时间，单位
                            pipeline.addLast(new IdleStateHandler(6,0,0, TimeUnit.SECONDS));
                            pipeline.addLast(new HandlerHeartBeat());//心跳处理器
                            //将http协议升级为ws协议，对websocket支持
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            pipeline.addLast(new HandlerWebSocket());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(5051).sync();
            logger.info("Netty WebSocket Server started on port 5051!!!");
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动Netty失败",e.getMessage());
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
