package com.hao.io.bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HelloClient {


        private Object send(Message message, String host, int port) {

            System.out.println("客户端: " + host + " 请求连接服务器端口: " + port);

            // 创建一个流套接字并将其连接到指定主机上的指定端口号
            try (Socket socket = new Socket(host, port)) {

                // 往输出流中写入数据
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(message);

                // 从输入流中读取服务器响应的数据
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                return objectInputStream.readObject();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("occur exception: " + e);
            }
            return null;
        }

        public static void main(String[] args) {
            HelloClient helloClient = new HelloClient();
            Message message = (Message) helloClient.send(new Message("content from Client"), "127.0.0.1", 6666);
            System.out.println("client receive message: " + message.getContent());
        }

}
