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
public class fundamental {// 批处理

    private static Connection con = null;
    private static PreparedStatement stmt = null;

    private static Statement stmt1 = null;

    private static ResultSet resultSet = null;

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

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Properties prop = loadDBUser();

        openDB(prop);
/*
Place to perform actions
 */
        closeDB();

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");

    }
    /*
    这个函数实现了查看用户的点赞Liked、收藏Favored、转发Shared帖⼦的列表
    输出：ArrayList<Integer> postList
     */
    private static ArrayList<Integer> showLFSList(String account_name, String type){
        ArrayList<Integer> postList = new ArrayList<>();
        String sql = String.format("SELECT post_id FROM %s WHERE account_name = '%s';",
            type, account_name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            resultSet = stmt1.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int pid = resultSet.getInt("post_id");
                // 处理每一行数据
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
        ArrayList<String> followList = new ArrayList<>();
        String sql = String.format("SELECT followee_name FROM follow WHERE follower_name = '%s';",
            account_name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            resultSet = stmt1.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                String followeeName = resultSet.getString("followee_name");
                // 处理每一行数据
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
            // 处理结果
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                String datetime = String.valueOf(resultSet.getTimestamp("datetime"));
                String city = resultSet.getString("city");
                // 处理每一行数据
                postIdList.add(postId+"");
                titleList.add(title);
                contentList.add(content);
                datetimeList.add(datetime);
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
        ArrayList<Integer> replyList = new ArrayList<>();
        String sql = String.format("SELECT distinct post_id FROM reply WHERE author_account_name = '%s';",
            account_name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            resultSet = stmt1.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                // 处理每一行数据
                replyList.add(postId);
            }
            return replyList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerNewUser(String name, String phone) {
        name.replaceAll("'","''");
        String sql = String.format("insert into account(name,account_id,registration_time,phone) VALUES ('%s','%s','%s','%s');",
            name, generateRandomID(18), new Timestamp(System.currentTimeMillis()).toString(), phone);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Like 点赞帖子： 通过入参name和postID，在like表中记录相应的内容
        名称为name的用户点赞了对应postID的帖子
     */

    private static void likePost(String name, long postID) {
        name.replaceAll("'","''");
        String sql = String.format("insert into liked(account_name,post_id) VALUES ('%s','%s');",
            name,postID);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*
    Favorite 收藏帖子： 通过入参name和postID，在like表中记录相应的内容
        名称为name的用户收藏了对应postID的帖子
     */

    private static void favoritePost(String name, long postID) {
        name.replaceAll("'","''");
        String sql = String.format("insert into favored (account_name,post_id) VALUES ('%s','%s');",
            name,postID);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Share 分享帖子： 通过入参name和postID，在like表中记录相应的内容
        名称为name的用户分享了对应postID的帖子
     */

    private static void sharePost(String name, long postID) {
        name.replaceAll("'","''");
        String sql = String.format("insert into shared(account_name,post_id) VALUES ('%s','%s');",
            name,postID);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Posting 发帖：通过入参title，content，city，name 在post表中加入相应内容
        发表了类似的帖子
     */

    private static void posting(String title, String content, String city, String name) {
        title.replaceAll("'","''");
        content.replaceAll("'","''");
        city.replaceAll("'","''");
        name.replaceAll("'","''");
        String sql = String.format("insert into post(title,content,datetime,city,post_account_name) VALUES ('%s','%s','%s','%s','%s');",
            title, content, new Timestamp(System.currentTimeMillis()).toString(), city, name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Reply 回复帖子：
     */


    private static void reply(int replyId, int postId, String content, String name) {
        content.replaceAll("'","''");
        name.replaceAll("'","''");
        String sql = String.format("insert into reply(reply_id,content,stars,post_id,post_account_name) VALUES ('%s','%s','%s','%s','%s');",
            replyId,postId,content,0,name);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void followUser(String followerName, String followeeName) {
        followerName.replaceAll("'","''");
        String sql = String.format("insert into follow(follower_name,followee_name) VALUES ('%s','%s');",
            followeeName,followerName);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void unfollowUser(String followerName, String followeeName) {
        followerName.replaceAll("'","''");
        followeeName.replaceAll("'","''");
        String sql = String.format("DELETE FROM follow WHERE follower_name = '%s' and followee_name = '%s';",
            followerName,followeeName);
        System.out.println("Executing sql command: " + sql);
        try {
            if (con != null && stmt1 == null) {
                stmt1 = con.createStatement();
            }
            stmt1.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
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
