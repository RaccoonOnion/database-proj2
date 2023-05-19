//package project2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Loader_new {// 批处理
    /*
    在数据传输上提高了效率；
    客户端-服务端
    sql语句从客户端发送到服务端，一次发送一句
    批处理：一次发送BATCH_SIZE 语句
     */

    private static boolean login;
    private final static int BATCH_SIZE = 10000;
    private static Connection con = null;
    private static PreparedStatement stmt = null;

    private static Statement stmt1 = null;

    private static ResultSet resultSet = null;
    private static QReader input = new QReader();
    private static QWriter out = new QWriter();
    private static Map<String, String[]> name2Attribute = new HashMap<>();


    private static void openDB(Properties prop) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + prop.getProperty("host") + "/" + prop.getProperty("database");
        try {
            con = DriverManager.getConnection(url, prop);
            if (con != null) {
                System.out.println("Successfully connected to the database "
                        + prop.getProperty("database") + " as " + prop.getProperty("user"));
                con.setAutoCommit(false);
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void setPrepareStatement(String tableName, String[] attributeNames) {
        try {
            stmt = null;
            String statement = "INSERT INTO public." + tableName + " (";
            String value = "VALUES (";
            for (int i = 0; i < attributeNames.length; i++) {
                statement += attributeNames[i];
                value += "?";
                if (i == attributeNames.length - 1) {
                    statement += ") ";
                    value += ");";
                } else {
                    statement += ", ";
                    value += ",";
                }
            }
            statement += value;
            stmt = con.prepareStatement(statement);
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDB();
            System.exit(1);
        }
    }

    private static void closeDB() {
        if (con != null) {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                con.close();
                con = null;
            } catch (Exception ignored) {
            }
        }
    }

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

    private static List<String> loadCSVFile(String fileName) {
        try {
            return Files.readAllLines(Path.of("resources/" + fileName + ".csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadData1(String line) { //categories
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setString(1, lineData[0]);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadData2(String line) { //account
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setString(1, lineData[0]);
                stmt.setString(2, lineData[1]);
//                System.out.println(lineData[2]);
                Timestamp ts = Timestamp.valueOf(lineData[2]);
                stmt.setTimestamp(3, ts);
                stmt.setString(4, lineData[3]);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadData3(String line) { //reply
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setLong(1, Long.parseLong(lineData[0]));
                stmt.setString(2, lineData[1]);
                stmt.setLong(3, Long.parseLong(lineData[2]));
                stmt.setLong(4, Long.parseLong(lineData[3]));
                stmt.setString(5, lineData[4]);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadData4(String line) { //liked, favored, shared
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setString(1, lineData[0]);
                stmt.setLong(2, Long.parseLong(lineData[1]));
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadData5(String line) { //follow
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setString(1, lineData[0]);
                stmt.setString(2, lineData[1]);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadData6(String line) { //post
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setLong(1, Long.parseLong(lineData[0]));
                stmt.setString(2, lineData[1]);
                stmt.setString(3, lineData[2]);
                stmt.setTimestamp(4, Timestamp.valueOf(lineData[3]));
                stmt.setString(5, lineData[4]);
                stmt.setString(6, lineData[5]);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void loadData7(String line) { //post_category
        String[] lineData = line.split(";");
        if (con != null) {
            try {
                stmt.setString(1, lineData[0]);
                stmt.setLong(2, Long.parseLong(lineData[1]));
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) {

//        BATCH_SIZE = Integer.parseInt(args[1]);

        long start = System.currentTimeMillis();
        Properties prop = loadDBUser();
        int cntTotal = 0;

        String[] tableNames = {"category", "account", "post", "post_category", "reply", "liked", "favored", "shared", "follow"};
        //表名
        String[] attributeCat = {"category_name"};
        String[] attributeAcc = {"account_id", "name", "registration_time", "phone"};
        String[] attributePost = {"post_id", "title", "content", "datetime", "city", "post_account_name"};
        String[] attributeReply = {"reply_id", "content", "stars", "post_id", "author_account_name"};
        String[] attributeLFS = {"account_name", "post_id"};
        String[] attributeFol = {"follower_name", "followee_name"};
        String[] attributePC = {"category_name", "post_id"};

//        Map<String, String[]> name2Attribute = new HashMap<>();
        name2Attribute.put(tableNames[0], attributeCat);
        name2Attribute.put(tableNames[1], attributeAcc);
        name2Attribute.put(tableNames[2], attributePost);
        name2Attribute.put(tableNames[4], attributeReply);
        name2Attribute.put("LFS", attributeLFS);
        name2Attribute.put(tableNames[8], attributeFol);
        name2Attribute.put(tableNames[3], attributePC);

        openDB(prop);
//        for (int i = 0; i < tableNames.length; i++){
//            int cnt = 0;
//            List<String> lines = loadCSVFile(tableNames[i]);
//            String tableName = tableNames[i];
//            //目前需要插入的表 -> tableName

//            boolean ifLFS = tableName.equals("liked") || tableName.equals("favored") || tableName.equals("shared");
//            if (ifLFS){
//                setPrepareStatement(tableName, name2Attribute.get("LFS"));
//            }
//            else{
//                setPrepareStatement(tableName, name2Attribute.get(tableName));
//            }
//            try {
//                for (String line : lines) {
//                    if (ifLFS){
//                        loadData4(line);
//                    } else if (tableName.equals("category")) {
//                        loadData1(line);
//                    }
//                    else if (tableName.equals("account")) {
//                        loadData2(line);
//                    }
//                    else if (tableName.equals("post")) {
//                        loadData6(line);
//                    }
//                    else if (tableName.equals("reply")) {
//                        loadData3(line);
//                    } else if (tableName.equals("follow")) {
//                        loadData5(line);
//                    }
//                    else{// post_category
//                        loadData7(line);
//                    }
//                    cnt++;
//                    if (cnt % BATCH_SIZE == 0) {
//                        stmt.executeBatch();
//                        System.out.println("insert " + BATCH_SIZE + " data successfully!");
//                        stmt.clearBatch();
//                        cntTotal += BATCH_SIZE;
//                    }
//                }
//
//                if (cnt % BATCH_SIZE != 0) {
//                    stmt.executeBatch();
//                    System.out.println("insert " + cnt % BATCH_SIZE + " data successfully!");
//                    cntTotal += cnt;
//                    stmt.clearBatch();
//                }
//                con.commit();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }

        closeDB();

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");
//        System.out.println(cntTotal + " records successfully loaded");
//        System.out.println("Loading speed : " + (cntTotal * 1000L) / (end - start) + " records/s");

    }
/*
这个函数实现了查看用户的点赞Liked、收藏Favored、转发Shared帖⼦的列表
输出：ArrayList<Integer> postList
 */
    private static ArrayList<Integer> showLFSList(String account_name, String type){
//        System.out.println("Please enter your name and the list you want to check, separated them by a line break");
//        String account_name = input.next();
//        System.out.println("Your name input is: " + account_name);
//        String type = input.next();
        ArrayList<Integer> postList = new ArrayList<>();
        String sql = String.format("SELECT post_id FROM %s WHERE account_name = '%s';",
            type, account_name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            resultSet = stmt1.executeQuery(sql);
//            System.out.println(type + " list:");
            // 处理结果
            while (resultSet.next()) {
                int pid = resultSet.getInt("post_id");
                // 处理每一行数据
//                System.out.println(pid + "; ");
                postList.add(pid);
            }
            return postList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
这个函数实现了查看⽤户⾃⼰关注作者的列表。
输出：ArrayList<String> followList
 */
    private static ArrayList<String> showFollowList(String account_name){
//        System.out.println("Please enter your name end with a line break");
//        String account_name = input.next();
//        System.out.println(account_name);
//        String type = "Follow";
        ArrayList<String> followList = new ArrayList<>();
        String sql = String.format("SELECT followee_name FROM follow WHERE follower_name = '%s';",
            account_name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            resultSet = stmt1.executeQuery(sql);
//            System.out.println(type + " list:");
            // 处理结果
            while (resultSet.next()) {
                String followeeName = resultSet.getString("followee_name");
                // 处理每一行数据
//                System.out.println(followeeName + ";");
                followList.add(followeeName);
            }
            return followList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
/*
这个函数实现了查看用户⾃⼰发布的帖⼦
输出：ArrayList<ArrayList<String>> postList
期中依次序存放着：postIdList， titleList， contentList， datetimeList， cityList， 类型都为ArrayList<String>
 */
    private static ArrayList<ArrayList<String>> showMyPost(String account_name){
//    System.out.println("Please enter your account name end with a line break");
//    String account_name = input.next();
//    System.out.println(account_name);
//    String type = "Post";
        ArrayList<ArrayList<String>> postList = new ArrayList<>();
        ArrayList<String> postIdList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        ArrayList<String> datetimeList = new ArrayList<>();
        ArrayList<String> cityList = new ArrayList<>();
        postList.add(postIdList);
        postList.add(titleList);
        postList.add(contentList);
        postList.add(datetimeList);
        postList.add(cityList);

    String sql = String.format("SELECT * FROM post WHERE post_account_name = '%s';",
        account_name);
    System.out.println("Executing sql command: " + sql);
    try {
        if (con != null && stmt1 == null) {
            stmt1 = con.createStatement();
        }
        resultSet = stmt1.executeQuery(sql);
//        System.out.println(type + " list:");
        // 处理结果
        while (resultSet.next()) {
            int postId = resultSet.getInt("post_id");
            String title = resultSet.getString("title");
            String content = resultSet.getString("content");
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Timestamp datetime = resultSet.getTimestamp("datetime");
//            LocalDateTime dateTimeLDT = datetime.toLocalDateTime();
//            String formattedDateTime = dateTimeLDT.format(formatter);
            String brutalDT = String.valueOf(datetime);
            String city = resultSet.getString("city");
            // 处理每一行数据
//            System.out.printf("Post ID: %d, title: %s, content: %s, datetime: %s, city: %s\n", postId, title, content, formattedDateTime, city);
//            System.out.println(brutalDT);
            postIdList.add(postId+"");
            titleList.add(title);
            contentList.add(content);
            datetimeList.add(brutalDT);
            cityList.add(city);
        }
        return postList;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

/*
这个函数实现了查看⾃⼰已回复的帖⼦的ID
输出：ArrayList<Integer> replyList
 */
    private static ArrayList<Integer> showMyReply(String account_name){
//        System.out.println("Please enter your account name end with a line break");
//        String account_name = input.next();
//        System.out.println(account_name);
//        String type = "Reply";
        ArrayList<Integer> replyList = new ArrayList<>();
        String sql = String.format("SELECT distinct post_id FROM reply WHERE author_account_name = '%s';",
            account_name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            resultSet = stmt1.executeQuery(sql);
//            System.out.println(type + " list:");
            // 处理结果
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                // 处理每一行数据
//                System.out.printf("Post ID: %d\n", postId);
//            System.out.println(brutalDT);
                replyList.add(postId);
            }
            return replyList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static class QReader {
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        private StringTokenizer tokenizer = new StringTokenizer("");

        private String innerNextLine() {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        public boolean hasNext() {
            while (!tokenizer.hasMoreTokens()) {
                String nextLine = innerNextLine();
                if (nextLine == null) {
                    return false;
                }
                tokenizer = new StringTokenizer(nextLine);
            }
            return true;
        }

        public String nextLine() {
            tokenizer = new StringTokenizer("");
            return innerNextLine();
        }

        public String next() {
            hasNext();
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }

    static class QWriter implements Closeable {
        private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        public void print(Object object) {
            try {
                writer.write(object.toString());
            } catch (IOException e) {
                return;
            }
        }

        public void println(Object object) {
            try {
                writer.write(object.toString());
                writer.write("\n");
            } catch (IOException e) {
                return;
            }
        }

        @Override
        public void close() {
            try {
                writer.close();
            } catch (IOException e) {
                return;
            }
        }
    }

    public static String generateRandomPhone(int length) {
        String numberChar = "0123456789";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    public static String generateRandomID(int length) {
        String numberChar = "0123456789X";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    public static String getRandomTime(String startDate, String endDate) {
        //时间的格式
        String dateTime = null;
        long result = 0;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        //转化
        java.util.Date start = null;
        try {
            start = format1.parse(startDate);
            Date end = format1.parse(endDate);
            //获取建立时间
            result = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateTime = format2.format(result);
            return dateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}


