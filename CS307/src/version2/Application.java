package version2;//package project2;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.util.Scanner;  // 导入Scanner

public class Application {
    private static String[] postColumns = {"postID", "title", "content", "datetime", "city", "post_account_name"};
    private static String[] replyColumns = {"id", "replyId", "content", "starNum", "postID", "authorAccountName"};
    private static boolean first = true;

    protected static Connection con = null; // TODO: connection pool

    private static Backend be = null;

    private static User usr = null;

    private static Post post = null;

    private static Reply reply = null;

    private static int stage = 0; // The stage the app is at. Initially it is at 0

    private static void openDB(Properties prop) { // create con with prop
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
                con.close();
                con = null;
            } catch (Exception ignored) { // TODO: exception handling
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
        Utils utils = new Utils();
        Scanner scanner = new Scanner(System.in);  // 创建Scanner对象
        be = new Backend(con, false); // TODO: connection pool and connection check
        /*
        Actions here
        TODO: fozzy matching for inputs
         */
        while (stage >= 0) {
            switch (stage) {
                case 0: {
                    if (first) {
                        System.out.println("Hi there! Do you want to login or register? ['login','register']");
                        first = false;
                    } else
                        System.out.println("Please select the action you want to perform: ['login','register','quit']");
                    String action = utils.getWord(scanner);
                    if (action.equals("register")) stage = 1; // 1: register
                    else if (action.equals("login")) stage = 2; // 2: login
                    else if (action.equals("quit")) {
                        System.out.println("Hope you enjoy surfing the blog! See you next time!");
                        stage = -1;
                    } else System.out.println("Please input one of the following ['login','register']");
                    break;
                }
                case 1: {
                    System.out.println("Great! Please input the username you want and then a space followed by your password"); //TODO: check for password(twice, complexity, ...)
                    String userName = scanner.next();
                    String passWord = utils.getWord(scanner);
                    System.out.println("Hurray! Let us check if your input username is unoccupied.");
                    boolean succeed = be.addAccount(userName, utils.generateRandomID(18), "12345678900", passWord);
                    if (succeed) {
                        System.out.println("Congrats! Your account has been successfully created!");
                        stage = 0;
                    } else {
                        System.out.printf("Oops, the username %s has been used, pick a new one!\n", userName);
                    }
                    break;
                }
                case 2: {
                    System.out.println("Please input your user name and password, separated by a space.");
                    String userName = scanner.next();
                    String passWord = utils.getWord(scanner);
                    boolean succeed = be.verifyPw(userName, passWord);
                    if (succeed) {
                        System.out.printf("Account login successful!\n");
                        ArrayList<String> infoList = be.getAccountInfo(userName);
                        usr = new User(userName, infoList.get(0), infoList.get(1), infoList.get(2), passWord, true);
//                        System.out.printf("%s %s, %s, %s %s %s\n",usr.getName(), usr.getAccount_id(), usr.getRegTime(), usr.getPhone(), usr.getPw(), usr.isLogin());
                        stage = 3;
                    } else {
                        System.out.println("Oops! The username you input does not exist or the username and password do not match.");
                        System.out.println("Do you want to retrieve to last layer or try login again? ['back','try-again']");
                        String action = utils.getWord(scanner);
                        if (action.equals("back")) {
                            stage = 0;
                        } else if (action.equals("try-again")) {
                            System.out.println("Nvm! Try again!");
                        } else {
                            System.out.println("We don't recognize your input. I will let you try again.");
                        }
                    }
                    break;
                }
                case 3: {
                    System.out.println("Welcome to the playground! What do you want to do? "
                            + "['show-post','show-my-post', 'show-post-detail','show-my-reply', 'search-post-by-multi-parameter','show-replied-post', 'show-my-follow-list', " +
                            "\n'show-my-like-list','show-my-favor-list','show-my-share-list','show-hotList','block','shield','unblock','unshield'" +
                            "\n'follow', 'unfollow','post','logoff']");
                    String action = utils.getWord(scanner);
                    switch (action) {
                        case "unblock":{
                            System.out.println("Please input the account id you want to unblock:");
                            String name = utils.getWord(scanner);
                            be.unBlockAccount(name, usr.getName());
                            System.out.println("You unblock a person, thank you!");
                            break;
                        }
                        case "unshield":{
                            System.out.println("Please input the account id you want to unshield:");
                            String name = utils.getWord(scanner);
                            be.unShieldAccount(name, usr.getName());
                            System.out.println("You unshield a person, thank you!");
                            break;
                        }
                        case "block":{
                            System.out.println("Please input the account id you want to block:");
                            String name = utils.getWord(scanner);
                            be.blockAccount(name, usr.getName());
                            System.out.println("You block a person, thank you!");
                            break;
                        }
                        case "shield":{
                            System.out.println("Please input the account id you want to shield:");
                            String name = utils.getWord(scanner);
                            be.shieldAccount(name, usr.getName());
                            System.out.println("You shield a person, thank you!");
                            break;
                        }
                        case "logoff": {
                            System.out.println("Bye from playground!");
                            stage = 0;
                            break;
                        }
                        case "show-post":// TODO: show only the first n posts
                        {
                            boolean[] print = {false, true, false, true, true, true};
                            utils.printArray2(be.checkPosts(be.getAllPostIds(),usr.getName()), "post", print, postColumns);
                            System.out.println("You can see the post detail with postID");
                            break;
                        }
                        case "show-my-post": {
                            ArrayList<ArrayList<String>> myPosts = be.checkPosts(be.showMyPostIDs(usr.getName()),usr.getName());
//                            System.out.println("content" + myPosts);
                            if (myPosts.get(0).size() == 0) {
                                System.out.println("Oops! You have not posted anything yet. Let start posting!");
                            } else {
                                boolean[] print = {false, true, false, true, true, true};
                                utils.printArray2(myPosts, "My post", print, postColumns);
                                System.out.println("You can see the post detail with postID");
                            }
                            break;
                        }
                        case "show-post-detail": {
                            stage = 4;
                            System.out.println("Please input the post ID you want to take a closer look.");
                            ArrayList<Integer> postIDs = new ArrayList<>(); // TODO: simplify
                            String[] input = utils.getWord(scanner).split(" ");
                            for (String id : input) {
                                postIDs.add(Integer.parseInt(id));
                            }
                            ArrayList<ArrayList<String>> postInfo = be.checkPosts(postIDs,usr.getName());
                            post = new Post(postIDs.get(0), postInfo.get(1).get(0), postInfo.get(2).get(0), postInfo.get(3).get(0), postInfo.get(4).get(0), postInfo.get(5).get(0));
                            break;
                        }
                        case "search-post-by-multi-parameter":{
                            System.out.println("Please enter the keywords (enter an empty line if you don't want it)");
                            String keywords = scanner.nextLine();
                            System.out.println("Please enter the start and end time (enter an empty line if you don't want it)");
                            String startTime = scanner.nextLine();
                            String endTime = scanner.nextLine();
                            System.out.println("Please enter the city (enter an empty line if you don't want it)");
                            String city = scanner.nextLine();
                            System.out.println("Please enter the author's name (enter an empty line if you don't want it)");
                            String authorName = scanner.nextLine();
                            ArrayList<ArrayList<String>> postInfo = be.checkPostsMultiParameter(keywords,startTime,endTime,city,authorName) ;
                            boolean[] print = {false, true, false, true, true, true};
                            utils.printArray2(postInfo,"selected post",print,postColumns);
                            break;
                        }
                        case "show-my-reply": {
                            ArrayList<ArrayList<String>> myReplies = be.checkReplies(be.showMyReplyIDs(usr.getName()),usr.getName());
//                            System.out.println("content" + myPosts);
                            if (myReplies.get(0).size() == 0) {
                                System.out.println("Oops! You have not replied to any post yet. Let start replying!");
                            } else {// TODO: test!!
                                boolean[] print = {true, false, true, true, true, true};
                                utils.printArray2(myReplies, "My reply", print, replyColumns);
                                System.out.println("You can see the reply detail with replyID");
                            }
                            break;
                        }
                        case "show-replied-post": {
                            ArrayList<ArrayList<String>> repliedPost = be.checkPosts(be.showMyRepliedPostIDs(usr.getName()),usr.getName());
//                            System.out.println("content" + myPosts);
                            if (repliedPost.get(0).size() == 0) {
                                System.out.println("Oops! You have not replied to anything yet. Let's start replying!");
                            } else {
                                boolean[] print = {false, true, false, true, true, true};
                                utils.printArray2(repliedPost, "You have replied to these posts", print, postColumns);
                                System.out.println("You can see the post detail with postID");
                            }
                            break;
                        }
                        case "show-my-follow-list": {
                            boolean[] print = {true};
                            ArrayList<String> followList = be.showFollowNameList(usr.getName());
                            if (followList.size() == 0) {
                                System.out.println("You have not followed anyone. Start following!");
                            } else {
                                utils.printArray(be.showFollowNameList(usr.getName()), "My follow list", print);
                            }
                            break;
                        }
                        case "show-my-like-list": {
                            boolean[] print = {true, true, false, true, true, true};
                            utils.printArray2(be.checkPosts(be.showLFSIDList(usr.getName(), "liked"),usr.getName()), "Like list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "show-my-favor-list": {
                            boolean[] print = {true, true, false, true, true, true};
                            utils.printArray2(be.checkPosts(be.showLFSIDList(usr.getName(), "favored"),usr.getName()), "Favor list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "show-my-share-list": {
                            boolean[] print = {true, true, false, true, true, true};
                            utils.printArray2(be.checkPosts(be.showLFSIDList(usr.getName(), "shared"),usr.getName()), "Share list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "show-hotList":{
                            boolean[] print = {true, true, false, true, true, true};
                            utils.printArray2(be.checkPosts(be.showHotlist(),usr.getName()), "Share list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "follow": {
                            System.out.println("Please input the account id you want to follow:");
                            String name = utils.getWord(scanner);
                            if (be.ifFollowed(usr.getName(), name)) {
                                System.out.println("You have followed this minion. Why you like him/her/it so much wuuuu.");
                            } else {
                                be.followAccount(usr.getName(), name);
                                System.out.println("You follow a new person, thank you!");
                            }
                            break;
                        }
                        case "unfollow": {
                            System.out.println("Please input the account name you want to unfollow:");
                            String name = utils.getWord(scanner);
                            if (!be.ifFollowed(usr.getName(), name)) {
                                System.out.println("You have not followed this minion. Why you hate him/her/it so much wuuuu.");
                            } else {
                                be.unfollowAccount(usr.getName(), name);
                                System.out.println("You unfollow an old friend. What's wrong!");
                            }
                            break;
                        }
                        case "post": {
                            System.out.println("Please enter the title below:");
                            String title = utils.getLine(scanner);
                            System.out.println("Please enter the content below. End with an empty line.");
                            String contentBuffer = "";
                            read:
                            while (scanner.hasNextLine()) {
                                String contentLine = scanner.nextLine();
                                if (contentLine == "" && contentBuffer != "") break read;
                                else contentBuffer += (contentLine + "\n");
                            }
                            contentBuffer.trim();
                            System.out.println("Please enter the city you are in honestly.");
                            String city = utils.getLine(scanner);
                            System.out.println("Please select one or more categories from the list: below");
                            ArrayList<String> catList = be.getAllCategories();
                            for (String catName : catList) {
                                System.out.printf("%s ", catName);
                            }
                            System.out.println();
                            boolean catNotSafe = true;
                            ArrayList<String> input = new ArrayList<>();
                            while (catNotSafe) {
                                System.out.println("Please enter the categories you think your post belongs to, separate them with space end with new line.");
                                input = new ArrayList<>(Arrays.asList(utils.getLine(scanner).split(" ")));
                                catNotSafe = false;
                                for (String cat : input) {
                                    if (!catList.contains(cat)) {
                                        System.out.printf("You enter a category that is not in the category list!! %s\n", cat);
                                        catNotSafe = true;
                                        break;
                                    }
                                }
                            }

                            //是否匿名的检测
                            System.out.println("Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
                            String Ano = utils.getWord(scanner);
                            boolean anonymous = Ano.equals("y") ? true : false;

                            boolean postSafe;
                            postSafe = be.post(title, contentBuffer, city, usr.getName(), input, anonymous);
                            System.out.println("Hurry!!!!! You just post a post.");

                            if (!postSafe) {
                                System.out.println("Something is wrong in our backend. Our sincere apology!");
                            }
                        }
                    }
                    break;
                }
                case 4: //post
                {
                    System.out.printf("Your are in post %s, ID: %d, author: %s\n", post.getTitle(), post.getPostID(), post.getPost_account_name());
                    System.out.printf("Post content is: %s\n", post.getContent());
                    System.out.println("Please select one of the actions: "
                            + "['like','favor','share','follow','show-reply','see-reply-detail','reply','back']");
                    String action = utils.getWord(scanner);
                    switch (action) {
                        case "like": {
                            be.lfsPost(usr.getName(), post.getPostID(), "liked");
                            System.out.println("You liked it, thank you!");
                            break;
                        }
                        case "favor": {
                            be.lfsPost(usr.getName(), post.getPostID(), "favored");
                            System.out.println("You favored it, thank you!");
                            break;
                        }
                        case "share": {
                            be.lfsPost(usr.getName(), post.getPostID(), "shared");
                            System.out.println("You shared it, thank you!");
                            break;
                        }
                        case "follow": {
                            if (be.ifFollowed(usr.getName(), post.getPost_account_name())) {
                                System.out.println("You already followed this minion. Don't love him/her/it too much.");
                            } else {
                                be.followAccount(usr.getName(), post.getPost_account_name());
                                System.out.println("You follow the post author, thank you!");
                            }
                            break;
                        }
                        case "show-reply": {
                            boolean[] print = {true, false, true, true, true, true};
                            if (be.showReplyIDs(post.getPostID()).size() == 0) {
                                System.out.println("There are no replies attached to this post.");
                            } else {
                                utils.printArray2(be.checkReplies(be.showReplyIDs(post.getPostID()),usr.getName()), "Reply list", print, replyColumns);
                                System.out.println("Your can see reply detail by 'see-reply-detail'.");
                            }
                            break;
                        }
                        case "see-reply-detail": {
                            System.out.println("Please input the id of the reply you want to see");
                            int id = Integer.parseInt(utils.getWord(scanner));
                            ArrayList<Integer> replyIDList = new ArrayList<>();
                            replyIDList.add(id);
                            ArrayList<ArrayList<String>> replyInfo = be.checkReplies(replyIDList,usr.getName());
                            reply = new Reply(id, Integer.parseInt(replyInfo.get(1).get(0)), replyInfo.get(2).get(0), Integer.parseInt(replyInfo.get(3).get(0)), Integer.parseInt(replyInfo.get(4).get(0)), replyInfo.get(5).get(0));
                            stage = 5;
                            break;
                        }
                        case "reply": {
                            System.out.println("Please input the content of your reply. End with a new line.");
                            String content = utils.getLine(scanner);

                            //是否匿名的检测
                            System.out.println("Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
                            String Ano = utils.getWord(scanner);
                            boolean anonymous = Ano.equals("y") ? true : false;


                            be.replyPost(-1, content, post.getPostID(), usr.getName(),anonymous);
                            System.out.println("You just replied to a post. Thank you!");
                            break;
                        }
                        case "back": {
                            stage--;
                            break;
                        }
                    }
                    break;
                }
                case 5: //reply TODO:
                {
                    boolean secondary = (reply.getId() <= 0) ? false : true;
                    System.out.printf("Your are in reply %d, secondary: %b, author: %s\n", reply.getId(), secondary, reply.getAuthor_account_name());
                    System.out.printf("Reply content is: %s\n", reply.getContent());
                    System.out.println("Please select one of the actions: "
                            + "['follow','show-secondary-reply','see-secondary-reply-detail','reply-to-reply','back','star-the-current-reply']");
                    String action = utils.getWord(scanner);
                    switch (action) {
                        case "back": {
                            stage--;
                            break;
                        }
                        case "follow": {
                            if (be.ifFollowed(usr.getName(), reply.getAuthor_account_name())) {
                                System.out.println("You already followed this minion. Don't love him/her/it too much.");
                            } else {
                                be.followAccount(usr.getName(), reply.getAuthor_account_name());
                                System.out.println("You follow a reply author.");
                            }
                            break;
                        }
                        case "show-secondary-reply": {
                            if (secondary) {
                                boolean[] print = {true, false, true, true, true, true};
                                ArrayList<Integer> replyIDList = new ArrayList<>();
                                replyIDList.add(reply.getId()); // secondary reply id is positive
                                utils.printArray2(be.checkReplies(replyIDList,usr.getName()), "Secondary reply list", print, replyColumns);
                                System.out.println("Your can see reply detail by 'see-secondary-reply-detail'.");
                                break;
                            } else {
                                System.out.println("There are no secondary replies to this reply.");
                            }
                            break;
                        }
                        case "see-secondary-reply-detail": {
                            System.out.println("Please input the id of the secondary reply.");
                            int id = Integer.parseInt(utils.getWord(scanner));
                            ArrayList<Integer> replyIDList = new ArrayList<>();
                            replyIDList.add(id);
                            ArrayList<ArrayList<String>> replyInfo = be.checkReplies(replyIDList,usr.getName());
                            reply = new Reply(id, Integer.parseInt(replyInfo.get(1).get(0)), replyInfo.get(2).get(0), Integer.parseInt(replyInfo.get(3).get(0)), Integer.parseInt(replyInfo.get(4).get(0)), replyInfo.get(5).get(0));
                            break;
                        }
                        case "reply-to-reply": // TODO: check for case break
                        {
                            System.out.println("Please input the content of your reply. End with a new line.");
                            String content = utils.getLine(scanner);

                            //是否匿名的检测
                            System.out.println("Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
                            String Ano = utils.getWord(scanner);
                            boolean anonymous = Ano.equals("y") ? true : false;

                            be.replyPost(reply.getId(), content, post.getPostID(), usr.getName(),anonymous);
                            System.out.println("You just replied to a reply. Thank you!");
                            break;
                        }
                        case "star-the-current-reply": {
                            be.starReply(reply.getId());
                            System.out.println("You just stared a reply. Thank you!");
                        }

                    }
                    break;
                }
                case -1: {
                    System.exit(0);
                    break;
                }
                default:
                    System.out.println("Default mode");
            }
        }

        closeDB();

        long end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms");
    }

}

