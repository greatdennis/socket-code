package com.arlin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器
 *
 * @author Junlin
 */
public class Server {
    private static final String BYE = "bye";

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2000);

        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());

        // 等待客户端连接
        while (true) {
            // 得到客户端
            Socket client = server.accept();
            // 客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            // 启动线程
            clientHandler.start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private boolean flag = true;
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + " P: " + socket.getPort());

            PrintStream socketOutput = null;
            BufferedReader socketInput = null;
            try {
                // 得到打印流，用于数据输出：服务器回送数据使用
                socketOutput = new PrintStream(socket.getOutputStream());
                // 得到输入流，用于接收数据
                socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                do {
                    // 从客户端拿到一条数据
                    String str = socketInput.readLine();
                    if (BYE.equalsIgnoreCase(str)) {
                        flag = false;
                        // 回送
                        socketOutput.println(BYE);
                    } else {
                        // 打印到屏幕，并回送数据长度
                        System.out.println(str);
                        socketOutput.println("回送：" + str.length());
                    }
                } while(flag);
            } catch (IOException e) {
                System.out.println("连接异常断开");
            } finally {
                // 关闭资源
                if (socketOutput != null) {
                    socketOutput.close();
                }
                if (socketInput != null) {
                    try {
                        socketInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
