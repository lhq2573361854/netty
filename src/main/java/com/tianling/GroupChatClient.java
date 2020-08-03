package com.tianling;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * author: 85907
 * date: 2020/8/3 13:54
 */
public class GroupChatClient {
    private String host = "127.0.0.1";
    private int port = 8080;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;


    public GroupChatClient() {

        try {
            selector = Selector.open();

            socketChannel = socketChannel.open(new InetSocketAddress(host,port));

            socketChannel.configureBlocking(false);

            socketChannel.register(selector, SelectionKey.OP_READ);

            username = socketChannel.getLocalAddress().toString().substring(1);

            System.out.println( username + "is ok ...");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendInfo(String info){
        info = username + " 说：" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInfo(){
        try {
            final int select = selector.select();
            if (select > 0){
                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    final SelectionKey next = iterator.next();
                    if(next.isReadable()){
                        final SocketChannel channel = (SocketChannel) next.channel();
                        final ByteBuffer allocate = ByteBuffer.allocate(1024);
                        channel.read(allocate);
                        final String s = new String(allocate.array());
                        System.out.println(s.trim());
                    }
                    iterator.remove();
                }
            }else {
//                System.out.println("没有可用的通道");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final GroupChatClient groupChatClient = new GroupChatClient();
        new Thread(){
            public void run(){
                while (true){
                    groupChatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(500);
                    }catch (Exception e){

                    }
                }
            }
        }.start();
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            final String s = scanner.nextLine();
            groupChatClient.sendInfo(s);
        }

    }

}
