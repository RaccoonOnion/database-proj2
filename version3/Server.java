package version2;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Server {
    private static final int MAX_CONNECTIONS = 10;
    private static Semaphore semaphore = new Semaphore(MAX_CONNECTIONS);
    private static ExecutorService executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
    private static ComboPooledDataSource dataSource; // C3P0 connection pool
//    private static ArrayList<Application> appPool; // Application pool

    private static Properties loadDBUser() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream("resources/dbUser.properties"))); //
            return properties;
        } catch (IOException e) {
            System.err.println("can not find db user file");
            throw new RuntimeException(e);
        }
    }
    static void setupDataSource() {
        try {
            Properties prop = loadDBUser();
            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass("org.postgresql.Driver");
            dataSource.setJdbcUrl("jdbc:postgresql://" + prop.getProperty("host") + ":" + prop.getProperty("port") + "/" + prop.getProperty("database"));
            dataSource.setUser(prop.getProperty("user"));
            dataSource.setPassword(prop.getProperty("password"));
            dataSource.setMaxPoolSize(MAX_CONNECTIONS);
            dataSource.setAutoCommitOnClose(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        setupDataSource(); // 设置连接池

        try {
            ServerSocket serverSocket = new ServerSocket(8888);  // create server socket
            System.out.println("服务器已启动，等待客户端连接...");
            while (true) {
                // 接受客户端连接请求
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已连接，IP地址为: " + clientSocket.getInetAddress());
                // 获取连接许可
                semaphore.acquire();
                System.out.println("许可获取完成。");
                // 创建一个新的线程处理客户端请求
                Runnable worker = new RequestHandler(clientSocket);
                System.out.println("worker创建完成");
                executor.execute(worker);
                System.out.println("worker执行完成");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // 关闭线程池
            if (dataSource != null) {
                dataSource.close(); // 关闭连接池
            }
        }
    }

    static class RequestHandler implements Runnable {// server logic
        private Socket clientSocket;

        public RequestHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
//            System.out.println("我们在run");
            Connection connection = null;
            try {
                // 从连接池获取连接
                connection = dataSource.getConnection();

                // 获取客户端信息
                ClientMsg clientMsg = handleObjectInput(clientSocket);
                ServerMsg serverMsg = null; // TODO: create server message
                // 对客户端信息进行处理调用Application，得到输出
                int stage = clientMsg.getStage();
                int subStage = clientMsg.getSubStage();
                boolean login = clientMsg.isLogin();
                User usr = clientMsg.getUsr();
                Post post = clientMsg.getPost();
                Reply reply = clientMsg.getReply();
                String[] clientInput = clientMsg.getClientInput();

                Application app = new Application(connection, stage, subStage, usr, post, reply, clientInput);
                String[] appResult = app.run();
                int oldStage = Integer.parseInt(appResult[0]);
                int oldSubStage = Integer.parseInt(appResult[1]);
                String msg4Client = appResult[2];
                String mode = appResult[3];
                boolean newLogin = (mode.equals("login")) ? true : login;
                serverMsg = new ServerMsg(app.getStage(), app.getSubStage(), newLogin, app.getUsr(), app.getPost(), app.getReply(), msg4Client, mode);

                //获取输出流
                handleObjectOutput(clientSocket, serverMsg);

                // clear app
//                app.clear(); // TODO: app management

                // 关闭连接
                clientSocket.close();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                // 释放连接许可
                semaphore.release();
                // 关闭数据库连接
                if (connection != null) {
//                    try {
//                        connection.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }

    private static void handleStringTransmission(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // 读取客户端发送的字符串
        String clientMessage = in.readLine();
        System.out.println("收到客户端的字符串消息：" + clientMessage);

        // 向客户端发送字符串响应
        out.println("服务器已接收到字符串消息：" + clientMessage);

        in.close();
        out.close();
    }

    private static void handleByteStreamTransmission(Socket clientSocket) throws IOException {
        InputStream in = clientSocket.getInputStream();
        OutputStream out = clientSocket.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder receivedData = new StringBuilder();

        // 读取客户端发送的字节流数据
        while ((bytesRead = in.read(buffer)) != -1) {
            receivedData.append(new String(buffer, 0, bytesRead));
        }

        System.out.println("收到客户端的字节流数据：" + receivedData);

        // 向客户端发送字节流响应
        byte[] responseBytes = "服务器已接收到字节流数据".getBytes();
        out.write(responseBytes);

        in.close();
        out.close();
    }

    private static ClientMsg handleObjectInput(Socket clientSocket) throws IOException, ClassNotFoundException {
        // input stream
        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        ClientMsg msg = null;
        // 读取客户端发送的对象 ArrayList<Obejct>
        Object receivedObject = objectInputStream.readObject();
        if (receivedObject instanceof ClientMsg) {
            msg = (ClientMsg) receivedObject;
            System.out.println("收到客户端消息。");
        }else{
            System.out.println("未接收到客户端信息。");
        }
//        objectInputStream.close();
        return msg;
    }

    private static void handleObjectOutput(Socket clientSocket, ServerMsg serverMsg) throws IOException, ClassNotFoundException {
        // Output stream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//        System.out.println("I am here");
        // 向客户端发送对象响应
        objectOutputStream.writeObject(serverMsg);

//        objectOutputStream.close();
    }

}
