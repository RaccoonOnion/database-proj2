package project2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;
import java.util.StringTokenizer;

public class Test {

    private static boolean login;
    private static String name;
    private static String phone;
    private static boolean logined = false;

    private static Connection con = null;
    private static Statement stmt1 = null;


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

    private static List<String> loadCSVFile(String fileName) {
        try {
            return Files.readAllLines(Path.of("resources/" + fileName + ".csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {


        long start = System.currentTimeMillis();
        Properties prop = loadDBUser();
        int cntTotal = 0;

        openDB(prop);

        ArrayList<String> names = generateNames(10000);
        ArrayList<String> phones = generatePhones(10000);
        ArrayList<String> titles = generateTitles(10000);
        ArrayList<String> contents = generateContents(10000);
        ArrayList<String> cities = generateCities(10000);

        // 执行插入操作
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String phone = phones.get(i);
            String title = titles.get(i);
            String content = contents.get(i);
            String city = cities.get(i);

           fundamental.regisiterNewUser(name, phone,con,stmt1);
           fundamental.posting(title, content, city, name,con,stmt1);
            fundamental.likePost(name, i + 1,con,stmt1);
            fundamental.favoritePost(name, i + 1,con,stmt1);
            fundamental.sharePost(name, i + 1,con,stmt1);
        }

        // 输出测试结果
        System.out.println("Test data generated and inserted successfully.");

        closeDB();

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");
        System.out.println(cntTotal + " records successfully loaded");
        System.out.println("Loading speed : " + (cntTotal * 1000L) / (end - start) + " records/s");

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

    private static final String[] NAMES = {"John", "Alice", "Michael", "Emily", "Daniel", "Olivia", "David", "Sophia"};
    private static final String[] CITIES = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia"};

    // 生成随机姓名
    private static ArrayList<String> generateNames(int count) {
        ArrayList<String> names = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(NAMES.length);
            names.add(NAMES[index]);
        }
        return names;
    }

    // 生成随机手机号
    private static ArrayList<String> generatePhones(int count) {
        ArrayList<String> phones = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            StringBuilder phone = new StringBuilder("1");
            for (int j = 0; j < 10; j++) {
                int digit = random.nextInt(10);
                phone.append(digit);
            }
            phones.add(phone.toString());
        }
        return phones;
    }

    // 生成随机标题
    private static ArrayList<String> generateTitles(int count) {
        ArrayList<String> titles = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int length = random.nextInt(5) + 5; // 随机标题长度为5到10之间
            StringBuilder title = new StringBuilder();
            for (int j = 0; j < length; j++) {
                char c = (char) (random.nextInt(26) + 'A');
                title.append(c);
            }
            titles.add(title.toString());
        }
        return titles;
    }

    // 生成随机内容
    private static ArrayList<String> generateContents(int count) {
        ArrayList<String> contents = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int length = random.nextInt(20) + 10; // 随机内容长度为10到30之间
            StringBuilder content = new StringBuilder();
            for (int j = 0; j < length; j++) {
                char c = (char) (random.nextInt(26) + 'a');
                content.append(c);
            }
            contents.add(content.toString());
        }
        return contents;
    }

    // 生成随机城市
    private static ArrayList<String> generateCities(int count) {
        ArrayList<String> cities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(CITIES.length);
            cities.add(CITIES[index]);
        }
        return cities;
    }
}

