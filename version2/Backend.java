// package version2;// package project2;

import java.util.*;
import java.sql.*;
import java.sql.Timestamp;

public class Backend {// 批处理
    private static boolean debug;
    private static Statement stmt = null;
    private static Statement stmt1 = null;

    private static ResultSet resultSet = null;
    private static ResultSet resultSet1 = null;

    public Backend(Connection con, Boolean debug) {//构造方法
        this.debug = debug;
        try {
            if (con != null && stmt == null && stmt1 == null) {
                stmt = con.createStatement();
                stmt1 = con.createStatement();
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
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int postId = resultSet1.getInt("post_id");
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

        ArrayList<String> NameCantSee = getNameCantSee(account_name);

        String sql = String.format("SELECT followee_name FROM follow WHERE follower_name = '%s';",
                account_name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                String followeeName = resultSet1.getString("followee_name");

                if (NameCantSee.contains(followeeName)) continue;

                // 处理每一行数据
                followList.add(followeeName);
            }
            return followList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected String checkAuthorNameByPostId(int postId) {
        String sql = String.format("select post_account_name from post where post_id = '%d'", postId);
        String phoneList;
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet = stmt1.executeQuery(sql);
            resultSet.next();
            phoneList = resultSet.getString("post_account_name");
            return phoneList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ArrayList<String> showMyPhone(String name) {
        String sql = String.format("select phone from account where name = '%s'", name);
        ArrayList<String> phoneList = new ArrayList<>();
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                String phone = resultSet1.getString("phone");
                // 处理每一行数据
                phoneList.add(phone);
            }
            return phoneList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phoneList;
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
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int postId = resultSet1.getInt("post_id");
                postIDList.add(postId);
            }
            return postIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    protected ArrayList<Integer> showReply(int replyID, boolean post) {
        ArrayList<Integer> replyIDList = new ArrayList<>();
        String sql;
        if (post) {
            sql = String.format("SELECT id FROM reply WHERE post_id = %d and reply_id <= 0;",
                    replyID);
        } else {
            sql = String.format("SELECT id FROM reply WHERE reply_id = %d and reply_id > 0;",
                    replyID);
        }

        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int replyId = resultSet1.getInt("id");
                // 处理每一行数据
                replyIDList.add(replyId);
            }
            return replyIDList;
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
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int replyId = resultSet1.getInt("id");
                replyIDList.add(replyId);
            }
            return replyIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ArrayList<String> getNameCantSee(String name) {
        ArrayList<String> result = new ArrayList<>();
        String sql = String.format("select shielded_name from shielding where shielder_name = '%s';",
                name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                String temp = resultSet1.getString("shielded_name");
                result.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sql = String.format("select blocker_name from block where blocked_name = '%s';",
                name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                String temp = resultSet1.getString("blocker_name");
                result.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /*
    这个函数实现了利用帖⼦ID list查看post
    输出：ArrayList<ArrayList<String>> postList
    期中依次序存放着：postIdList， titleList， contentList， datetimeList， cityList，post_account_name 类型都为ArrayList<String>
     */
    protected ArrayList<ArrayList<String>> checkPosts(ArrayList<Integer> postIDList, String userName) {
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

        ArrayList<String> NameCantSee = getNameCantSee(userName);

        for (int postID : postIDList) {
            String sql = String.format("SELECT * FROM post WHERE post_id = %d;",
                    postID);
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                resultSet1 = stmt.executeQuery(sql);
                // 处理结果
                while (resultSet1.next()) {
                    if (NameCantSee.contains(resultSet1.getString("post_account_name"))) {
                        continue;
                    }

                    int postId = resultSet1.getInt("post_id");
                    String title = resultSet1.getString("title");
                    String content = resultSet1.getString("content");
                    String datetime = String.valueOf(resultSet1.getTimestamp("datetime"));
                    String city = resultSet1.getString("city");

                    String name = resultSet1.getBoolean("anonymous") ? "***Anonymous***" : resultSet1.getString("post_account_name");
//                    if (resultSet.getBoolean("anonymous")) name = "***Anonymous***";
//                    else name = resultSet.getString("post_account_name");
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
        return Utils.transfor(postList);
    }

    /*
这个函数实现了利用reply ID list查看reply
输出：ArrayList<ArrayList<String>> replyList
期中依次序存放着：idList, replyIDList， contentList， starNumList, postIDList, authorAccountNameList， 类型都为ArrayList<String>
 */
    protected ArrayList<ArrayList<String>> checkReplies(ArrayList<Integer> replyIDList, String userName) {
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

        ArrayList<String> NameCantSee = getNameCantSee(userName);

        for (int id : replyIDList) {
            String sql = String.format("SELECT * FROM reply WHERE id = %d;",
                    id);
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                resultSet1 = stmt.executeQuery(sql);
                // 处理结果
                while (resultSet1.next()) {
                    if (NameCantSee.contains(resultSet1.getString("author_account_name"))
                            || NameCantSee.contains(checkAuthorNameByPostId(resultSet1.getInt("post_id")))
                    ) continue;

                    String replyId = resultSet1.getInt("reply_id") + "";
                    String content = resultSet1.getString("content");
                    String starNum = resultSet1.getInt("stars") + "";
                    String postID = resultSet1.getInt("post_id") + "";

                    String authorAccountName = resultSet1.getBoolean("anonymous") ? "***Anonymous***" : resultSet1.getString("author_account_name");

                    // 处理每一行数据
                    idList.add(id + "");
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

//        replyList = Utils.transfor(replyList);
//        for (int i = 0; i < replyList.size(); i++) {
//            if (NameCantSee.contains(checkAuthorNameByPostId(Integer.parseInt(replyList.get(i).get(4)))))
//                replyList.remove(i);
//        }

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
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int postId = resultSet1.getInt("post_id");
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
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int replyId = resultSet1.getInt("id");
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


    protected ArrayList<Integer> getPostIdLFS(String userName, String type) {
        ArrayList<Integer> result = new ArrayList<>();
        userName = userName.replaceAll("'", "''");
        String sql = String.format("select post_id from %s where account_name = '%s';", type, userName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                int postId = resultSet1.getInt("post_id");
                result.add(postId);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Like 点赞帖子： 通过入参name和postID，在like表中记录相应的内容
        名称为name的用户点赞了对应postID的帖子
     */
    protected void lfsPost(String name, int postID, String type) {
        name = name.replaceAll("'", "''");
        ArrayList<Integer> LFSList = getPostIdLFS(name, type);
        if (LFSList.contains(postID)) {
            String sql = String.format("delete from %s where post_id = %d and account_name = '%s';",
                    type, postID, name);
            System.out.println(String.format("The %s relation is deleted",type));
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sql = String.format("insert into %s(account_name,post_id) VALUES ('%s',%d);",
                    type, name, postID);
            if (debug) System.out.println("Executing sql command: " + sql);
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    post 发布帖子： 由两个函数组成，posting函数更新post表中的内容 同时category更新了post_category和category两张表的内容
     */
    protected boolean post(String title, String content, String city, String name, ArrayList<String> categories, boolean Anonymous) {
        int postid = getMaxPostId() + 1;
        boolean succeedP = posting(postid, title, content, city, name, Anonymous);
        boolean succeedC = categorize(postid, categories);
        return succeedC && succeedP;
    }

    /*
    Posting 发帖：通过入参title，content，city，name 在post表中加入相应内容
        发表了类似的帖子
     */
    //TODO: check ' for all string input!!!!!
    //TODO: add debug mode to exception print
    protected boolean posting(int postId, String title, String content, String city, String name, boolean Anonymous) {
        boolean succeed = true;
        title = title.replaceAll("'", "''");
        content = content.replaceAll("'", "''");
        city = city.replaceAll("'", "''");
        name = name.replaceAll("'", "''");

        String sql = String.format("insert into post(post_id ,title,content,datetime,city,post_account_name,Anonymous) VALUES (%d,'%s','%s','%s','%s','%s',%b);", postId,
                title, content, new Timestamp(System.currentTimeMillis()).toString(), city, name, Anonymous);
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

    protected void replyPost(int replyId, String content, int postID, String name, boolean Anonymous) {
        content = content.replaceAll("'", "''");
        name = name.replaceAll("'", "''");
        String sql = String.format("insert into reply(reply_id,content,stars,post_id,author_account_name,anonymous) VALUES (%d,'%s',%d,%d,'%s',%b);",
                replyId, content, 0, postID, name, Anonymous);
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

    protected void blockAccount(String blockedName, String blockerName) {
        blockedName = blockedName.replaceAll("'", "''");
        blockerName = blockerName.replaceAll("'", "''");
        String sql = String.format("insert into block(blocked_name,blocker_name) VALUES ('%s','%s');",
                blockedName, blockerName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void shieldAccount(String shieldedName, String shielderName) {
        shieldedName = shieldedName.replaceAll("'", "''");
        shielderName = shielderName.replaceAll("'", "''");
        String sql = String.format("insert into Shielding(shielded_name ,shielder_name ) VALUES ('%s','%s');",
                shieldedName, shielderName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void unfollowAccount(String followerName, String followeeName) {
        followerName = followerName.replaceAll("'", "''");
        followeeName = followeeName.replaceAll("'", "''");
        String sql = String.format("DELETE FROM follow WHERE follower_name = '%s' and followee_name = '%s';",
                followerName, followeeName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void unBlockAccount(String blockedName, String blockerName) {
        blockedName = blockedName.replaceAll("'", "''");
        blockerName = blockerName.replaceAll("'", "''");
        String sql = String.format("DELETE FROM block WHERE blocked_name = '%s' and blocker_name = '%s';",
                blockedName, blockerName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void unShieldAccount(String shieldedName, String shielderName) {
        shieldedName = shieldedName.replaceAll("'", "''");
        shielderName = shielderName.replaceAll("'", "''");
        String sql = String.format("DELETE FROM shielding WHERE shielded_name = '%s' and shielder_name = '%s';",
                shieldedName, shielderName);
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
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                String name = resultSet1.getString("name");
                accountNames.add(name);
            }
            return accountNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ArrayList<String> getAllCategories() {
        ArrayList<String> categoryNames = new ArrayList<>();
        String sql = "SELECT * FROM category;";
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                String name = resultSet1.getString("category_name");
                categoryNames.add(name);
            }
            return categoryNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ArrayList<String> getShieldAccount(String userName) {
        ArrayList<String> categoryNames = new ArrayList<>();
        userName = userName.replaceAll("'", "''");
        String sql = String.format("select shielded_name from shielding where shielder_name = '%s';", userName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                String name = resultSet1.getString("shielded_name");
                categoryNames.add(name);
            }
            return categoryNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected ArrayList<String> getBlockedAccount(String userName) {
        ArrayList<String> categoryNames = new ArrayList<>();
        userName = userName.replaceAll("'", "''");
        String sql = String.format("select blocked_name from block where blocker_name = '%s';", userName);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                String name = resultSet1.getString("blocked_name");
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
            resultSet1 = stmt.executeQuery(sql);
            while (resultSet1.next()) {
                int postId = resultSet1.getInt("post_id");
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
            resultSet1 = stmt.executeQuery(sql);
            if (resultSet1.next()) {
                int maxPostId = resultSet1.getInt("max_post_id");
                return maxPostId;
            }
            return 0; // 如果没有查询到结果，默认返回0或其他合适的默认值
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean verifyPw(String name, String password) {
        name = name.replaceAll("'", "''");
        String pw = "";
        String sql = String.format("SELECT password FROM account WHERE name = '%s';",
                name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            pw = resultSet1.next() ? resultSet1.getString("password") : "";
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (pw.length() == 0 || !pw.equals(password)) {
            return false;
        }
        return true;
    }

//    name              varchar primary key,
//    account_id        varchar,
//    registration_time timestamp,
//    phone             varchar,
//    password          varchar

    protected ArrayList<String> getAccountInfo(String name) {
        ArrayList<String> infoList = new ArrayList<>();
        name = name.replaceAll("'", "''");
        String sql = String.format("SELECT * FROM account WHERE name = '%s';",
                name);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            resultSet1.next();
            String account_id = resultSet1.getString("account_id");
            String registration_time = resultSet1.getString("registration_time");
            String phone = resultSet1.getString("phone") + "";
            // 处理每一行数据
            infoList.add(account_id);
            infoList.add(registration_time);
            infoList.add(phone);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return infoList;
    }

    protected boolean ifFollowed(String followerName, String followeeName) {
        followerName = followerName.replaceAll("'", "''");
        followeeName = followeeName.replaceAll("'", "''");
        String sql = String.format("select count(*) as cnt from follow where follower_name = '%s' and followee_name = '%s';",
                followerName, followeeName);
        if (debug) System.out.println("Executing sql command: " + sql);
        int cnt = 0;
        try {
            resultSet1 = stmt.executeQuery(sql);
            resultSet1.next();
            cnt = resultSet1.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (cnt == 0) return false;
        else return true;
    }

    protected void starReply(int id) {
        String sql = String.format("update reply set stars = stars + 1 where id = %d;", id);
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected boolean changeAccountDetail(String name, String phone, String password) {
        password = password.replaceAll("'", "''");
        String sql = String.format("update account set phone = '%s' , password = '%s' where name = '%s';", phone, password, name);
        boolean succeed = false;
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            stmt.execute(sql);
            succeed = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return succeed;
    }

    protected ArrayList<ArrayList<String>> checkPostsMultiParameter(String keywords, String start, String end, String City, String Author_name) {
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

        keywords = keywords.replaceAll("'", "''");
        City = City.replaceAll("'", "''");
        Author_name = Author_name.replaceAll("'", "''");
        String sql = "SELECT * FROM post WHERE 1 = 1";
        if (keywords != "") sql += " and content like '%" + keywords + "%'";
        if (start != "") sql += " and datetime >= '" + String.valueOf(Timestamp.valueOf(start)) + "'";
        if (end != "") sql += " and datetime <= '" + String.valueOf(Timestamp.valueOf(end)) + "'";
        if (City != "") sql += " and city like '%" + City + "%'";
        if (Author_name != "") sql += " and post_account_name = '" + Author_name + "'";

        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int postId = resultSet1.getInt("post_id");

                String SQL = String.format("update searchCount set numOfSearched = numOfSearched + 1 where post_id = %d;", postId);
                stmt1.execute(SQL);

                String title = resultSet1.getString("title");
                String content = resultSet1.getString("content");
                String datetime = String.valueOf(resultSet1.getTimestamp("datetime"));
                String city = resultSet1.getString("city");

                String name = resultSet1.getBoolean("anonymous") ? "***Anonymous***" : resultSet1.getString("post_account_name");

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

        return postList;
    }

    protected ArrayList<Integer> showHotlist() {
        ArrayList<Integer> replyIDList = new ArrayList<>();
        String sql = String.format("select post_id from searchcount where numOfSearched != 0 order by numOfSearched desc limit 10;");
        if (debug) System.out.println("Executing sql command: " + sql);
        try {
            resultSet1 = stmt.executeQuery(sql);
            // 处理结果
            while (resultSet1.next()) {
                int replyId = resultSet1.getInt("post_id");
                // 处理每一行数据
                replyIDList.add(replyId);
            }
            return replyIDList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
