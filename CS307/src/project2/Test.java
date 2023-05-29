package project2;

import java.io.*;
import java.util.*;
import java.sql.*;

public class Test {

    protected static Connection con = null;
    private static Statement stmt1 = null;

    private static final int ACCOUNT_COUNT = 100;
    private static final int POST_COUNT = 100;
    private static final int PHONE_LENGTH = 11;
    private static final int LFS_COUNT = 100;
    private static final int REPLY_COUNT = 100;


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
                con.setAutoCommit(true);
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
                if (stmt1 != null) {
                    stmt1.close();
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

    public void insert() {

        long start = System.currentTimeMillis();
        Properties prop = loadDBUser();
        int cntTotal = 0;

        openDB(prop);
        Fundamental fundamental = new Fundamental(con);

        accountInserting(fundamental);
        postInserting(fundamental);
        likedInserting(fundamental);
        sharedInserting(fundamental);
        favoredInserting(fundamental);
        followInserting(fundamental);
        primaryReplyInserting(fundamental);
        secondaryReplyInserting(fundamental);

        closeDB();

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");
        System.out.println(cntTotal + " records successfully loaded");
        System.out.println("Loading speed : " + (cntTotal * 1000L) / (end - start) + " records/s");

    }

    private static void accountInserting(Fundamental fundamental){
        // Generate and insert account data
        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            String name = generateRandomName();
            String phone = generateRandomPhone(PHONE_LENGTH);
            fundamental.registerNewUser(name, phone);
        }
        System.out.println("Test data generated and inserted successfully.");
    }
    private static void postInserting(Fundamental fundamental) {
        ArrayList<String> accountNames = fundamental.getAllAccountNames();
        Random random = new Random();
        for (int i = 0; i < POST_COUNT; i++) {
            String title = generateRandomString(20);
            String content = generateRandomString(100);
            String city = generateRandomCity();
            String name = accountNames.get(random.nextInt(accountNames.size()));
            List<String> categories = generateRandomCategories(random.nextInt(10));
            fundamental.post(title, content, city, name,categories);
        }
    }
    private static void likedInserting(Fundamental fundamental) {
        ArrayList<String> accountNames = fundamental.getAllAccountNames();
        ArrayList<Integer> postIds = fundamental.getAllPostIds();

        Random random = new Random();

        for (int i = 0; i < LFS_COUNT; i++) {
            String name = accountNames.get(random.nextInt(accountNames.size()));
            int postId = postIds.get(random.nextInt(postIds.size()));
            fundamental.likePost(name, postId);
        }
        System.out.println("Liked data inserted successfully.");
    }
    private static void sharedInserting(Fundamental fundamental) {
        ArrayList<String> accountNames = fundamental.getAllAccountNames();
        ArrayList<Integer> postIds = fundamental.getAllPostIds();

        Random random = new Random();

        for (int i = 0; i < LFS_COUNT; i++) {
            String name = accountNames.get(random.nextInt(accountNames.size()));
            int postId = postIds.get(random.nextInt(postIds.size()));
            fundamental.sharePost(name, postId);
        }
        System.out.println("Shared data inserted successfully.");
    }
    private static void favoredInserting(Fundamental fundamental) {
        ArrayList<String> accountNames = fundamental.getAllAccountNames();
        ArrayList<Integer> postIds = fundamental.getAllPostIds();

        Random random = new Random();

        for (int i = 0; i < LFS_COUNT; i++) {
            String name = accountNames.get(random.nextInt(accountNames.size()));
            int postId = postIds.get(random.nextInt(postIds.size()));
            fundamental.favoritePost(name, postId);
        }
        System.out.println("Favored data inserted successfully.");
    }
    private static void followInserting(Fundamental fundamental) {
        ArrayList<String> accountNames = fundamental.getAllAccountNames();
        Random random = new Random();
        for (String follower : accountNames) {
            // Generate random number of followings for each follower
            int numFollowings = random.nextInt(accountNames.size() - 1)/4000; // Randomly select number of followings
            HashSet<String> followingSet = new HashSet<>();
            while (followingSet.size() < numFollowings) {
                String following = accountNames.get(random.nextInt(accountNames.size())); // Randomly select a following
                if (!following.equals(follower) && !followingSet.contains(following)) {
                    followingSet.add(following);
                }
            }
            // Insert followings into follow table

            for (String following : followingSet) {
                fundamental.followUser(follower, following);
            }
        }
    }
    private static void primaryReplyInserting(Fundamental fundamental) {
        ArrayList<Integer> postIds = fundamental.getAllPostIds();
        ArrayList<String> accountNames = fundamental.getAllAccountNames();
        Random random = new Random();

        for (int replyId = 1; replyId <= REPLY_COUNT; replyId++) {
            int postId = postIds.get(random.nextInt(postIds.size()));
            String accountName = accountNames.get(random.nextInt(accountNames.size()));
            String content = "This is a reply.";
            fundamental.reply(-replyId, postId, content, accountName);
        }
    }
    private static void secondaryReplyInserting(Fundamental fundamental) {
        // 生成测试用例数据
        Random random = new Random();
        int numAuthors = random.nextInt(3); // 随机生成作者数量

        // 获取所有账户名
        ArrayList<String> accountNames = fundamental.getAllAccountNames();

        // 插入次级回复
        ArrayList<int[]> replyIdsAndPostIds = fundamental.getAllReplyIdsAndPostIds();
        for (int[] reply : replyIdsAndPostIds) {
            int topLevelReplyId = reply[0]; // 上一级回复的id
            int postId = reply[1]; // 帖子的post_id

            // 选择次级回复的作者
            String author = accountNames.get(random.nextInt(accountNames.size() - 1));

            // 插入次级回复，其中次级回复的reply_id指向上一级回复的id
            for (int i = 0; i < numAuthors; i++) {
                int replyId = Math.abs(topLevelReplyId); // 次级回复的reply_id
                String content = "次级回复内容 " + (i + 1) + "，对应一级回复 " + topLevelReplyId;
                fundamental.reply(replyId, postId, content, author);
            }
        }
    }


