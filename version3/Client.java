package version2;
import static java.lang.Thread.sleep;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static int stage = 0;
    private static int subStage = 0;
    private static boolean login = false;
    private static User usr = null;
    private static Post post = null;
    private static Reply reply = null;

    private static Utils utils = new Utils();

    private static Scanner scanner = new Scanner(System.in);

    private static boolean first = true;

    private static ClientMsg clientMsg;

    public static void main(String[] args) {
        try {
            while (true){
            // 创建客户端Socket，连接到服务器
            Socket socket = new Socket("localhost", 8888);
//                System.out.println("the stage and substage is"+stage+subStage);
            if (first){
                clientMsg = new ClientMsg(stage, subStage, login, usr, post, reply, null);
                handleObjectOutput(socket, clientMsg);
                first = false;
            }

            // 输出流
            handleObjectOutput(socket, clientMsg);

            // 输入流
            ServerMsg serverMsg = handleObjectInput(socket);
            // 对服务器信息进行处理与用户交互，得到输出
            stage = serverMsg.getStage();
            subStage = serverMsg.getSubStage();
            login = serverMsg.isLogin();
            usr = serverMsg.getUsr();
            post = serverMsg.getPost();
            reply = serverMsg.getReply();
            String prompt = serverMsg.getPrompt();
            String mode = serverMsg.getMode();

            if (stage == -1){ // exit
                socket.close();
                System.exit(0);
            }
            System.out.print(prompt);

            ArrayList<String> usrInput = new ArrayList<>();
            if (mode.length() > 0 && mode.charAt(0) == 'g'){
                for(int i = 0; i < mode.length() - 1; i++){
                    char type = mode.charAt(i+1);
                    switch (type){
                        case '1':{
                            usrInput.add(scanner.next());
                            break;
                        }
                        case '2':{
                            usrInput.add(utils.getWord(scanner));
                            break;
                        }
                    }
                }
            }

            String[] clientInput = usrInput.toArray(new String[usrInput.size()]);
            clientMsg = new ClientMsg(stage, subStage, login, usr, post, reply, clientInput);

            // 关闭连接
            socket.close();
//            sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendStringMessage(Socket socket, String message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 发送字符串消息
        out.println(message);

        // 接收服务器的字符串响应
        String serverResponse = in.readLine();
        System.out.println("服务器响应: " + serverResponse);

        in.close();
        out.close();
    }

    private static void sendByteStreamData(Socket socket, String data) throws IOException {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        OutputStream out = socket.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        // 发送字节流数据
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        out.flush();
        in.close();
        out.close();
    }

    private static ServerMsg handleObjectInput(Socket clientSocket) throws IOException, ClassNotFoundException {
        // input stream
        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        ServerMsg msg = null;
        // 读取客户端发送的对象 ArrayList<Obejct>
        Object receivedObject = objectInputStream.readObject();
        if (receivedObject instanceof ServerMsg) {
            msg = (ServerMsg) receivedObject;
//            System.out.println("收到服务器消息。");
        }else{
            System.out.println("未接收到正确服务器信息。");
        }
//        objectInputStream.close();
        return msg;
    }

    private static void handleObjectOutput(Socket clientSocket, ClientMsg clientMsg) throws IOException, ClassNotFoundException {
//        System.out.println("send msg to server");
        // Output stream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        // 向客户端发送对象响应
        objectOutputStream.writeObject(clientMsg);
//        System.out.println("finish sending");
//        objectOutputStream.close();
    }

}

