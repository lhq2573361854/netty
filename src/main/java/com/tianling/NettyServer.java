package com.tianling;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * author: 85907
 * date: 2020/8/4 17:12
 */
public class NettyServer {
    public static void main(String[] args) {
        final EventLoopGroup boss = new NioEventLoopGroup();
        final EventLoopGroup work = new NioEventLoopGroup();

        final ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                       ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        System.out.println("服务启动完成");

        final ChannelFuture bind = bootstrap.bind(8080);

        try {
            bind.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
