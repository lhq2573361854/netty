package com.tianling;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * author: 85907
 * date: 2020/8/4 17:28
 */
public class NettyClient {
    public static void main(String[] args) {
        final EventLoopGroup work = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
        try {
            final ChannelFuture cf = bootstrap.connect("127.0.0.1", 8080).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            work.shutdownGracefully();
        }

    }
}
