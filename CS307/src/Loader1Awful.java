import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.sql.*;

public class Loader1Awful {
    private static Connection con = null;
    private static Statement stmt = null;

    public static void openDBLoader1(Properties prop) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + prop.getProperty("host") + "/" + prop.getProperty("database");
        try {
            con = DriverManager.getConnection(url, prop);
//            if (con != null) {
//                System.out.println("Successfully connected to the database "
//                        + prop.getProperty("database") + " as " + prop.getProperty("user"));
//            }
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void closeDBLoader1() {
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

    public static Properties loadDBUserLoader1() {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream("resources/dbUser.properties")));
            return properties;
        } catch (IOException e) {
            System.err.println("can not find db user file");
            throw new RuntimeException(e);
        }
    }

    public static void loadAuthorDataLoader1(Author lineData) {
        String sql = String.format("insert into Authors(Authorid, AuthorName, registration_time, phone) VALUES ('%s','%s','%s','%s');",
                lineData.getAuthorID(), lineData.getName(), lineData.getRegistration_time(), lineData.getPhone());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadPostsDataLoader1(Posts lineData) {
        String target = "'";
        String replacement = "''";
        lineData.setContent(lineData.getContent().replaceAll(target,replacement));
        lineData.setTitle(lineData.getTitle().replaceAll(target,replacement));
        lineData.setPosting_city(lineData.getPosting_city().replaceAll(target,replacement));

        String sql = String.format("insert into posts(postID, title, content, posting_time, posting_city, AuthorName) VALUES ('%s','%s','%s','%s','%s','%s');",
                lineData.getPostID(), lineData.getTitle(), lineData.getContent(), lineData.getPosting_time(),lineData.getPosting_city(),lineData.getAuthorName());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadCategories_detailDataLoader1(CategoryDetails lineData) {
        String sql = String.format("insert into categories_detail(postID, Categoryid) VALUES ('%s','%s');",
                lineData.getPostID(),lineData.getCategoryID());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadFollowsDataLoader1(Follows lineData) {
        String sql = String.format("insert into follows(followedName, followerName) VALUES ('%s','%s');",
                lineData.getFollowedName(),lineData.getFollowerName());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadFavoritesDataLoader1(Favorites lineData) {
        String sql = String.format("insert into favorites(authorname, postid) VALUES ('%s','%s');",
                lineData.getAuthorName(),lineData.getPostID());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadSharesDataLoader1(Shares lineData) {
        String sql = String.format("insert into shares(authorname, postid) VALUES ('%s','%s');",
                lineData.getAuthorName(),lineData.getPostID());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadLikesDataLoader1(Likes lineData) {
        String sql = String.format("insert into likes(authorname, postid) VALUES ('%s','%s');",
                lineData.getAuthorName(),lineData.getPostID());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadRepliesDataLoader1(PrimaryReply lineData) {
        String target = "'";
        String replacement = "''";
        lineData.setContent(lineData.getContent().replaceAll(target,replacement));
        String sql = String.format("INSERT INTO primaryreplies (postid, content, stars, authorname) VALUES ('%s','%s','%s','%s');",
                lineData.getPostID(),lineData.getContent(),lineData.getStars(),lineData.getAuthorName());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadSecondaryRepliesDataLoader1(SecondaryReply lineData) {
        String target = "'";
        String replacement = "''";
        lineData.setContent(lineData.getContent().replaceAll(target,replacement));

        String sql = String.format("insert into secondaryreplies(primaryReplyId, content, stars, AuthorName) VALUES ('%s','%s','%s','%s');",
                lineData.getReplyID(),lineData.getContent(),lineData.getStars(),lineData.getAuthorName());
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadCategoryDataLoader1(String lineData) {
        String sql = String.format("insert into Categories(category) VALUES ('%s');",
                lineData);
        try {
            if (con != null) {
                stmt = con.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearDataInTableLoader1() {
        Statement stmt0;
        if (con != null) {
            try {
                stmt0 = con.createStatement();
                stmt0.executeUpdate("drop table if exists authors,follows,categories,favorites,likes,posts,primaryreplies,secondaryreplies,shares,categories_detail cascade;");
                stmt0.executeUpdate("CREATE TABLE if not exists Authors\n" +
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
                stmt0.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

