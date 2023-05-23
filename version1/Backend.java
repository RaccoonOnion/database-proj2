// package version1;// package project2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;
import java.sql.Timestamp;
import org.postgresql.util.PSQLException;

public class Backend {// 批处理
    private static boolean debug;
    private static Statement stmt = null;

    private static ResultSet resultSet = null;

    public Backend(Connection con, Boolean debug) {//构造方法
        this.debug = debug;
        try {
            if (con != null && stmt == null) {
                stmt = con.createStatement();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    这个函数实现了查看用户的点赞Liked、收藏Favored、转发Shared帖⼦的ID列表
    输出：ArrayList<Integer> postIDList
     */
    protected ArrayList<Integer> showLFSIDList(String account_name, String type) {
        ArrayList<Integer> postIDList = new ArrayList<>();
        String sql = String.format("SELECT post_id FROM %s WHERE account_name = '%s';",
                type, account_name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                // 处理每一行数据
                postIDList.add(postId);
            }
            return postIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    这个函数实现了查看⽤户⾃⼰关注作者的列表。
    输出：ArrayList<String> followNameList
    */
    protected ArrayList<String> showFollowNameList(String account_name) {
        ArrayList<String> followList = new ArrayList<>();
        String sql = String.format("SELECT followee_name FROM follow WHERE follower_name = '%s';",
                account_name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
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
    这个函数实现了查看用户⾃⼰发布的帖⼦ID
    输出：ArrayList<ArrayList<String>> postIDList
    期中依次序存放着：postIdList， titleList， contentList， datetimeList， cityList， 类型都为ArrayList<String>
     */
    protected ArrayList<Integer> showMyPostIDs(String account_name) {
        ArrayList<Integer> postIDList = new ArrayList<>();
        String sql = String.format("SELECT post_id FROM post WHERE post_account_name = '%s';",
                account_name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                postIDList.add(postId);
            }
            return postIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ArrayList<Integer> showReplyIDs(int postID) {
        ArrayList<Integer> replyIDList = new ArrayList<>();
        String sql = String.format("SELECT id FROM reply WHERE post_id = %d;",
                postID);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int replyId = resultSet.getInt("id");
                replyIDList.add(replyId);
            }
            return replyIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /*
    这个函数实现了利用帖⼦ID list查看post
    输出：ArrayList<ArrayList<String>> postList
    期中依次序存放着：postIdList， titleList， contentList， datetimeList， cityList，post_account_name 类型都为ArrayList<String>
     */
    protected ArrayList<ArrayList<String>> checkPosts(ArrayList<Integer> postIDList) {
        ArrayList<ArrayList<String>> postList = new ArrayList<>();
        ArrayList<String> postIdList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        ArrayList<String> datetimeList = new ArrayList<>();
        ArrayList<String> cityList = new ArrayList<>();
        ArrayList<String> postAccountNameList = new ArrayList<>();
        postList.add(postIdList);
        postList.add(titleList);
        postList.add(contentList);
        postList.add(datetimeList);
        postList.add(cityList);
        postList.add(postAccountNameList);

        for (int postID: postIDList){
            String sql = String.format("SELECT * FROM post WHERE post_id = %d;",
                    postID);
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                resultSet = stmt.executeQuery(sql);
                // 处理结果
                while (resultSet.next()) {
                    int postId = resultSet.getInt("post_id");
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");
                    String datetime = String.valueOf(resultSet.getTimestamp("datetime"));
                    String city = resultSet.getString("city");
                    String name = resultSet.getString("post_account_name");
                    // 处理每一行数据
                    postIdList.add(postId + "");
                    titleList.add(title);
                    contentList.add(content);
                    datetimeList.add(datetime);
                    cityList.add(city);
                    postAccountNameList.add(name);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return postList;
    }

    /*
这个函数实现了利用reply ID list查看reply
输出：ArrayList<ArrayList<String>> replyList
期中依次序存放着：idList, replyIDList， contentList， starNumList, postIDList, authorAccountNameList， 类型都为ArrayList<String>
 */
    protected ArrayList<ArrayList<String>> checkReplies(ArrayList<Integer> replyIDList) {
        ArrayList<ArrayList<String>> replyList = new ArrayList<>();
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<String> replyIdList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        ArrayList<String> starNumList = new ArrayList<>();
        ArrayList<String> postIDList = new ArrayList<>();
        ArrayList<String> authorAccountNameList = new ArrayList<>();
        replyList.add(idList);
        replyList.add(replyIdList);
        replyList.add(contentList);
        replyList.add(starNumList);
        replyList.add(postIDList);
        replyList.add(authorAccountNameList);

        for (int id: replyIDList){
            String sql = String.format("SELECT * FROM reply WHERE id = %d;",
                    id);
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                resultSet = stmt.executeQuery(sql);
                // 处理结果
                while (resultSet.next()) {
                    String replyId = resultSet.getInt("reply_id") + "";
                    String content = resultSet.getString("content");
                    String starNum = resultSet.getInt("stars") + "";
                    String postID = resultSet.getInt("post_id") + "";
                    String authorAccountName = resultSet.getString("author_account_name");
                    // 处理每一行数据
                    idList.add(id+"");
                    replyIdList.add(replyId);
                    contentList.add(content);
                    starNumList.add(starNum);
                    postIDList.add(postID);
                    authorAccountNameList.add(authorAccountName);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return replyList;
    }

    /*
    这个函数实现了查看⾃⼰已回复的帖⼦的ID
    输出：ArrayList<Integer> repliedPostIDList
     */
    protected ArrayList<Integer> showMyRepliedPostIDs(String account_name) {
        ArrayList<Integer> repliedPostIDList = new ArrayList<>();
        String sql = String.format("SELECT distinct post_id FROM reply WHERE author_account_name = '%s';",
                account_name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                // 处理每一行数据
                repliedPostIDList.add(postId);
            }
            return repliedPostIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    这个函数实现了查看⾃⼰的回复的ID list
    输出：ArrayList<Integer> replyIDList
     */
    protected ArrayList<Integer> showMyReplyIDs(String account_name) {
        ArrayList<Integer> replyIDList = new ArrayList<>();
        String sql = String.format("SELECT id FROM reply WHERE author_account_name = '%s';",
                account_name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet.next()) {
                int replyId = resultSet.getInt("id");
                // 处理每一行数据
                replyIDList.add(replyId);
            }
            return replyIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean addAccount(String name, String account_id, String phone, String password) {
        name = name.replaceAll("'", "''");
        String sql = String.format("INSERT INTO account(name,account_id,registration_time,phone,password) VALUES ('%s','%s','%s','%s','%s');",
                name, account_id, new Timestamp(System.currentTimeMillis()).toString(), phone, password);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    Like 点赞帖子： 通过入参name和postID，在like表中记录相应的内容
        名称为name的用户点赞了对应postID的帖子
     */
    protected void lfsPost(String name, int postID, String type) {
        name = name.replaceAll("'", "''");
        String sql = String.format("insert into %s(account_name,post_id) VALUES ('%s',%d);",
                type, name, postID);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    post 发布帖子： 由两个函数组成，posting函数更新post表中的内容 同时category更新了post_category和category两张表的内容
     */
    protected boolean post(String title, String content, String city, String name,ArrayList<String> categories){
        int postid = getMaxPostId() + 1;
        boolean succeedP = posting(postid,title,content,city,name);
        boolean succeedC = categorize(postid,categories);
        return succeedC && succeedP;
    }
    /*
    Posting 发帖：通过入参title，content，city，name 在post表中加入相应内容
        发表了类似的帖子
     */
    //TODO: check ' for all string input!!!!!
    //TODO: add debug mode to exception print
    protected boolean posting(int postId, String title, String content, String city, String name) {
        boolean succeed = true;
        title = title.replaceAll("'", "''");
        content = content.replaceAll("'", "''");
        city = city.replaceAll("'", "''");
        name = name.replaceAll("'", "''");
        String sql = String.format("insert into post(post_id ,title,content,datetime,city,post_account_name) VALUES (%d,'%s','%s','%s','%s','%s');", postId,
                title, content, new Timestamp(System.currentTimeMillis()).toString(), city, name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            succeed = false;
            e.printStackTrace();
        }
        return succeed;
    }

    /*
    category 归类帖子的种类： 与数据库层面的trigger一同更新对应postId的种类
     */
    protected boolean categorize(int postId, ArrayList<String> categories) {
        boolean succeed = true;
        for (String cat : categories) {
            cat = cat.replaceAll("'", "''");

            String sql = String.format("insert into post_category(category_name,post_id) VALUES ('%s',%d);",
                    cat, postId);
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                succeed = false;
                e.printStackTrace();
            }
        }
        return succeed;
    }
    /*
    Reply 回复帖子：
     */

    protected void replyPost(int replyId, String content, int postID, String name) {
        content = content.replaceAll("'", "''");
        name = name.replaceAll("'", "''");
        String sql = String.format("insert into reply(reply_id,content,stars,post_id,author_account_name) VALUES (%d,'%s',%d,%d,'%s');",
                replyId, content, 0, postID, name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void followAccount(String followerName, String followeeName) {
        followerName = followerName.replaceAll("'", "''");
        followeeName = followeeName.replaceAll("'", "''");
        String sql = String.format("insert into follow(follower_name,followee_name) VALUES ('%s','%s');",
                followerName, followeeName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    protected void unfollowAccount(String followerName, String followeeName) {
        followerName.replaceAll("'", "''");
        followeeName.replaceAll("'", "''");
        String sql = String.format("DELETE FROM follow WHERE follower_name = '%s' and followee_name = '%s';",
                followerName, followeeName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<String> getAllAccountNames() {
        ArrayList<String> accountNames = new ArrayList<>();
        String sql = "SELECT name FROM account;";
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                accountNames.add(name);
            }
            return accountNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ArrayList<String> getAllCategories()
    {
        ArrayList<String> categoryNames = new ArrayList<>();
        String sql = "SELECT * FROM category;";
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("category_name");
                categoryNames.add(name);
            }
            return categoryNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    protected ArrayList<Integer> getAllPostIds() {
        ArrayList<Integer> postIds = new ArrayList<>();
        String sql = "SELECT post_id FROM post;";
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int postId = resultSet.getInt("post_id");
                postIds.add(postId);
            }
            return postIds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int getMaxPostId() {
        String sql = "SELECT MAX(post_id) AS max_post_id FROM post;";
        if (debug) System.out.println("Executing SQL command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                int maxPostId = resultSet.getInt("max_post_id");
                return maxPostId;
            }
            return 0; // 如果没有查询到结果，默认返回0或其他合适的默认值
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean verifyPw(String name, String password){
        name = name.replaceAll("'", "''");
        String pw = "";
        String sql = String.format("SELECT password FROM account WHERE name = '%s';",
                name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            pw = resultSet.next() ? resultSet.getString("password"):"";
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (pw.length() == 0 || !pw.equals(password)){
            return false;
        }
        return true;
    }

//    name              varchar primary key,
//    account_id        varchar,
//    registration_time timestamp,
//    phone             varchar,
//    password          varchar

    protected ArrayList<String> getAccountInfo(String name){
        ArrayList<String> infoList = new ArrayList<>();
        name = name.replaceAll("'", "''");
        String sql = String.format("SELECT * FROM account WHERE name = '%s';",
                name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt.executeQuery(sql);
            // 处理结果
            resultSet.next();
            String account_id = resultSet.getString("account_id");
            String registration_time = resultSet.getString("registration_time");
            String phone = resultSet.getString("phone") + "";
            // 处理每一行数据
            infoList.add(account_id);
            infoList.add(registration_time);
            infoList.add(phone);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return infoList;
    }

    protected boolean ifFollowed(String followerName, String followeeName){
        followerName = followerName.replaceAll("'", "''");
        followeeName = followeeName.replaceAll("'", "''");
        String sql = String.format("select count(*) as cnt from follow where follower_name = '%s' and followee_name = '%s';",
                followerName, followeeName);
        if (debug) System.out.println("Executing sql command: " + sql);
        int cnt = 0;
        try {
            resultSet = stmt.executeQuery(sql);
            resultSet.next();
            cnt = resultSet.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (cnt == 0) return false;
        else return true;
    }

    protected void starReply(int id){
        String sql = String.format("update reply set stars = stars + 1 where id = %d;",id);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected void changeAccountDetail(String name,String phone,String password){
        password = password.replaceAll("'","''");
        String sql = String.format("update account set phone = '%s' , password = '%s' where name = '%s';",phone,password,name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
