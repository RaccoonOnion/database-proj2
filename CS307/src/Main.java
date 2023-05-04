import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class Main {

    private static List<Post> Posts;
    private static List<Replies> replies;
    private static HashMap<String, Integer> categoryIndex = new HashMap<>();
    private static HashMap<String, Integer> replyIndex = new HashMap<>();
    private static ArrayList<String> categories = new ArrayList<>();
    private static ArrayList<Author> authors = new ArrayList<>();
    private static ArrayList<Follows> Follows = new ArrayList<>();
    private static ArrayList<Posts> posts = new ArrayList<>();
    private static ArrayList<CategoryDetails> categoryDetails = new ArrayList<>();
    private static ArrayList<Shares> shares = new ArrayList<>();
    private static ArrayList<Likes> likes = new ArrayList<>();
    private static ArrayList<Favorites> favorites = new ArrayList<>();
    private static ArrayList<PrimaryReply> primaryReplies = new ArrayList<>();
    private static ArrayList<SecondaryReply> secondaryReplies = new ArrayList<>();

    public static void main(String[] args) {

        try {
            String jsonStrings = Files.readString(Path.of("E://project data and scripts/project data and scripts/posts.json"));
            Posts = JSON.parseArray(jsonStrings, Post.class);
//            posts.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String jsonStrings2 = Files.readString(Path.of("E://project data and scripts/project data and scripts/replies.json"));
            replies = JSON.parseArray(jsonStrings2, Replies.class);
//            replies.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        categories = categoriesProcess();
        authors = authorProcess();
        Follows = followsProcess();
        posts = postsProcess();
        categoryDetails = categoryDetailsProcess();
        shares = sharesProcess();
        likes = likesProcess();
        favorites = favoritesProcess();
        primaryReplies = primaryRepliesProcess();
        secondaryReplies = secondaryRepliesProcess();
        //数据拆分部分


        dataProcessLoader5();
//        dataProcessLoader4();
//        dataProcessLoader3();
//        dataProcessLoader2();
//        dataProcessLoader1();
    }

    public static void dataProcessLoader5() {
        Properties prop = Loader5Batch.loadDBUserLoader5();

        // Empty target table
        Loader5Batch.openDBLoader5(prop);
        Loader5Batch.clearDataInTableLoader5();
        Loader5Batch.closeDBLoader5();

        int cnt = 0;
        long start = System.currentTimeMillis();
        Loader5Batch.openDBLoader5(prop);
        {
            Loader5Batch.setAuthorPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (Author line : authors) {
                    Loader5Batch.loadAuthorDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setCategoryPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (String line : categories) {
                    Loader5Batch.loadCategoryDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setPostsPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (Posts line : posts) {
                    Loader5Batch.loadPostsDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setCategories_detailPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (CategoryDetails line : categoryDetails) {
                    Loader5Batch.loadCategoryDetailDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setFollowsPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (Follows line : Follows) {
                    Loader5Batch.loadFollowsDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setFavoritesPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (Favorites line : favorites) {
                    Loader5Batch.loadFavoritesDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setSharesPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (Shares line : shares) {
                    Loader5Batch.loadSharesDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setLikesPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (Likes line : likes) {
                    Loader5Batch.loadLikesDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setRepliesPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (PrimaryReply line : primaryReplies) {
                    Loader5Batch.loadRepliesDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader5Batch.setSecondaryRepliesPrepareStatementLoader5();
            //setPrepareStatement Loader5 对于每个Loader和表都要重写

            try {
                for (SecondaryReply line : secondaryReplies) {
                    Loader5Batch.loadSecondaryRepliesDataLoader5(line);//do insert command
                    //loadData Loader5 对于每个Loader和表都要重写

                    if (cnt % Loader5Batch.BATCH_SIZE == 0) {
                        Loader5Batch.stmt.executeBatch();
                        System.out.println("insert " + Loader5Batch.BATCH_SIZE + " data successfully!");
                        Loader5Batch.stmt.clearBatch();
                    }
                    cnt++;
                }
                //每1000个计数

                if (cnt % Loader5Batch.BATCH_SIZE != 0) {
                    Loader5Batch.stmt.executeBatch();
                    System.out.println("insert " + cnt % Loader5Batch.BATCH_SIZE + " data successfully!");
                }
                //完成之后打印输出

                Loader5Batch.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        Loader5Batch.closeDBLoader5();
        long end = System.currentTimeMillis();
        System.out.println(cnt + " records successfully loaded");
        System.out.println("Loader5's Loading speed : " + (cnt * 1000L) / (end - start) + " records/s");
        System.out.println("Loader5's Loading time " + (end - start)/1000.0+ " s");

        int a = 1;
    }

    public static void dataProcessLoader4() {
        Properties prop = Loader4Transaction.loadDBUserLoader4();

        // Empty target table
        Loader4Transaction.openDBLoader4(prop);
        Loader4Transaction.clearDataInTableLoader4();
        Loader4Transaction.closeDBLoader4();

        int cnt = 0;
        long start = System.currentTimeMillis();
        Loader4Transaction.openDBLoader4(prop);
        {
            Loader4Transaction.setAuthorPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写
            for (Author line : authors) {
                Loader4Transaction.loadAuthorDataLoader4(line);//do insert command
                //loadData Loader4 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        {
            Loader4Transaction.setCategoryPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写
                for (String line : categories) {
                    Loader4Transaction.loadCategoryDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
                //每1000个计数
            try {
                Loader4Transaction.con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader4Transaction.setPostsPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写


                for (Posts line : posts) {
                    Loader4Transaction.loadPostsDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
                //每1000个计数

            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
            Loader4Transaction.setCategories_detailPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写
                for (CategoryDetails line : categoryDetails) {
                    Loader4Transaction.loadCategoryDetailDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
                //每1000个计数
            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
            Loader4Transaction.setFollowsPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写

                for (Follows line : Follows) {
                    Loader4Transaction.loadFollowsDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
            Loader4Transaction.setFavoritesPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写
                for (Favorites line : favorites) {
                    Loader4Transaction.loadFavoritesDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
                //每1000个计数
            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader4Transaction.setSharesPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写


                for (Shares line : shares) {
                    Loader4Transaction.loadSharesDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }

            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {

            Loader4Transaction.setLikesPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写


                for (Likes line : likes) {
                    Loader4Transaction.loadLikesDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
            try {
                Loader4Transaction.con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
            Loader4Transaction.setRepliesPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写
                for (PrimaryReply line : primaryReplies) {
                    Loader4Transaction.loadRepliesDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
            try {
                Loader4Transaction.con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        {
            Loader4Transaction.setSecondaryRepliesPrepareStatementLoader4();
            //setPrepareStatement Loader4 对于每个Loader和表都要重写
                for (SecondaryReply line : secondaryReplies) {
                    Loader4Transaction.loadSecondaryRepliesDataLoader4(line);//do insert command
                    //loadData Loader4 对于每个Loader和表都要重写
                    cnt++;
                    if (cnt % 1000 == 0) {
                        System.out.println("insert " + 1000 + " data successfully!");
                    }
                }
            try {
                Loader4Transaction.con.commit();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        Loader4Transaction.closeDBLoader4();
        long end = System.currentTimeMillis();
        System.out.println(cnt + " records successfully loaded");
        System.out.println("Loader4's Loading speed : " + (cnt * 1000L) / (end - start) + " records/s");
        System.out.println("Loader4's Loading time " + (end - start)/1000.0+ " s");

    }

    public static void dataProcessLoader3() {
        Properties prop = Loader3Prepare.loadDBUserLoader3();

        // Empty target table
        Loader3Prepare.openDBLoader3(prop);
        Loader3Prepare.clearDataInTableLoader3();
        Loader3Prepare.closeDBLoader3();

        int cnt = 0;
        long start = System.currentTimeMillis();
        Loader3Prepare.openDBLoader3(prop);
        {
            Loader3Prepare.setAuthorPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写
            for (Author line : authors) {
                Loader3Prepare.loadAuthorDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
        }

        {
            Loader3Prepare.setCategoryPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写
            for (String line : categories) {
                Loader3Prepare.loadCategoryDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            //每1000个计数
            
        }
        {

            Loader3Prepare.setPostsPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写


            for (Posts line : posts) {
                Loader3Prepare.loadPostsDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            //每1000个计数

            
        }
        {
            Loader3Prepare.setCategories_detailPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写
            for (CategoryDetails line : categoryDetails) {
                Loader3Prepare.loadCategoryDetailDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            //每1000个计数
            
        }
        {
            Loader3Prepare.setFollowsPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写

            for (Follows line : Follows) {
                Loader3Prepare.loadFollowsDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            
        }
        {
            Loader3Prepare.setFavoritesPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写
            for (Favorites line : favorites) {
                Loader3Prepare.loadFavoritesDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            //每1000个计数
            
        }
        {

            Loader3Prepare.setSharesPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写


            for (Shares line : shares) {
                Loader3Prepare.loadSharesDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }

            
        }
        {

            Loader3Prepare.setLikesPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写


            for (Likes line : likes) {
                Loader3Prepare.loadLikesDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            
        }
        {
            Loader3Prepare.setRepliesPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写
            for (PrimaryReply line : primaryReplies) {
                Loader3Prepare.loadRepliesDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            
        }
        {
            Loader3Prepare.setSecondaryRepliesPrepareStatementLoader3();
            //setPrepareStatement Loader3 对于每个Loader和表都要重写
            for (SecondaryReply line : secondaryReplies) {
                Loader3Prepare.loadSecondaryRepliesDataLoader3(line);//do insert command
                //loadData Loader3 对于每个Loader和表都要重写
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            
        }
        Loader3Prepare.closeDBLoader3();
        long end = System.currentTimeMillis();
        System.out.println(cnt + " records successfully loaded");
        System.out.println("Loader3's Loading speed : " + (cnt * 1000L) / (end - start) + " records/s");
        System.out.println("Loader3's Loading time " + (end - start)/1000.0 + " s");

    }

    public static void dataProcessLoader2() {
        Properties prop = Loader2Connect.loadDBUserLoader2();

        // Empty target table
        Loader2Connect.openDBLoader2(prop);
        Loader2Connect.clearDataInTableLoader2();
        Loader2Connect.closeDBLoader2();

        long start = System.currentTimeMillis();
        Loader2Connect.openDBLoader2(prop);
        int cnt = 0;
        {
        for (Author line : authors) {
                Loader2Connect.loadAuthorDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
                System.out.println(cnt + " records successfully loaded");
        }

        {
            for (String line : categories) {
                Loader2Connect.loadCategoryDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Posts line : posts) {
                Loader2Connect.loadPostsDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (CategoryDetails line : categoryDetails) {
                Loader2Connect.loadCategories_detailDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Follows line : Follows) {
                Loader2Connect.loadFollowsDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Favorites line : favorites) {
                Loader2Connect.loadFavoritesDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Shares line : shares) {
                Loader2Connect.loadSharesDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Likes line : likes) {
                Loader2Connect.loadLikesDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (PrimaryReply line : primaryReplies) {
                Loader2Connect.loadRepliesDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (SecondaryReply line : secondaryReplies) {
                Loader2Connect.loadSecondaryRepliesDataLoader2(line);//do insert command
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }
        Loader2Connect.closeDBLoader2();
        long end = System.currentTimeMillis();
        System.out.println(cnt + " records successfully loaded");
        System.out.println("Loader2's Loading speed : " + (cnt * 1000L) / (end - start) + " records/s");
        System.out.println("Loader2's Loading time " + (end - start)/1000.0+ " s");
    }

    public static void dataProcessLoader1() {
        Properties prop = Loader1Awful.loadDBUserLoader1();

        // Empty target table
        Loader1Awful.openDBLoader1(prop);
        Loader1Awful.clearDataInTableLoader1();
        Loader1Awful.closeDBLoader1();

        long start = System.currentTimeMillis();
        Loader1Awful.openDBLoader1(prop);
        int cnt = 0;

        {
            for (Author line : authors) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadAuthorDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (String line : categories) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadCategoryDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Posts line : posts) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadPostsDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (CategoryDetails line : categoryDetails) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadCategories_detailDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Follows line : Follows) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadFollowsDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Favorites line : favorites) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadFavoritesDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Shares line : shares) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadSharesDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (Likes line : likes) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadLikesDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (PrimaryReply line : primaryReplies) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadRepliesDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }

        {
            for (SecondaryReply line : secondaryReplies) {
                Loader1Awful.openDBLoader1(prop);
                Loader1Awful.loadSecondaryRepliesDataLoader1(line);//do insert command
                Loader1Awful.closeDBLoader1();
                cnt++;
                if (cnt % 1000 == 0) {
                    System.out.println("insert " + 1000 + " data successfully!");
                }
            }
            System.out.println(cnt + " records successfully loaded");
        }
        Loader1Awful.closeDBLoader1();

        long end = System.currentTimeMillis();
        System.out.println(cnt + " records successfully loaded");
        System.out.println("Loader1's Loading speed : " + (cnt * 1000L) / (end - start) + " records/s");
        System.out.println("Loader1's Loading time " + (end - start)/1000.0+ " s");
    }

    public static ArrayList<SecondaryReply> secondaryRepliesProcess() {
        ArrayList<SecondaryReply> result = new ArrayList<>();
        for (Replies replies : replies) {
            result.add(new SecondaryReply(replyIndex.get(replies.getReplyContent()), replies.getSecondaryReplyContent(), replies.getSecondaryReplyStars(), replies.getSecondaryReplyAuthor()));
        }
        return result;
    }

    public static ArrayList<PrimaryReply> primaryRepliesProcess() {
        ArrayList<PrimaryReply> result = new ArrayList<>();
        Flag:
        for (Replies replies : replies) {
            String temp = replies.getReplyContent();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getContent().equals(temp)) continue Flag;
            }
            result.add(new PrimaryReply(replies.getPostID(), replies.getReplyContent(), replies.getReplyStars(), replies.getReplyAuthor()));
            replyIndex.put(replies.getReplyContent(), result.size());
        }
        return result;
    }

    public static ArrayList<Favorites> favoritesProcess() {
        ArrayList<Favorites> result = new ArrayList<>();
        for (Post post : Posts) {
            int temp = post.getPostID();
            for (int i = 0; i < post.getAuthorFavorite().size(); i++) {
                result.add(new Favorites(temp, post.getAuthorFavorite().get(i)));
            }
        }
        return result;
    }

    public static ArrayList<Likes> likesProcess() {
        ArrayList<Likes> result = new ArrayList<>();
        for (Post post : Posts) {
            int temp = post.getPostID();
            for (int i = 0; i < post.getAuthorLiked().size(); i++) {
                result.add(new Likes(temp, post.getAuthorLiked().get(i)));
            }
        }
        return result;
    }

    public static ArrayList<Shares> sharesProcess() {
        ArrayList<Shares> result = new ArrayList<>();
        for (Post post : Posts) {
            int temp = post.getPostID();
            for (int i = 0; i < post.getAuthorShared().size(); i++) {
                result.add(new Shares(temp, post.getAuthorShared().get(i)));
            }
        }
        return result;
    }

    public static ArrayList<CategoryDetails> categoryDetailsProcess() {
        ArrayList<CategoryDetails> result = new ArrayList<>();
        for (Post post : Posts) {
            for (int i = 0; i < post.getCategory().size(); i++) {
                String temp = post.getCategory().get(i);
                result.add(new CategoryDetails(post.getPostID(), categoryIndex.get(temp)));
            }
        }
        return result;
    }

    public static ArrayList<Posts> postsProcess() {
        ArrayList<Posts> result = new ArrayList<>();
        for (Post post : Posts) {
            Timestamp data = Timestamp.valueOf(post.getPostingTime());
            result.add(new Posts(post.getPostID(), post.getTitle(), post.getContent(), data, post.getPostingCity(), post.getAuthor()));
        }
        return result;
    }

    public static ArrayList<String> categoriesProcess() {
        ArrayList<String> result = new ArrayList<>();
        for (Post post : Posts) {

            Flag:
            for (int i = 0; i < post.getCategory().size(); i++) {
                String temp = post.getCategory().get(i);
                for (int j = 0; j < result.size(); j++) {
                    if (temp.equals(result.get(j))) continue Flag;
                }
                result.add(temp);
                categoryIndex.put(temp, result.size());
            }
        }
        return result;
    }

    public static ArrayList<Author> authorProcess() {
        ArrayList<Author> result = new ArrayList<>();
        Random random = new Random();
        for (Post post : Posts) {
            Timestamp data = Timestamp.valueOf(post.getAuthorRegistrationTime());

            result.add(new Author(post.getAuthorID(), post.getAuthor(), data, post.getAuthoPhone()));
        }
        //先处理出现过的部分
        for (Post post : Posts) {

            Flag1:
            for (String temp : post.getAuthorFavorite()) {
                for (int i = 0; i < result.size(); i++) {
                    if (temp.equals(result.get(i).getName())) continue Flag1;
                }
                String time = getRandomTime("1949-04-22", "2023-04-22");
                Timestamp data = Timestamp.valueOf(time);
                result.add(new Author(generateRandomID(18), temp, data, "1" + generateRandomPhone(10)));
            }

            Flag2:
            for (String temp : post.getAuthorLiked()) {
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getName().equals(temp)) continue Flag2;
                }
                Timestamp data = Timestamp.valueOf(getRandomTime("1949-04-22", "2023-04-22"));
                result.add(new Author(generateRandomID(18), temp, data, "1" + generateRandomPhone(10)));
            }

            Flag3:
            for (String temp : post.getAuthorShared()) {
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getName().equals(temp)) continue Flag3;
                }
                Timestamp data = Timestamp.valueOf(getRandomTime("1949-04-22", "2023-04-22"));
                result.add(new Author(generateRandomID(18), temp, data, "1" + generateRandomPhone(10)));
            }

            Flag4:
            for (String temp : post.getAuthorFollowedBy()) {
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getName().equals(temp)) continue Flag4;
                }
                Timestamp data = Timestamp.valueOf(getRandomTime("1949-04-22", "2023-04-22"));
                result.add(new Author(generateRandomID(18), temp, data, "1" + generateRandomPhone(10)));
            }
        }
        //可能没有出现过的Author
        Flag5:
        for (Replies replies : replies) {
            String temp = replies.getReplyAuthor();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getName().equals(temp)) continue Flag5;
            }
            Timestamp data = Timestamp.valueOf(getRandomTime("1949-04-22", "2023-04-22"));
            result.add(new Author(generateRandomID(18), temp, data, "1" + generateRandomPhone(10)));
        }
        Flag6:
        for (Replies replies : replies) {
            String temp = replies.getSecondaryReplyAuthor();
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getName().equals(temp)) continue Flag6;
            }
            Timestamp data = Timestamp.valueOf(getRandomTime("1949-04-22", "2023-04-22"));
            result.add(new Author(generateRandomID(18), temp, data, "1" + generateRandomPhone(10)));
        }


        return result;
    }

    public static ArrayList<Follows> followsProcess() {
        ArrayList<Follows> result = new ArrayList<>();
        for (Post post : Posts) {
            String followed = post.getAuthor();
            for (int i = 0; i < post.getAuthorFollowedBy().size(); i++) {
                String temp = post.getAuthorFollowedBy().get(i);
                result.add(new Follows(temp, followed));
            }
        }
        return result;
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