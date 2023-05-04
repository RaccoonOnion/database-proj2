import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.sql.*;

public class Loader4Transaction {
    public static Connection con = null;
    public static PreparedStatement stmt = null;

    public static void openDBLoader4(Properties prop) {
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

    public static void closeDBLoader4() {
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

    public static Properties loadDBUserLoader4() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream("resources/dbUser.properties")));
            return properties;
        } catch (IOException e) {
            System.err.println("can not find db user file");
            throw new RuntimeException(e);
        }
    }

    public static void setAuthorPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into Authors(Authorid, AuthorName, registration_time, phone) VALUES (?,?,?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setPostsPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into posts(postID, title, content, posting_time, posting_city, AuthorName) VALUES (?,?,?,?,?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setCategories_detailPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into categories_detail(postID, Categoryid) VALUES (?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setFollowsPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into follows(followedName, followerName) VALUES (?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setFavoritesPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into favorites(authorname, postid) VALUES (?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setSharesPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into shares(authorname, postid) VALUES (?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setLikesPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into likes(authorname, postid) VALUES (?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setRepliesPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("INSERT INTO primaryreplies (postid, content, stars, authorname) VALUES (?,?,?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setSecondaryRepliesPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into secondaryreplies(primaryReplyId, content, stars, AuthorName) VALUES (?,?,?,?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }
    public static void setCategoryPrepareStatementLoader4() {
        try {
            stmt = con.prepareStatement("insert into Categories(category) VALUES (?);");
        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closeDBLoader4();
            System.exit(1);
        }
    }

    public static void loadAuthorDataLoader4(Author line) {
        if (con != null) {
            try {
                stmt.setString(1, line.getAuthorID());
                stmt.setString(2, line.getName());
                stmt.setTimestamp(3, line.getRegistration_time());
                stmt.setString(4, line.getPhone());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadPostsDataLoader4(Posts line){
        if (con != null) {
            try {
                stmt.setInt(1, line.getPostID());
                stmt.setString(2, line.getTitle());
                stmt.setString(3, line.getContent());
                stmt.setTimestamp(4, line.getPosting_time());
                stmt.setString(5, line.getPosting_city());
                stmt.setString(6, line.getAuthorName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadCategoryDetailDataLoader4(CategoryDetails line) {
        if (con != null) {
            try {
                stmt.setInt(1, line.getPostID());
                stmt.setInt(2, line.getCategoryID());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadFollowsDataLoader4(Follows line) {
        if (con != null) {
            try {
                stmt.setString(1, line.getFollowedName());
                stmt.setString(2, line.getFollowerName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadFavoritesDataLoader4(Favorites line) {
        if (con != null) {
            try {
                stmt.setString(1, line.getAuthorName());
                stmt.setInt(2, line.getPostID());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadSharesDataLoader4(Shares line) {
        if (con != null) {
            try {
                stmt.setString(1, line.getAuthorName());
                stmt.setInt(2, line.getPostID());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadLikesDataLoader4(Likes line) {
        if (con != null) {
            try {
                stmt.setString(1, line.getAuthorName());
                stmt.setInt(2, line.getPostID());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadRepliesDataLoader4(PrimaryReply line) {
        if (con != null) {
            try {
                stmt.setInt(1, line.getPostID());
                stmt.setString(2, line.getContent());
                stmt.setInt(3,line.getStars());
                stmt.setString(4,line.getAuthorName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadSecondaryRepliesDataLoader4(SecondaryReply line) {
        if (con != null) {
            try {
                stmt.setInt(1, line.getReplyID());
                stmt.setString(2, line.getContent());
                stmt.setInt(3,line.getStars());
                stmt.setString(4,line.getAuthorName());
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public static void loadCategoryDataLoader4(String line) {
        if (con != null) {
            try {
                stmt.setString(1,line);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void clearDataInTableLoader4() {
        Statement stmt0;
        if (con != null) {
            try {
                stmt0 = con.createStatement();
                stmt0.executeUpdate("drop table if exists authors,follows,categories,favorites,likes,posts,primaryreplies,secondaryreplies,shares,categories_detail  cascade;");
                con.commit();
                stmt0.executeUpdate(
                        "CREATE TABLE if not exists Authors\n" +
                        "(\n" +
                        "    Authorid          varchar(255)        not null,\n" +
                        "    AuthorName        VARCHAR(255) unique NOT NULL,\n" +
                        "    registration_time Timestamp           NOT NULL,\n" +
                        "    phone             VARCHAR(20)         NOT NULL,\n" +
                        "    primary key (Authorid, registration_time)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists Categories\n" +
                        "(\n" +
                        "    Categoryid SERIAL PRIMARY KEY,\n" +
                        "    Category   VARCHAR(255) NOT NULL unique\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists Posts\n" +
                        "(\n" +
                        "    postID       int          not null PRIMARY KEY,\n" +
                        "    title        VARCHAR(255) NOT NULL,\n" +
                        "    content      TEXT         NOT NULL,\n" +
                        "    posting_time Timestamp    NOT NULL,\n" +
                        "    posting_city VARCHAR(255) NOT NULL,\n" +
                        "    AuthorName   varchar(255) NOT NULL REFERENCES Authors (AuthorName)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists Categories_detail\n" +
                        "(\n" +
                        "    postID     INT not null references Posts (postID),\n" +
                        "    Categoryid INT NOT NULL references Categories (Categoryid),\n" +
                        "    primary key (postID, Categoryid)\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "CREATE TABLE if not exists Follows\n" +
                        "(\n" +
                        "    followedName varchar NOT NULL REFERENCES Authors (AuthorName),\n" +
                        "    followerName varchar NOT NULL REFERENCES Authors (AuthorName),\n" +
                        "    primary key (followedName, followerName)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists Favorites\n" +
                        "(\n" +
                        "    AuthorName varchar NOT NULL REFERENCES Authors (AuthorName),\n" +
                        "    postID     INT     NOT NULL REFERENCES Posts (postID),\n" +
                        "    primary key (AuthorName, postID)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists Shares\n" +
                        "(\n" +
                        "    AuthorName varchar NOT NULL REFERENCES Authors (AuthorName),\n" +
                        "    postID     INT     NOT NULL REFERENCES Posts (postID),\n" +
                        "    primary key (AuthorName, postID)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists Likes\n" +
                        "(\n" +
                        "    AuthorName varchar NOT NULL REFERENCES Authors (AuthorName),\n" +
                        "    postID     INT     NOT NULL REFERENCES Posts (postID),\n" +
                        "    primary key (AuthorName, postID)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists primaryReplies\n" +
                        "(\n" +
                        "    primaryReplyId SERIAL PRIMARY KEY,\n" +
                        "    content        TEXT    NOT NULL,\n" +
                        "    stars          INT     NOT NULL,\n" +
                        "    AuthorName     varchar NOT NULL REFERENCES Authors (AuthorName),\n" +
                        "    postID         INT     NOT NULL REFERENCES Posts (postID)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE if not exists SecondaryReplies\n" +
                        "(\n" +
                        "    secondaryReplyId SERIAL PRIMARY KEY,\n" +
                        "    primaryReplyId   INT     NOT NULL REFERENCES primaryReplies (primaryReplyId),\n" +
                        "    content          TEXT    NOT NULL,\n" +
                        "    stars            INT     NOT NULL,\n" +
                        "    AuthorName       varchar NOT NULL REFERENCES Authors (AuthorName)\n" +
                        ");");
                con.commit();
                stmt0.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

