package com.tianling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;


/**
 * author: 85907
 * date: 2020/8/3 12:37
 */
public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static int port = 8080;

    public GroupChatServer() {
        try{
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.bind(new InetSocketAddress(port));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void listen(){
        try {
            while(true) {
                int select = selector.select();
                if(select > 0){
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey next = iterator.next();
                        if(next.isAcceptable()){
                            SocketChannel accept = listenChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                            System.out.println(accept.getRemoteAddress()+" 上线 ");
                        }
                        if(next.isReadable()){
                            readData(next);
                        }
                        iterator.remove();

                    }

                }else{
                    System.out.println("等待。。。。。");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData(SelectionKey key){
        SocketChannel channel = null;
        try {
            channel = (SocketChannel)key.channel();
            final ByteBuffer allocate = ByteBuffer.allocate(1024);
            final int read = channel.read(allocate);
            if(read > 0 ){
                final String s = new String(allocate.array());
                System.out.println("from 客户端："+s);
                sendInfoToOtherClients(s,channel);
            }

        }catch (Exception e){
            try {
                System.out.println(channel.getRemoteAddress() +" 离线了");
                key.cancel();
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    private void sendInfoToOtherClients(String msg , SocketChannel channel) throws IOException {
        System.out.println("服务器转发中。。。。");
        for (SelectionKey key : selector.keys()) {
            final Channel channel1 = (Channel)key.channel();
            if(channel1 instanceof SocketChannel && channel!= channel1){
                final SocketChannel dest = (SocketChannel) channel1;
                final ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
                dest.write(wrap);
            }
        }
    }

    public static void main(String[] args) {
        final GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }

}
