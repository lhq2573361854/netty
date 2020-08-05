package com.tianling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 加深映像
 * author: 85907
 * date: 2020/8/3 18:21
 */
public class GroupChatServer2 {
    private int port=8080;
    private Selector selector;
    private ServerSocketChannel socketChannel;

    public GroupChatServer2() {
        try {
            selector =  Selector.open();
            socketChannel = ServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress(port));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void listen() throws IOException{
        while (true){
            final int select = selector.select();
            if(select > 0){
                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    final SelectionKey next = iterator.next();
                    if(next.isAcceptable()){
                        final SocketChannel accept = socketChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                        System.out.println(accept.getRemoteAddress()+" 上线 ");
                    }
                    if(next.isReadable()){
                        readData(next);
                    }
                    iterator.remove(); //???
                }
            }
        }
    }

    private void readData(SelectionKey next) throws IOException{
        final SocketChannel channel = (SocketChannel)next.channel();
        final ByteBuffer allocate = ByteBuffer.allocate(1024);
        final int read = channel.read(allocate);
        if(read > 0){
            final String s = new String(allocate.array());
            System.out.println("from  客户端 ： " + s);
            sendInfoToOtherClients(s,channel);
        }

    }

    private void sendInfoToOtherClients(String msg, SocketChannel channel) throws IOException {
        for (SelectionKey key : selector.keys()) {
            final Channel channel1 = (Channel) key.channel();
            if(channel1 instanceof SocketChannel && channel != channel1 ){
                final SocketChannel dest = (SocketChannel) channel1;
                final ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
                dest.write(wrap);
            }
        }
    }

    public static void main(String[] args) {
        final GroupChatServer2 groupChatServer2 = new GroupChatServer2();
        try {
            groupChatServer2.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