    private static String generateRandomPhone(int length) {
        String numberChar = "0123456789";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }
    private static String generateRandomID(int length) {
        String numberChar = "0123456789X";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }
    private static String generateRandomName() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = characters.charAt(random.nextInt(characters.length()));
            sb.append(c);
        }
        return sb.toString();
    }
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ';l,.!@#$%^&*(){}[],.<>?/??》";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
    private static String generateRandomCity() {
        String[] cities = { "New York", "London", "Tokyo", "Paris", "Berlin", "Sydney", "Toronto", "Dubai" ,
                "北京", "上海", "广州", "深圳", "杭州", "南京", "重庆", "成都", "武汉", "西安",
                "天津", "苏州", "济南", "青岛", "大连", "沈阳", "长春", "哈尔滨", "长沙", "南昌",
                "福州", "厦门", "南宁", "珠海", "佛山", "东莞", "海口", "三亚", "兰州", "银川",
                "太原", "西宁", "贵阳", "昆明", "拉萨", "乌鲁木齐", "香港", "澳门", "台北", "高雄",
                "桂林", "黄山", "张家界", "九寨沟", "凤凰古城", "西塘", "苏州园林", "乌镇", "周庄", "壶口瀑布",
                "敦煌", "莫高窟", "泰山", "庐山", "衡山", "黄山", "峨眉山", "武当山", "五台山", "恒山",
                "青岛", "大连", "天津", "烟台", "威海", "潍坊", "济宁", "泰安", "临沂", "滨州",
                "菏泽", "日照", "德州", "聊城", "东营", "枣庄", "淄博", "莱芜", "赣州", "上饶",
                "景德镇", "萍乡", "九江", "新余", "鹰潭", "宜春", "吉安", "抚州", "南通", "常州",
                "无锡", "扬州", "镇江", "泰州", "盐城", "连云港", "徐州", "淮安", "宿迁", "湖州"};
        Random random = new Random();
        int index = random.nextInt(cities.length);
        return cities[index];
    }
    private static List<String> generateRandomCategories(int n) {
        String[] categories = {
                "Technology", "Sports", "Fashion", "Food", "Travel", "Music", "Art", "Health", "Business"
        };
        Random random = new Random();
        List<String> selectedCategories = new ArrayList<>();

        while (selectedCategories.size() < n) {
            int index = random.nextInt(categories.length);
            String category = categories[index];
            if (!selectedCategories.contains(category)) {
                selectedCategories.add(category);
            }
        }

        return selectedCategories;
    }




}



