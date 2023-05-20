import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
public class Application {

    private static int numOfCon = 1;
    protected static ArrayList<Connection> conPool = new ArrayList<>();
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
    private static void openDB(Properties prop) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + prop.getProperty("host") + "/" + prop.getProperty("database");
        try {
            for (int i = 0; i < numOfCon; i++){
                Connection con = DriverManager.getConnection(url, prop);
                conPool.add(con);
            }
//            Connection con = DriverManager.getConnection(url, prop);
            if (conPool.get(0) != null) { // here we only check the first connection
                System.out.println("Successfully connected to the database "
                    + prop.getProperty("database") + " as " + prop.getProperty("user"));
                conPool.get(0).setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void closeDB() {
        for (int i = 0; i < numOfCon; i++){// close all connections in connection pool
            try {
                conPool.get(i).close();
            }catch (Exception ignore){
                // ignore the exception
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Properties prop = loadDBUser();

        openDB(prop);

        Fundamental fun = new Fundamental(conPool.get(0)); // Instantiate a Fundamental type

        /*
        We can use fun to perform any actions here
         */
        ArrayList<Integer> likeList = fun.showLFSList("brave_apple","liked");
        for (int i = 0; i < likeList.size(); i++){
            System.out.println(likeList.get(i));
        }

        closeDB(); // close DB and close all connections

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");

    }
}




