package com.tianling;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * author: 85907
 * date: 2020/8/4 18:10
 */
public class TestChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());
        pipeline.addLast("HttpServerHandler",new HttpServerHandler());

    }
}
