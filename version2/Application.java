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
                    String menu =
                                    "  1: 'post'\n" +
                                    "  2: 'show-post'\n" +
                                    "  3: 'show-post-detail'\n" +
                                    "  4: 'show-replied-post'\n" +
                                    "  5: 'search-post-by-multi-parameter'\n" +
                                    "  6: 'show-hotList'\n" +
                                    "  7: 'show-post-with-photo'\n" +
                                    "  8: 'show-post-with-video'\n" +
                                    "  9: 'show-post-with-both-photo-and-video'\n" +
                                    " 10: 'show-my-block-list'\n" +
                                    " 11: 'show-my-shield-list'\n" +
                                    " 12: 'show-my-post'\n" +
                                    " 13: 'show-my-reply'\n" +
                                    " 14: 'show-my-follow-list'\n" +
                                    " 15: 'show-my-like-list'\n" +
                                    " 16: 'show-my-favor-list'\n" +
                                    " 17: 'show-my-share-list'\n" +
                                    " 18: 'show-my-phone'\n"+
                                    " 19: 'change-phone-pw'\n" +
                                    " 20: 'block'\n" +
                                    " 21: 'shield'\n" +
                                    " 22: 'unblock'\n" +
                                    " 23: 'unshield'\n" +
                                    " 24: 'follow'\n" +
                                    " 25: 'unfollow'\n" +
                                    " 26: 'logoff'";

                    {
                    String[] lines = menu.split("\n");
                    int maxLength = 0;
                    for (String line : lines) {
                        maxLength = Math.max(maxLength, line.length());
                    }

                    String horizontalLine = "*" + "*".repeat(maxLength * 3 + 4) + "*";
                    System.out.println(horizontalLine);
                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Welcome to the playground!") + "   *");
                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "What do you want to do?") + "   *");
                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
                    System.out.println(horizontalLine);
                    System.out.println();

                    int lineLength = maxLength * 3 + 4;
                    int centerOffset = (lineLength - 10) / 2; // 计算居中偏移量

                    String Header = "*" + "*".repeat(centerOffset - 1) + " Playground " + "*".repeat(centerOffset-1) + "*";

                    System.out.println(Header);
                    int count = 0;
                    for (String line : lines) {
                        if (count % 3 == 0) {
                            System.out.print("* ");
                        }
                        System.out.print(String.format("%-" + maxLength + "s", line) + " ");
                        count++;
                        if (count % 3 == 0) {
                            System.out.println("*");
                        }
                    }
                    if (count % 3 != 0) {
                        System.out.println(" ".repeat((3 - (count % 3)) * (maxLength + 1)) + "*");
                    }
                    System.out.println(horizontalLine);
                }
                    String action = utils.getWord(scanner);

                    switch (action) {
                        case "show-my-phone", "18": {
                            System.out.printf("Your phone number is: %s. Note that the default phone number is 12345678900. You can change it with change-phone-pw.\n", be.showMyPhone(usr.getName()).get(0));
                            break;
                        }
                        case "change-phone-pw", "19": {
                            System.out.println("Please input the phone number and the password you want to change, separated by a space, end with new line.");
                            String phone = scanner.next();
                            String pw = utils.getWord(scanner);
                            boolean succeed = be.changeAccountDetail(usr.getName(), phone, pw);
                            if (succeed) {
                                System.out.println("Your phone number and password have been successfully set up!!");
                            } else {
                                System.out.println("Sorry, there is some problem with the backend.");
                            }
                            break;
                        }
                        case "unblock", "22": {
                            System.out.println("Please input the account id you want to unblock:");
                            String name = utils.getWord(scanner);
                            be.unBlockAccount(name, usr.getName());
                            System.out.println("You unblock a person, thank you!");
                            break;
                        }
                        case "unshield", "23": {
                            System.out.println("Please input the account id you want to unshield:");
                            String name = utils.getWord(scanner);
                            be.unShieldAccount(name, usr.getName());
                            System.out.println("You unshield a person, thank you!");
                            break;
                        }
                        case "block", "20": {
                            System.out.println("Please input the account id you want to block:");
                            String name = utils.getWord(scanner);
                            be.blockAccount(name, usr.getName());
                            System.out.println("You block a person, thank you!");
                            break;
                        }
                        case "shield", "21": {
                            System.out.println("Please input the account id you want to shield:");
                            String name = utils.getWord(scanner);
                            be.shieldAccount(name, usr.getName());
                            System.out.println("You shield a person, thank you!");
                            break;
                        }
                        case "logoff", "26": {
                            System.out.println("Bye from playground!");
                            stage = 0;
                            break;
                        }
                        case "show-post", "2":// TODO: show only the first n posts
                        {
                            boolean[] print = {false, true, false, true, true, true};
                            ArrayList<ArrayList<String>> temp = be.checkPosts(be.getAllPostIds(), usr.getName());

                            int currentIndex = 0; // 当前索引位置

                            String input = "next";
                            boolean first = true;
                            do {
                                if (!first) {
                                    System.out.print("Enter 'next' to print the ten values: ");
                                    input = scanner.next();
                                }
                                if (input.equalsIgnoreCase("next")) {
                                    int endIndex = Math.min(currentIndex + 10, temp.get(0).size());
                                    if (currentIndex == endIndex){
                                        System.out.println("There are no posts left");
                                        break;
                                    }
                                    ArrayList<String> temp1 = new ArrayList<>(temp.get(0).subList(currentIndex,endIndex));
                                    ArrayList<String> temp2 = new ArrayList<>(temp.get(1).subList(currentIndex,endIndex));
                                    ArrayList<String> temp3 = new ArrayList<>(temp.get(2).subList(currentIndex,endIndex));
                                    ArrayList<String> temp4 = new ArrayList<>(temp.get(3).subList(currentIndex,endIndex));
                                    ArrayList<String> temp5 = new ArrayList<>(temp.get(4).subList(currentIndex,endIndex));
                                    ArrayList<String> temp6 = new ArrayList<>(temp.get(5).subList(currentIndex,endIndex));

                                    ArrayList<ArrayList<String>> out = new ArrayList<>();
                                    out.add(temp1);
                                    out.add(temp2);
                                    out.add(temp3);
                                    out.add(temp4);
                                    out.add(temp5);
                                    out.add(temp6);

                                    utils.printArray2(out, "post", print, postColumns);
                                    currentIndex = endIndex;
                                }
                                first = false;
                            } while (input.equalsIgnoreCase("next"));


                            System.out.println("You can see the post detail with postID");
                            break;
                        }
                        case "show-my-post", "12": {
                            ArrayList<ArrayList<String>> myPosts = be.checkPosts(be.showMyPostIDs(usr.getName()), usr.getName());
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
                        case "show-post-detail", "3": {
                            stage = 4;
                            System.out.println("Please input the post ID you want to take a closer look.");
                            ArrayList<Integer> postIDs = new ArrayList<>(); // TODO: simplify
                            String[] input = utils.getWord(scanner).split(" ");
                            ArrayList<Integer> postWithPhoto = be.getAllPostIdsWithPhoto();
                            ArrayList<Integer> postWithVideo = be.getAllPostIdsWithVideo();
                            int detail = Integer.parseInt(input[0]);
                            boolean photo = postWithPhoto.contains(detail);
                            boolean video = postWithVideo.contains(detail);
                            if (photo && video){
                                be.retrieveAndDisplayPhotos(detail);
                                be.retrieveAndDisplayVideosWithStreamingChunks(detail);
                            }
                            else if (photo && !video) be.retrieveAndDisplayPhotos(detail);
                            else if (!photo && video) be.retrieveAndDisplayVideosWithStreamingChunks(detail);
                            for (String id : input) {
                                postIDs.add(Integer.parseInt(id));
                            }
                            ArrayList<ArrayList<String>> postInfo = be.checkPosts(postIDs, usr.getName());
                            if (postInfo.get(1).size() == 0) {
                                System.out.println("Opps , it seems you can't see this post");
                                stage = 3;
                            } else
                                post = new Post(postIDs.get(0), postInfo.get(1).get(0), postInfo.get(2).get(0), postInfo.get(3).get(0), postInfo.get(4).get(0), postInfo.get(5).get(0));
                            break;
                        }
                        case "search-post-by-multi-parameter", "5": {
                            System.out.println("Please enter the keywords (enter an empty line if you don't want it)");
                            String keywords = scanner.nextLine();
                            System.out.println("Please enter the start and end time (enter an empty line if you don't want it)");
                            String startTime = scanner.nextLine();
                            String endTime = scanner.nextLine();
                            System.out.println("Please enter the city (enter an empty line if you don't want it)");
                            String city = scanner.nextLine();
                            System.out.println("Please enter the author's name (enter an empty line if you don't want it)");
                            String authorName = scanner.nextLine();
                            ArrayList<ArrayList<String>> postInfo = be.checkPostsMultiParameter(keywords, startTime, endTime, city, authorName);
                            if (postInfo.get(0).size() != 0) {
                                boolean[] print = {false, true, false, true, true, true};
                                utils.printArray2(postInfo, "selected post", print, postColumns);
                            } else System.out.println("There is no such post");
                            break;
                        }
                        case "show-my-reply", "13": {
                            ArrayList<ArrayList<String>> myReplies = be.checkReplies(be.showMyReplyIDs(usr.getName()), usr.getName());
                            if (myReplies.get(0).size() == 0) {
                                System.out.println("Oops! You have not replied to any post yet. Let start replying!");
                            } else {// TODO: test!!
                                boolean[] print = {true, false, true, true, true, true};
                                utils.printArray2(myReplies, "My reply", print, replyColumns);
                                System.out.println("You can see the reply detail with replyID");
                            }
                            break;
                        }
                        case "show-replied-post", "4": {
                            ArrayList<ArrayList<String>> repliedPost = be.checkPosts(be.showMyRepliedPostIDs(usr.getName()), usr.getName());
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
                        case "show-my-follow-list", "14": {
                            ArrayList<String> followList = be.showFollowNameList(usr.getName());
                            if (followList.size() == 0) {
                                System.out.println("You have not followed anyone. Start following!");
                            } else {
                                utils.printArray(be.showFollowNameList(usr.getName()), "My follow list");
                            }
                            break;
                        }
                        case "show-my-block-list", "10": {
                            boolean[] print = {true};
                            ArrayList<String> followList = be.getBlockedAccount(usr.getName());
                            if (followList.size() == 0) {
                                System.out.println("You have not block anyone. Start blocking!");
                            } else {
                                utils.printArray(be.getBlockedAccount(usr.getName()), "My block list");
                            }
                            break;
                        }
                        case "show-my-shield-list", "11": {
                            boolean[] print = {true};
                            ArrayList<String> followList = be.getShieldAccount(usr.getName());
                            if (followList.size() == 0) {
                                System.out.println("You have not shield anyone. Start shielding!");
                            } else {
                                utils.printArray(be.getShieldAccount(usr.getName()), "My shield list");
                            }
                            break;
                        }
                        case "show-my-like-list", "15": {
                            boolean[] print = {true, true, false, true, true, true};
                            ArrayList<ArrayList<String>> temp = be.checkPosts(be.showLFSIDList(usr.getName(), "liked"), usr.getName());
                            if (temp.get(0).size() == 0) {
                                System.out.println("You haven't like any post yet");
                                break;
                            }
                            utils.printArray2(temp, "Like list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "show-my-favor-list", "16": {
                            boolean[] print = {true, true, false, true, true, true};
                            ArrayList<ArrayList<String>> temp = be.checkPosts(be.showLFSIDList(usr.getName(), "favored"), usr.getName());
                            if (temp.get(0).size() == 0) {
                                System.out.println("You haven't favored any post yet");
                                break;
                            }
                            utils.printArray2(temp, "Favor list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "show-my-share-list", "17": {
                            boolean[] print = {true, true, false, true, true, true};
                            ArrayList<ArrayList<String>> temp = be.checkPosts(be.showLFSIDList(usr.getName(), "shared"), usr.getName());
                            if (temp.get(0).size() == 0) {
                                System.out.println("You haven't shared any post yet");
                                break;
                            }
                            utils.printArray2(temp, "Share list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "show-hotList", "6": {
                            boolean[] print = {true, true, false, true, true, true};
                            utils.printArray2(be.checkPosts(be.showHotlist(), usr.getName()), "Share list", print, postColumns);
                            System.out.println("You can take a deeper look with 'show-post-detail'");
                            break;
                        }
                        case "follow", "24": {
                            System.out.println("Please input the account name you want to follow:");
                            String name = utils.getWord(scanner);
                            if (be.ifFollowed(usr.getName(), name)) {
                                System.out.println("You have followed this minion. Why you like him/her/it so much wuuuu.");
                            } else {
                                be.followAccount(usr.getName(), name);
                                System.out.println("You follow a new person, thank you!");
                            }
                            break;
                        }
                        case "unfollow", "25": {
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
                        case "post", "1": {
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

                            System.out.println("Do you want to attach any image or video to the post?(Enter 'i' to attach a photo , 'v' to attach a video \n 'b' for both of them and 'n' for no)");
                            String op = utils.getWord(scanner);

                            boolean postSafe;
                            postSafe = be.post(title, contentBuffer, city, usr.getName(), input, anonymous,op);
                            System.out.println("Hurry!!!!! You just post a post.");

                            if (!postSafe) {
                                System.out.println("Something is wrong in our backend. Our sincere apology!");
                            }
                        }
                        case "show-post-with-photo", "7": {
                            ArrayList<ArrayList<String>> myPosts = be.checkPosts(be.getAllPostIdsWithPhoto(), usr.getName());
//                            System.out.println("content" + myPosts);
                            if (myPosts.get(0).size() == 0) {
                                System.out.println("Oops! There is no post with photo");
                            } else {
                                boolean[] print = {false, true, false, true, true, true};
                                utils.printArray2(myPosts, "Posts with photo", print, postColumns);
                                System.out.println("You can see the post detail with postID");
                            }
                            break;
                        }
                        case "show-post-with-video", "8": {
                            ArrayList<ArrayList<String>> myPosts = be.checkPosts(be.getAllPostIdsWithVideo(), usr.getName());
//                            System.out.println("content" + myPosts);
                            if (myPosts.get(0).size() == 0) {
                                System.out.println("Oops! There is no post with photo");
                            } else {
                                boolean[] print = {false, true, false, true, true, true};
                                utils.printArray2(myPosts, "Posts with video", print, postColumns);
                                System.out.println("You can see the post detail with postID");
                            }
                            break;
                        }
                        case "show-post-with-photo-and-video", "9": {
                            ArrayList<ArrayList<String>> myPosts = be.checkPosts(be.getAllPostIdsWithBothPhotoAndVideo(), usr.getName());
//                            System.out.println("content" + myPosts);
                            if (myPosts.get(0).size() == 0) {
                                System.out.println("Oops! There is no post with photo");
                            } else {
                                boolean[] print = {false, true, false, true, true, true};
                                utils.printArray2(myPosts, "Posts with both photo and video", print, postColumns);
                                System.out.println("You can see the post detail with postID");
                            }
                            break;
                        }
                    }
                    break;
                }
                case 4: // post
                {
                    String menu =
                            "  1: 'like'\n" +
                                    "  2: 'favor'\n" +
                                    "  3: 'share'\n" +
                                    "  4: 'follow'\n" +
                                    "  5: 'show-reply'\n" +
                                    "  6: 'see-reply-detail'\n" +
                                    "  7: 'reply'\n" +
                                    "  8: 'back'\n";

                    {
                        String[] lines = menu.split("\n");
                        int maxLength = 0;
                        for (String line : lines) {
                            maxLength = Math.max(maxLength, line.length());
                        }

                        String horizontalLine = "*" + "*".repeat(maxLength * 3 + 4) + "*";
                        int lineLength = maxLength * 3 + 4;
                        String heading = "PostId : " + String.valueOf(post.getPostID());
                        int centerOffset = (lineLength - heading.length()) / 2;

                        String Header = "*" + "*".repeat(centerOffset - 1) + " " + heading + " " + "*".repeat(centerOffset - 1) + "*";

                        System.out.println(Header);
                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Title: " + post.getTitle()) + "   *");
                        System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Content:") + "   *");
                        printWrappedContent(post.getContent(), maxLength * 3);
                        System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Author: " + post.getPost_account_name()) + "   *");
                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "City: " + post.getCity()) + "   *");
                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Time: " + post.getDatetime()) + "   *");
                        System.out.println(horizontalLine);
                        System.out.println();

                        System.out.println(horizontalLine);
                        int count = 0;
                        for (String line : lines) {
                            if (count % 3 == 0) {
                                System.out.print("* ");
                            }
                            System.out.print(String.format("%-" + maxLength + "s", line) + " ");
                            count++;
                            if (count % 3 == 0) {
                                System.out.println("*");
                            }
                        }
                        if (count % 3 != 0) {
                            System.out.println(" ".repeat((3 - (count % 3)) * (maxLength + 1)) + "*");
                        }
                        System.out.println(horizontalLine);
                    }
                    String action = utils.getWord(scanner);


                    switch (action) {
                        case "like", "1": {
                            be.lfsPost(usr.getName(), post.getPostID(), "liked");
                            System.out.println("You liked it, thank you!");
                            break;
                        }
                        case "favor", "2": {
                            be.lfsPost(usr.getName(), post.getPostID(), "favored");
                            System.out.println("You favored it, thank you!");
                            break;
                        }
                        case "share", "3": {
                            be.lfsPost(usr.getName(), post.getPostID(), "shared");
                            System.out.println("You shared it, thank you!");
                            break;
                        }
                        case "follow", "4": {
                            if (be.ifFollowed(usr.getName(), post.getPost_account_name())) {
                                System.out.println("You already followed this minion. Don't love him/her/it too much.");
                            } else {
                                be.followAccount(usr.getName(), post.getPost_account_name());
                                System.out.println("You follow the post author, thank you!");
                            }
                            break;
                        }
                        case "show-reply", "5": {
                            boolean[] print = {true, false, true, true, true, true};
                            ArrayList<Integer> firstLevelReplyList = be.showReply(post.getPostID(), true);
                            if (firstLevelReplyList.size() == 0) {
                                System.out.println("There are no replies attached to this post.");
                            } else {
                                utils.printArray2(be.checkReplies(firstLevelReplyList, usr.getName()), "Reply list", print, replyColumns);
                                System.out.printf("There are %d first-level reply to this post\n", firstLevelReplyList.size());
                                System.out.println("Your can see reply detail by 'see-reply-detail'.");
                            }
                            break;
                        }
                        case "see-reply-detail", "6": {
                            System.out.println("Please input the id of the reply you want to see");
                            int id = Integer.parseInt(utils.getWord(scanner));
                            ArrayList<Integer> replyIDList = new ArrayList<>();
                            replyIDList.add(id);
                            ArrayList<ArrayList<String>> replyInfo = be.checkReplies(replyIDList, usr.getName());
                            if (replyInfo.get(1).size() == 0) {
                                System.out.println("Opps , it seems you can't see this reply");
                                stage = 3;
                            } else
                                reply = new Reply(id, Integer.parseInt(replyInfo.get(1).get(0)), replyInfo.get(2).get(0), Integer.parseInt(replyInfo.get(3).get(0)), Integer.parseInt(replyInfo.get(4).get(0)), replyInfo.get(5).get(0));
                            stage = 5;
                            break;
                        }
                        case "reply", "7": {
                            System.out.println("Please input the content of your reply. End with a new line.");
                            String content = utils.getLine(scanner);

                            //是否匿名的检测
                            System.out.println("Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
                            String Ano = utils.getWord(scanner);
                            boolean anonymous = Ano.equals("y") ? true : false;


                            be.replyPost(-1, content, post.getPostID(), usr.getName(), anonymous);
                            System.out.println("You just replied to a post. Thank you!");
                            break;
                        }
                        case "back", "8": {
                            stage--;
                            break;
                        }
                    }
                    break;
                }
                case 5: // reply TODO:
                {
                    String menu =
                            "  1: 'follow'\n" +
                                    "  2: 'show-secondary-reply'\n" +
                                    "  3: 'see-secondary-reply-detail'\n" +
                                    "  4: 'reply-to-reply'\n" +
                                    "  5: 'back'\n" +
                                    "  6: 'star-the-current-reply'\n";

                    String[] lines = menu.split("\n");
                    int maxLength = 0;
                    for (String line : lines) {
                        maxLength = Math.max(maxLength, line.length());
                    }
                    boolean secondary = (reply.getReplyID() <= 0) ? false : true;
                    String level = secondary ? "Secondary " : "Primary ";

                    String horizontalLine = "*" + "*".repeat(maxLength * 3 + 4) + "*";
                    int lineLength = maxLength * 3 + 4;
                    String heading = level + "Reply Id : " + String.valueOf(reply.getId());
                    int centerOffset = (lineLength - heading.length()) / 2;

                    String Header = "*" + "*".repeat(centerOffset - 1) + " " + heading + " " + "*".repeat(centerOffset-1) + "*";

                    System.out.println(Header);
                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Author: " + reply.getAuthor_account_name()) + "   *");
                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Stars: " + reply.getStars()) + "   *");
                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Content: ") + "   *");
                    printWrappedContent(reply.getContent(), maxLength * 3);
                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Post Id: " + reply.getPostID()) + "   *");
                    if (secondary) {
                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s", "Parent Reply Id: " + reply.getReplyID()) + "   *");
                    }
                    System.out.println(horizontalLine);
                    System.out.println();


                    System.out.println(horizontalLine);
                    int count = 0;
                    for (String line : lines) {
                        if (count % 3 == 0) {
                            System.out.print("* ");
                        }
                        System.out.print(String.format("%-" + maxLength + "s", line) + " ");
                        count++;
                        if (count % 3 == 0) {
                            System.out.println("*");
                        }
                    }
                    if (count % 3 != 0) {
                        System.out.println(" ".repeat((3 - (count % 3)) * (maxLength + 1)) + "*");
                    }
                    System.out.println(horizontalLine);

                    String action = utils.getWord(scanner);
                    switch (action) {
                        case "back","5": {
                            stage--;
                            break;
                        }
                        case "follow","1": {
                            if (be.ifFollowed(usr.getName(), reply.getAuthor_account_name())) {
                                System.out.println("You already followed this minion. Don't love him/her/it too much.");
                            } else {
                                be.followAccount(usr.getName(), reply.getAuthor_account_name());
                                System.out.println("You follow a reply author.");
                            }
                            break;
                        }
                        case "show-secondary-reply","2": {
                            ArrayList<Integer> secondaryReplyList = be.showReply(reply.getId(), false);
                            if (secondaryReplyList.size() != 0) {
                                boolean[] print = {true, false, true, true, true, true};
                                utils.printArray2(be.checkReplies(secondaryReplyList, usr.getName()), "Secondary reply list", print, replyColumns);
                                System.out.printf("There are %d secondary replies.\n", secondaryReplyList.size());
                                System.out.println("Your can see reply detail by 'see-secondary-reply-detail'.");
                                break;
                            } else {
                                System.out.println("There are no secondary replies to this reply.");
                            }
                            break;
                        }
                        case "see-secondary-reply-detail","3": {
                            System.out.println("Please input the id of the secondary reply.");
                            int id = Integer.parseInt(utils.getWord(scanner));
                            ArrayList<Integer> replyIDList = new ArrayList<>();
                            replyIDList.add(id);
                            ArrayList<ArrayList<String>> replyInfo = be.checkReplies(replyIDList, usr.getName());
                            if (replyInfo.get(1).size() == 0) {
                                System.out.println("Opps , it seems you can't see this reply");
                                stage = 3;
                            } else
                                reply = new Reply(id, Integer.parseInt(replyInfo.get(1).get(0)), replyInfo.get(2).get(0), Integer.parseInt(replyInfo.get(3).get(0)), Integer.parseInt(replyInfo.get(4).get(0)), replyInfo.get(5).get(0));
                            break;
                        }
                        case "reply-to-reply","4": // TODO: check for case break
                        {
                            System.out.println("Please input the content of your reply. End with a new line.");
                            String content = utils.getLine(scanner);

                            //是否匿名的检测
                            System.out.println("Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
                            String Ano = utils.getWord(scanner);
                            boolean anonymous = Ano.equals("y") ? true : false;

                            be.replyPost(reply.getId(), content, post.getPostID(), usr.getName(), anonymous);
                            System.out.println("You just replied to a reply. Thank you!");
                            break;
                        }
                        case "star-the-current-reply","6": {
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

    public static void printWrappedContent(String content, int maxLength) {
        if (content.length() <= maxLength) {
            System.out.println("* " + String.format("%-" + maxLength + "s", content) + "   *");
        } else {
            int endIndex = content.lastIndexOf(" ", maxLength);
            if (endIndex == -1) {
                endIndex = maxLength;
            }
            System.out.println("* " + String.format("%-" + maxLength + "s", content.substring(0, endIndex)) + "   *");
            printWrappedContent(content.substring(endIndex).trim(), maxLength);
        }
    }

}

