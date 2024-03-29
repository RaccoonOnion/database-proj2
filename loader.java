import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.*;

public class Loader {// 批处理
    /*
    在数据传输上提高了效率；
    客户端-服务端
    sql语句从客户端发送到服务端，一次发送一句
    批处理：一次发送BATCH_SIZE 语句
     */
    private final static int BATCH_SIZE = 10000;
    private static Connection con = null;
    private static PreparedStatement stmt = null;

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
            for (int i = 0; i < attributeNames.length; i++)
            {
                statement += attributeNames[i];
                value += "?";
                if (i == attributeNames.length -1){
                    statement += ") ";
                    value += ");";
                }
                else{
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

        String[] tableNames = {"category","account","post","post_category","reply","liked","favored","shared","follow"};
        String[] attributeCat = {"category_name"};
        String[] attributeAcc = {"account_id","name","registration_time","phone"};
        String[] attributePost = {"post_id","title","content","datetime","city","post_account_name"};
        String[] attributeReply = {"reply_id","content","stars","post_id","author_account_name"};
        String[] attributeLFS = {"account_name","post_id"};
        String[] attributeFol = {"follower_name","followee_name"};
        String[] attributePC = {"category_name", "post_id"};

        Map<String, String[]> name2Attribute = new HashMap<>();
        name2Attribute.put(tableNames[0], attributeCat);
        name2Attribute.put(tableNames[1], attributeAcc);
        name2Attribute.put(tableNames[2], attributePost);
        name2Attribute.put(tableNames[4], attributeReply);
        name2Attribute.put("LFS", attributeLFS);
        name2Attribute.put(tableNames[8], attributeFol);
        name2Attribute.put(tableNames[3], attributePC);

        openDB(prop);

        for (int i = 0; i < tableNames.length; i++){
            int cnt = 0;
            List<String> lines = loadCSVFile(tableNames[i]);
            String tableName = tableNames[i];
            boolean ifLFS = tableName.equals("liked") || tableName.equals("favored") || tableName.equals("shared");
            if (ifLFS){
                setPrepareStatement(tableName, name2Attribute.get("LFS"));
            }
            else{
                setPrepareStatement(tableName, name2Attribute.get(tableName));
            }
            try {
                for (String line : lines) {
                    if (ifLFS){
                        loadData4(line);
                    } else if (tableName.equals("category")) {
                        loadData1(line);
                    }
                    else if (tableName.equals("account")) {
                        loadData2(line);
                    }
                    else if (tableName.equals("post")) {
                        loadData6(line);
                    }
                    else if (tableName.equals("reply")) {
                        loadData3(line);
                    } else if (tableName.equals("follow")) {
                        loadData5(line);
                    }
                    else{// post_category
                        loadData7(line);
                    }
                    cnt++;
                    if (cnt % BATCH_SIZE == 0) {
                        stmt.executeBatch();
                        System.out.println("insert " + BATCH_SIZE + " data successfully!");
                        stmt.clearBatch();
                        cntTotal += BATCH_SIZE;
                    }
                }

                if (cnt % BATCH_SIZE != 0) {
                    stmt.executeBatch();
                    System.out.println("insert " + cnt % BATCH_SIZE + " data successfully!");
                    cntTotal += cnt;
                    stmt.clearBatch();
                }
                con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        closeDB();

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: "+(end - start)+"ms");
        System.out.println(cntTotal + " records successfully loaded");
        System.out.println("Loading speed : " + (cntTotal * 1000L) / (end - start) + " records/s");

    }
}


