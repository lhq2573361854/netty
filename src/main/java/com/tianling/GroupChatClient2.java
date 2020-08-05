package com.tianling;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 自己写的 加深影响
 * author: 85907
 * date: 2020/8/3 18:00
 */
public class GroupChatClient2 {
    private int port=8080;
    private String host="127.0.0.1";
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupChatClient2() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(host,port));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            username = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println( username + "is ok ...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readData() throws IOException {
        final int select = selector.select();
        if(select > 0){
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                final SelectionKey next = iterator.next();
                if (next.isReadable()){
                    final ByteBuffer allocate = ByteBuffer.allocate(1024);
                    final SocketChannel channel = (SocketChannel) next.channel();
                    channel.read(allocate);
                    System.out.println(new String(allocate.array()).trim());
                }
                iterator.remove();
            }
        }
    }

    public void sendData(String msg) throws IOException {
        msg = username + " 说：" + msg;
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
    }

    public static void main(String[] args) {
        final GroupChatClient2 groupChatClient2 = new GroupChatClient2();
        new Thread(){
            public void run(){
                while (true){
                    try {
                        groupChatClient2.readData();
                        Thread.currentThread().sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            final String s = scanner.nextLine();
            try {
                groupChatClient2.sendData(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
