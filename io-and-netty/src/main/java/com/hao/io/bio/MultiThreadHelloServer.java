package com.hao.io.bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class MultiThreadHelloServer implements Runnable {

    private Socket socket;

    //线程池处理

    static ExecutorService threadPool = new ThreadPoolExecutor(10, 100, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), Executors.defaultThreadFactory());

    public MultiThreadHelloServer(Socket socket) {
        this.socket = socket;
    }

    public static void start(int port) {

        // 创建 ServerSocket 对象并且绑定一个端口
        try (ServerSocket server = new ServerSocket(port);) {
            // 通过 accept()方法监听客户端请求
            System.out.println("正在监听客户端请求......");
            while (true) {
                Socket accept = server.accept();
                System.out.println("Connected");
                threadPool.execute(new MultiThreadHelloServer(accept));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        MultiThreadHelloServer.start(6666);
    }

    @Override
    public void run() {
        System.out.println("客户端 " + socket.getRemoteSocketAddress() + " 连接成功!");
        // 打开 Socket 的输入流和输出流
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

            // 通过输入流读取客户端发送的请求信息
            Message message = (Message) objectInputStream.readObject();
            System.out.println("Server receive message: " + message.getContent());

            // 通过输出流向客户端发送响应信息
            message.setContent("new content from Server");
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("occur exception: " + e);
        }
    }


}