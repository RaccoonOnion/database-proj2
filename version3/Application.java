package version2;

import java.sql.Connection;

public class Application {
    private String[] postColumns = {"postID", "title", "content", "datetime", "city", "post_account_name"};
    private String[] replyColumns = {"id", "replyId", "content", "starNum", "postID", "authorAccountName"};

    /*
    Some String prompts
     */
    private final String stageNoMatch = "There is no stage to match\n";
    private final String subStageNoMatch = "There is no subStage to match\n";
    private final String pmpt0_0 = "Please select the action you want to perform: ['login','register','quit']\n";

    private final String pmpt0_1 = "Hope you enjoy surfing the blog! See you next time!\n";

    private final String pmpt1_1 = "Great! Please input the username you want and then a space followed by your password\n";

    private final String pmpt1_2 = "Congrats! Your account has been successfully created!\n";

    private final String pmpt1_3 = "Oops, the username %s has been used, pick a new one!\n";
    private final String pmpt2_1 = "Please input your user name and password, separated by a space.\n";
    private final String pmpt2_2_1 = "Account login successful!\n";
    private final String getPmpt2_2_2 = "Oops! The username you input does not exist or the username and password do not match.\n";
    private final String pmpt2_3 = "Do you want to retrieve to last layer or try login again? ['back','try-again']\n";
    private final String pmpt2_4 = "Nvm! Try again!\n";


    private boolean first = true;

    protected Connection con = null; // TODO: connection pool

    private Backend be = null;

    public User getUsr() {
        return usr;
    }

    public Post getPost() {
        return post;
    }

    public Reply getReply() {
        return reply;
    }

    private User usr = null;

    private Post post = null;

    private Reply reply = null;

    private Utils utils = null;

    public int getStage() {
        return stage;
    }

    public int getSubStage() {
        return subStage;
    }

    private int stage = 0; // The stage the app is at. Initially it is at 0

    private int subStage = 0;

    private String[] msg4Client;

    private String[] msgFromClient;

    public Application (Connection con, int stage, int subStage, User usr, Post post, Reply reply, String[] msgFromClient){
        this.con = con;
        this.stage = stage;
        this.subStage = subStage;
        this.usr = usr;
        this.post = post;
        this.reply = reply;
        this.msg4Client = null;
        this.msgFromClient = msgFromClient;

        this.be = new Backend(con, true); // TODO: connection pool and connection check
        this.utils = new Utils();

        // set first to false
        this.first = false;
    }

//    public void clear(){
//        this.con = null;
//        this.stage = 0;
//        this.subStage = 0;
//        this.be = null;
//        this.usr = null;
//        this.post = null;
//        this.reply = null;
//        this.utils = null;
//        this.msg4Client = null;
//        this.msgFromClient = null;
//        // set first to true
//        this.first = true;
//    }

    public boolean isFirst() {
        return first;
    }

    public String[] run() {
        /*
        Actions here
        TODO: fozzy matching for inputs
         */
        try {

            switch (stage) {
                case 0: {// login, register quit stage
                    switch (subStage) {
                        case 0: {// initial or not login and register
                            // {last stage number, last subStage number, prompt, mode}
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt0_0, "g2"};
                            stage = 0;
                            subStage = 4;
                            break;
                        }
                        case 1: {// register
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "", "n"};
                            stage = 1;
                            subStage = 1;
                            break;
                        }
                        case 2: {// login
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "" ,"n"};
                            stage = 2;
                            subStage = 1;
                            break;
                        }
                        case 3: {// quit
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt0_1, "n"};// TODO: socket Client quit
                            stage = -1;
                            subStage = -1;
                            break;
                        }
                        case 4:{
                            switch (msgFromClient[0]){
                                case "login":{
                                    subStage = 2;
                                    msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "", "n"};
                                    break;
                                }
                                case "register":{
                                    subStage = 1;
                                    msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "", "n"};
                                    break;
                                }
                                case "quit":{
                                    subStage = 3;
                                    msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "", "n"};
                                    break;
                                }
                                default:{
                                    subStage = 0;
                                    msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "", "n"};
                                    break;
                                }
                            }
                            break;
                        }
                        default:{
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), subStageNoMatch, "e"}; // e for error
                            // no change
                            break;
                        }
                    }
                    break;// break outer switch
                }
                case 1: {// register stage
                    switch (subStage){
                        case 1:{ //get
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt1_1, "g12"}; // g for get
                            subStage = 2;
                            break;
                        }
                        case 2:{// add Account
                            boolean succeed = be.addAccount(this.msgFromClient[0], utils.generateRandomID(18),
                                "12345678900", this.msgFromClient[1]);// clientMsg0: username; clientMsg1: pw
                            if (succeed) {
                                msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt1_2, "n"};
                                stage = 0;
                                subStage = 0;
                            } else {
                                msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), String.format(pmpt1_3, this.msgFromClient[0]), "n"};
                                stage = 1;
                                subStage = 1;
                            }
                            break;
                        }
                        default:{
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), subStageNoMatch, "e"}; // e for error
                            break;
                        }
                    }
                    break;
                }
                case 2: {// login stage
                    switch (subStage) {
                        case 1: {
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt2_1,
                                "g12"}; // g for get
                            subStage = 2;
                            break;
                        }
                        case 2:{// check Account
                            boolean succeed = be.verifyPw(this.msgFromClient[0], this.msgFromClient[1]); // clientMsg0: username; clientMsg1: pw
                            if (succeed) {
                                msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt2_2_1, "login"};// login successful
                                stage = 3;
                                subStage = 1;
                            } else {
                                msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), getPmpt2_2_2, "n"}; // login not successful
                                stage = 1;
                                subStage = 1;
                            }
                            break;
                        }
                        case 3:{// get
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt2_3, "g2"}; // g for get
                            subStage = 4;
                            break;
                        }
                        case 4:{ //clientMsg[0]: action
                            String action = this.msgFromClient[0];
                            if (action.equals("back")) {
                                msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "", "n"};
                                stage = 0;
                                subStage = 0;
                            } else if (action.equals("try-again")) {
                                msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), pmpt2_4, "n"};
                                subStage = 3;
                            } else { // Do nothing
                            }
                            break;
                        }
                    }
                    break;
                }

                case 3: {
                    switch (subStage){
                        case 1:{
                            stage = 0;
                            subStage = 0;
                            msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), "We are here!!!", "n"};
                            break;
                        }
                    }
                    break;
                }
//                case 3: {
//                    String menu =
//                        "  1: 'show-my-block-list'\n" +
//                            "  2: 'show-my-shield-list'\n" +
//                            "  3: 'change-phone-pw'\n" +
//                            "  4: 'show-post'\n" +
//                            "  5: 'show-my-post'\n" +
//                            "  6: 'show-post-detail'\n" +
//                            "  7: 'show-my-reply'\n" +
//                            "  8: 'search-post-by-multi-parameter'\n" +
//                            "  9: 'show-replied-post'\n" +
//                            " 10: 'show-my-follow-list'\n" +
//                            " 11: 'show-my-like-list'\n" +
//                            " 12: 'show-my-favor-list'\n" +
//                            " 13: 'show-my-share-list'\n" +
//                            " 14: 'show-hotList'\n" +
//                            " 15: 'block'\n" +
//                            " 16: 'shield'\n" +
//                            " 17: 'unblock'\n" +
//                            " 18: 'unshield'\n" +
//                            " 19: 'follow'\n" +
//                            " 20: 'unfollow'\n" +
//                            " 21: 'post'\n" +
//                            " 22: 'logoff'\n" +
//                            " 23: 'show-my-phone'";
//
//                    String[] lines = menu.split("\n");
//                    int maxLength = 0;
//                    for (String line : lines) {
//                        maxLength = Math.max(maxLength, line.length());
//                    }
//
//                    String horizontalLine = "*" + "*".repeat(maxLength * 3 + 4) + "*";
//                    System.out.println(horizontalLine);
//                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Welcome to the playground!") + "   *");
//                    System.out.println(
//                        "* " + String.format("%-" + maxLength * 3 + "s", "What do you want to do?")
//                            + "   *");
//                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
//                    System.out.println(horizontalLine);
//                    System.out.println();
//
//                    int lineLength = maxLength * 3 + 4;
//                    int centerOffset = (lineLength - 10) / 2; // 计算居中偏移量
//
//                    String Header =
//                        "*" + "*".repeat(centerOffset - 1) + " Playground " + "*".repeat(
//                            centerOffset) + "*";
//
//                    System.out.println(Header);
//                    int count = 0;
//                    for (String line : lines) {
//                        if (count % 3 == 0) {
//                            System.out.print("* ");
//                        }
//                        System.out.print(String.format("%-" + maxLength + "s", line) + " ");
//                        count++;
//                        if (count % 3 == 0) {
//                            System.out.println("*");
//                        }
//                    }
//                    if (count % 3 != 0) {
//                        System.out.println(" ".repeat((3 - (count % 3)) * (maxLength + 1)) + "*");
//                    }
//                    System.out.println(horizontalLine);
//
//                    String action = utils.getWord(scanner);
//
//                    switch (action) {
//                        case "show-my-phone", "23": {
//                            System.out.printf(
//                                "Your phone number is: %s. Note that the default phone number is 12345678900. You can change it with change-phone-pw.\n",
//                                be.showMyPhone(usr.getName()).get(0));
//                            break;
//                        }
//                        case "change-phone-pw", "3": {
//                            System.out.println(
//                                "Please input the phone number and the password you want to change, separated by a space, end with new line.");
//                            String phone = scanner.next();
//                            String pw = utils.getWord(scanner);
//                            boolean succeed = be.changeAccountDetail(usr.getName(), phone, pw);
//                            if (succeed) {
//                                System.out.println(
//                                    "Your phone number and password have been successfully set up!!");
//                            } else {
//                                System.out.println(
//                                    "Sorry, there is some problem with the backend.");
//                            }
//                            break;
//                        }
//                        case "unblock", "17": {
//                            System.out.println("Please input the account id you want to unblock:");
//                            String name = utils.getWord(scanner);
//                            be.unBlockAccount(name, usr.getName());
//                            System.out.println("You unblock a person, thank you!");
//                            break;
//                        }
//                        case "unshield", "18": {
//                            System.out.println("Please input the account id you want to unshield:");
//                            String name = utils.getWord(scanner);
//                            be.unShieldAccount(name, usr.getName());
//                            System.out.println("You unshield a person, thank you!");
//                            break;
//                        }
//                        case "block", "15": {
//                            System.out.println("Please input the account id you want to block:");
//                            String name = utils.getWord(scanner);
//                            be.blockAccount(name, usr.getName());
//                            System.out.println("You block a person, thank you!");
//                            break;
//                        }
//                        case "shield", "16": {
//                            System.out.println("Please input the account id you want to shield:");
//                            String name = utils.getWord(scanner);
//                            be.shieldAccount(name, usr.getName());
//                            System.out.println("You shield a person, thank you!");
//                            break;
//                        }
//                        case "logoff", "22": {
//                            System.out.println("Bye from playground!");
//                            stage = 0;
//                            break;
//                        }
//                        case "show-post", "4":// TODO: show only the first n posts
//                        {
//                            boolean[] print = {false, true, false, true, true, true};
//                            utils.printArray2(be.checkPosts(be.getAllPostIds(), usr.getName()),
//                                "post", print, postColumns);
//                            System.out.println("You can see the post detail with postID");
//                            break;
//                        }
//                        case "show-my-post", "5": {
//                            ArrayList<ArrayList<String>> myPosts = be.checkPosts(
//                                be.showMyPostIDs(usr.getName()), usr.getName());
////                            System.out.println("content" + myPosts);
//                            if (myPosts.get(0).size() == 0) {
//                                System.out.println(
//                                    "Oops! You have not posted anything yet. Let start posting!");
//                            } else {
//                                boolean[] print = {false, true, false, true, true, true};
//                                utils.printArray2(myPosts, "My post", print, postColumns);
//                                System.out.println("You can see the post detail with postID");
//                            }
//                            break;
//                        }
//                        case "show-post-detail", "6": {
//                            stage = 4;
//                            System.out.println(
//                                "Please input the post ID you want to take a closer look.");
//                            ArrayList<Integer> postIDs = new ArrayList<>(); // TODO: simplify
//                            String[] input = utils.getWord(scanner).split(" ");
//                            for (String id : input) {
//                                postIDs.add(Integer.parseInt(id));
//                            }
//                            ArrayList<ArrayList<String>> postInfo = be.checkPosts(postIDs,
//                                usr.getName());
//                            if (postInfo.get(1).size() == 0) {
//                                System.out.println("Opps , it seems you can't see this post");
//                                stage = 3;
//                            } else
//                                post = new Post(postIDs.get(0), postInfo.get(1).get(0),
//                                    postInfo.get(2).get(0), postInfo.get(3).get(0),
//                                    postInfo.get(4).get(0), postInfo.get(5).get(0));
//                            break;
//                        }
//                        case "search-post-by-multi-parameter", "8": {
//                            System.out.println(
//                                "Please enter the keywords (enter an empty line if you don't want it)");
//                            String keywords = scanner.nextLine();
//                            System.out.println(
//                                "Please enter the start and end time (enter an empty line if you don't want it)");
//                            String startTime = scanner.nextLine();
//                            String endTime = scanner.nextLine();
//                            System.out.println(
//                                "Please enter the city (enter an empty line if you don't want it)");
//                            String city = scanner.nextLine();
//                            System.out.println(
//                                "Please enter the author's name (enter an empty line if you don't want it)");
//                            String authorName = scanner.nextLine();
//                            ArrayList<ArrayList<String>> postInfo = be.checkPostsMultiParameter(
//                                keywords, startTime, endTime, city, authorName);
//                            if (postInfo.get(0).size() != 0) {
//                                boolean[] print = {false, true, false, true, true, true};
//                                utils.printArray2(postInfo, "selected post", print, postColumns);
//                            } else
//                                System.out.println("There is no such post");
//                            break;
//                        }
//                        case "show-my-reply", "7": {
//                            ArrayList<ArrayList<String>> myReplies = be.checkReplies(
//                                be.showMyReplyIDs(usr.getName()), usr.getName());
//                            if (myReplies.get(0).size() == 0) {
//                                System.out.println(
//                                    "Oops! You have not replied to any post yet. Let start replying!");
//                            } else {// TODO: test!!
//                                boolean[] print = {true, false, true, true, true, true};
//                                utils.printArray2(myReplies, "My reply", print, replyColumns);
//                                System.out.println("You can see the reply detail with replyID");
//                            }
//                            break;
//                        }
//                        case "show-replied-post", "9": {
//                            ArrayList<ArrayList<String>> repliedPost = be.checkPosts(
//                                be.showMyRepliedPostIDs(usr.getName()), usr.getName());
////                            System.out.println("content" + myPosts);
//                            if (repliedPost.get(0).size() == 0) {
//                                System.out.println(
//                                    "Oops! You have not replied to anything yet. Let's start replying!");
//                            } else {
//                                boolean[] print = {false, true, false, true, true, true};
//                                utils.printArray2(repliedPost, "You have replied to these posts",
//                                    print, postColumns);
//                                System.out.println("You can see the post detail with postID");
//                            }
//                            break;
//                        }
//                        case "show-my-follow-list", "10": {
//                            ArrayList<String> followList = be.showFollowNameList(usr.getName());
//                            if (followList.size() == 0) {
//                                System.out.println(
//                                    "You have not followed anyone. Start following!");
//                            } else {
//                                utils.printArray(be.showFollowNameList(usr.getName()),
//                                    "My follow list");
//                            }
//                            break;
//                        }
//                        case "show-my-block-list", "1": {
//                            boolean[] print = {true};
//                            ArrayList<String> followList = be.getBlockedAccount(usr.getName());
//                            if (followList.size() == 0) {
//                                System.out.println("You have not block anyone. Start blocking!");
//                            } else {
//                                utils.printArray(be.getBlockedAccount(usr.getName()),
//                                    "My block list");
//                            }
//                            break;
//                        }
//                        case "show-my-shield-list", "2": {
//                            boolean[] print = {true};
//                            ArrayList<String> followList = be.getShieldAccount(usr.getName());
//                            if (followList.size() == 0) {
//                                System.out.println("You have not shield anyone. Start shielding!");
//                            } else {
//                                utils.printArray(be.getShieldAccount(usr.getName()),
//                                    "My shield list");
//                            }
//                            break;
//                        }
//                        case "show-my-like-list", "11": {
//                            boolean[] print = {true, true, false, true, true, true};
//                            ArrayList<ArrayList<String>> temp = be.checkPosts(
//                                be.showLFSIDList(usr.getName(), "liked"), usr.getName());
//                            if (temp.get(0).size() == 0) {
//                                System.out.println("You haven't like any post yet");
//                                break;
//                            }
//                            utils.printArray2(temp, "Like list", print, postColumns);
//                            System.out.println(
//                                "You can take a deeper look with 'show-post-detail'");
//                            break;
//                        }
//                        case "show-my-favor-list", "12": {
//                            boolean[] print = {true, true, false, true, true, true};
//                            ArrayList<ArrayList<String>> temp = be.checkPosts(
//                                be.showLFSIDList(usr.getName(), "favored"), usr.getName());
//                            if (temp.get(0).size() == 0) {
//                                System.out.println("You haven't favored any post yet");
//                                break;
//                            }
//                            utils.printArray2(temp, "Favor list", print, postColumns);
//                            System.out.println(
//                                "You can take a deeper look with 'show-post-detail'");
//                            break;
//                        }
//                        case "show-my-share-list", "13": {
//                            boolean[] print = {true, true, false, true, true, true};
//                            ArrayList<ArrayList<String>> temp = be.checkPosts(
//                                be.showLFSIDList(usr.getName(), "shared"), usr.getName());
//                            if (temp.get(0).size() == 0) {
//                                System.out.println("You haven't shared any post yet");
//                                break;
//                            }
//                            utils.printArray2(temp, "Share list", print, postColumns);
//                            System.out.println(
//                                "You can take a deeper look with 'show-post-detail'");
//                            break;
//                        }
//                        case "show-hotList", "14": {
//                            boolean[] print = {true, true, false, true, true, true};
//                            utils.printArray2(be.checkPosts(be.showHotlist(), usr.getName()),
//                                "Share list", print, postColumns);
//                            System.out.println(
//                                "You can take a deeper look with 'show-post-detail'");
//                            break;
//                        }
//                        case "follow", "19": {
//                            System.out.println("Please input the account name you want to follow:");
//                            String name = utils.getWord(scanner);
//                            if (be.ifFollowed(usr.getName(), name)) {
//                                System.out.println(
//                                    "You have followed this minion. Why you like him/her/it so much wuuuu.");
//                            } else {
//                                be.followAccount(usr.getName(), name);
//                                System.out.println("You follow a new person, thank you!");
//                            }
//                            break;
//                        }
//                        case "unfollow", "20": {
//                            System.out.println(
//                                "Please input the account name you want to unfollow:");
//                            String name = utils.getWord(scanner);
//                            if (!be.ifFollowed(usr.getName(), name)) {
//                                System.out.println(
//                                    "You have not followed this minion. Why you hate him/her/it so much wuuuu.");
//                            } else {
//                                be.unfollowAccount(usr.getName(), name);
//                                System.out.println("You unfollow an old friend. What's wrong!");
//                            }
//                            break;
//                        }
//                        case "post", "21": {
//                            System.out.println("Please enter the title below:");
//                            String title = utils.getLine(scanner);
//                            System.out.println(
//                                "Please enter the content below. End with an empty line.");
//                            String contentBuffer = "";
//                            read:
//                            while (scanner.hasNextLine()) {
//                                String contentLine = scanner.nextLine();
//                                if (contentLine == "" && contentBuffer != "")
//                                    break read;
//                                else
//                                    contentBuffer += (contentLine + "\n");
//                            }
//                            contentBuffer.trim();
//                            System.out.println("Please enter the city you are in honestly.");
//                            String city = utils.getLine(scanner);
//                            System.out.println(
//                                "Please select one or more categories from the list: below");
//                            ArrayList<String> catList = be.getAllCategories();
//                            for (String catName : catList) {
//                                System.out.printf("%s ", catName);
//                            }
//                            System.out.println();
//                            boolean catNotSafe = true;
//                            ArrayList<String> input = new ArrayList<>();
//                            while (catNotSafe) {
//                                System.out.println(
//                                    "Please enter the categories you think your post belongs to, separate them with space end with new line.");
//                                input = new ArrayList<>(
//                                    Arrays.asList(utils.getLine(scanner).split(" ")));
//                                catNotSafe = false;
//                                for (String cat : input) {
//                                    if (!catList.contains(cat)) {
//                                        System.out.printf(
//                                            "You enter a category that is not in the category list!! %s\n",
//                                            cat);
//                                        catNotSafe = true;
//                                        break;
//                                    }
//                                }
//                            }
//
//                            //是否匿名的检测
//                            System.out.println(
//                                "Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
//                            String Ano = utils.getWord(scanner);
//                            boolean anonymous = Ano.equals("y") ? true : false;
//
//                            boolean postSafe;
//                            postSafe = be.post(title, contentBuffer, city, usr.getName(), input,
//                                anonymous);
//                            System.out.println("Hurry!!!!! You just post a post.");
//
//                            if (!postSafe) {
//                                System.out.println(
//                                    "Something is wrong in our backend. Our sincere apology!");
//                            }
//                        }
//                    }
//                    break;
//                }
//                case 4: // post
//                {
//                    String menu =
//                        "  1: 'like'\n" +
//                            "  2: 'favor'\n" +
//                            "  3: 'share'\n" +
//                            "  4: 'follow'\n" +
//                            "  5: 'show-reply'\n" +
//                            "  6: 'see-reply-detail'\n" +
//                            "  7: 'reply'\n" +
//                            "  8: 'back'\n";
//
//                    String[] lines = menu.split("\n");
//                    int maxLength = 0;
//                    for (String line : lines) {
//                        maxLength = Math.max(maxLength, line.length());
//                    }
//
//                    String horizontalLine = "*" + "*".repeat(maxLength * 3 + 4) + "*";
//                    int lineLength = maxLength * 3 + 4;
//                    String heading = "PostId : " + String.valueOf(post.getPostID());
//                    int centerOffset = (lineLength - heading.length()) / 2;
//
//                    String Header =
//                        "*" + "*".repeat(centerOffset - 1) + " " + heading + " " + "*".repeat(
//                            centerOffset - 1) + "*";
//
//                    System.out.println(Header);
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Title: " + post.getTitle()) + "   *");
//                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
//                    System.out.println(
//                        "* " + String.format("%-" + maxLength * 3 + "s", "Content:") + "   *");
//                    printWrappedContent(post.getContent(), maxLength * 3);
//                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Author: " + post.getPost_account_name()) + "   *");
//                    System.out.println(
//                        "* " + String.format("%-" + maxLength * 3 + "s", "City: " + post.getCity())
//                            + "   *");
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Time: " + post.getDatetime()) + "   *");
//                    System.out.println(horizontalLine);
//                    System.out.println();
//
//                    System.out.println(horizontalLine);
//                    int count = 0;
//                    for (String line : lines) {
//                        if (count % 3 == 0) {
//                            System.out.print("* ");
//                        }
//                        System.out.print(String.format("%-" + maxLength + "s", line) + " ");
//                        count++;
//                        if (count % 3 == 0) {
//                            System.out.println("*");
//                        }
//                    }
//                    if (count % 3 != 0) {
//                        System.out.println(" ".repeat((3 - (count % 3)) * (maxLength + 1)) + "*");
//                    }
//                    System.out.println(horizontalLine);
//
//                    String action = utils.getWord(scanner);
//
//                    switch (action) {
//                        case "like", "1": {
//                            be.lfsPost(usr.getName(), post.getPostID(), "liked");
//                            System.out.println("You liked it, thank you!");
//                            break;
//                        }
//                        case "favor", "2": {
//                            be.lfsPost(usr.getName(), post.getPostID(), "favored");
//                            System.out.println("You favored it, thank you!");
//                            break;
//                        }
//                        case "share", "3": {
//                            be.lfsPost(usr.getName(), post.getPostID(), "shared");
//                            System.out.println("You shared it, thank you!");
//                            break;
//                        }
//                        case "follow", "4": {
//                            if (be.ifFollowed(usr.getName(), post.getPost_account_name())) {
//                                System.out.println(
//                                    "You already followed this minion. Don't love him/her/it too much.");
//                            } else {
//                                be.followAccount(usr.getName(), post.getPost_account_name());
//                                System.out.println("You follow the post author, thank you!");
//                            }
//                            break;
//                        }
//                        case "show-reply", "5": {
//                            boolean[] print = {true, false, true, true, true, true};
//                            ArrayList<Integer> firstLevelReplyList = be.showReply(post.getPostID(),
//                                true);
//                            if (firstLevelReplyList.size() == 0) {
//                                System.out.println("There are no replies attached to this post.");
//                            } else {
//                                utils.printArray2(
//                                    be.checkReplies(firstLevelReplyList, usr.getName()),
//                                    "Reply list", print, replyColumns);
//                                System.out.printf("There are %d first-level reply to this post\n",
//                                    firstLevelReplyList.size());
//                                System.out.println(
//                                    "Your can see reply detail by 'see-reply-detail'.");
//                            }
//                            break;
//                        }
//                        case "see-reply-detail", "6": {
//                            System.out.println("Please input the id of the reply you want to see");
//                            int id = Integer.parseInt(utils.getWord(scanner));
//                            ArrayList<Integer> replyIDList = new ArrayList<>();
//                            replyIDList.add(id);
//                            ArrayList<ArrayList<String>> replyInfo = be.checkReplies(replyIDList,
//                                usr.getName());
//                            if (replyInfo.get(1).size() == 0) {
//                                System.out.println("Opps , it seems you can't see this reply");
//                                stage = 3;
//                            } else
//                                reply = new Reply(id, Integer.parseInt(replyInfo.get(1).get(0)),
//                                    replyInfo.get(2).get(0),
//                                    Integer.parseInt(replyInfo.get(3).get(0)),
//                                    Integer.parseInt(replyInfo.get(4).get(0)),
//                                    replyInfo.get(5).get(0));
//                            stage = 5;
//                            break;
//                        }
//                        case "reply", "7": {
//                            System.out.println(
//                                "Please input the content of your reply. End with a new line.");
//                            String content = utils.getLine(scanner);
//
//                            //是否匿名的检测
//                            System.out.println(
//                                "Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
//                            String Ano = utils.getWord(scanner);
//                            boolean anonymous = Ano.equals("y") ? true : false;
//
//                            be.replyPost(-1, content, post.getPostID(), usr.getName(), anonymous);
//                            System.out.println("You just replied to a post. Thank you!");
//                            break;
//                        }
//                        case "back", "8": {
//                            stage--;
//                            break;
//                        }
//                    }
//                    break;
//                }
//                case 5: // reply TODO:
//                {
//                    String menu =
//                        "  1: 'follow'\n" +
//                            "  2: 'show-secondary-reply'\n" +
//                            "  3: 'see-secondary-reply-detail'\n" +
//                            "  4: 'reply-to-reply'\n" +
//                            "  5: 'back'\n" +
//                            "  6: 'star-the-current-reply'\n";
//
//                    String[] lines = menu.split("\n");
//                    int maxLength = 0;
//                    for (String line : lines) {
//                        maxLength = Math.max(maxLength, line.length());
//                    }
//                    boolean secondary = (reply.getReplyID() <= 0) ? false : true;
//                    String level = secondary ? "Secondary " : "Primary ";
//
//                    String horizontalLine = "*" + "*".repeat(maxLength * 3 + 4) + "*";
//                    int lineLength = maxLength * 3 + 4;
//                    String heading = level + "Reply Id : " + String.valueOf(reply.getId());
//                    int centerOffset = (lineLength - heading.length()) / 2;
//
//                    String Header =
//                        "*" + "*".repeat(centerOffset - 1) + " " + heading + " " + "*".repeat(
//                            centerOffset - 1) + "*";
//
//                    System.out.println(Header);
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Author: " + reply.getAuthor_account_name()) + "   *");
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Stars: " + reply.getStars()) + "   *");
//                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
//                    System.out.println(
//                        "* " + String.format("%-" + maxLength * 3 + "s", "Content: ") + "   *");
//                    printWrappedContent(reply.getContent(), maxLength * 3);
//                    System.out.println("* " + " ".repeat(maxLength * 3 + 2) + " *");
//                    System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                        "Post Id: " + reply.getPostID()) + "   *");
//                    if (secondary) {
//                        System.out.println("* " + String.format("%-" + maxLength * 3 + "s",
//                            "Parent Reply Id: " + reply.getReplyID()) + "   *");
//                    }
//                    System.out.println(horizontalLine);
//                    System.out.println();
//
//                    System.out.println(horizontalLine);
//                    int count = 0;
//                    for (String line : lines) {
//                        if (count % 3 == 0) {
//                            System.out.print("* ");
//                        }
//                        System.out.print(String.format("%-" + maxLength + "s", line) + " ");
//                        count++;
//                        if (count % 3 == 0) {
//                            System.out.println("*");
//                        }
//                    }
//                    if (count % 3 != 0) {
//                        System.out.println(" ".repeat((3 - (count % 3)) * (maxLength + 1)) + "*");
//                    }
//                    System.out.println(horizontalLine);
//
//                    String action = utils.getWord(scanner);
//                    switch (action) {
//                        case "back", "5": {
//                            stage--;
//                            break;
//                        }
//                        case "follow", "1": {
//                            if (be.ifFollowed(usr.getName(), reply.getAuthor_account_name())) {
//                                System.out.println(
//                                    "You already followed this minion. Don't love him/her/it too much.");
//                            } else {
//                                be.followAccount(usr.getName(), reply.getAuthor_account_name());
//                                System.out.println("You follow a reply author.");
//                            }
//                            break;
//                        }
//                        case "show-secondary-reply", "2": {
//                            ArrayList<Integer> secondaryReplyList = be.showReply(reply.getId(),
//                                false);
//                            if (secondaryReplyList.size() != 0) {
//                                boolean[] print = {true, false, true, true, true, true};
//                                utils.printArray2(
//                                    be.checkReplies(secondaryReplyList, usr.getName()),
//                                    "Secondary reply list", print, replyColumns);
//                                System.out.printf("There are %d secondary replies.\n",
//                                    secondaryReplyList.size());
//                                System.out.println(
//                                    "Your can see reply detail by 'see-secondary-reply-detail'.");
//                                break;
//                            } else {
//                                System.out.println("There are no secondary replies to this reply.");
//                            }
//                            break;
//                        }
//                        case "see-secondary-reply-detail", "3": {
//                            System.out.println("Please input the id of the secondary reply.");
//                            int id = Integer.parseInt(utils.getWord(scanner));
//                            ArrayList<Integer> replyIDList = new ArrayList<>();
//                            replyIDList.add(id);
//                            ArrayList<ArrayList<String>> replyInfo = be.checkReplies(replyIDList,
//                                usr.getName());
//                            if (replyInfo.get(1).size() == 0) {
//                                System.out.println("Opps , it seems you can't see this reply");
//                                stage = 3;
//                            } else
//                                reply = new Reply(id, Integer.parseInt(replyInfo.get(1).get(0)),
//                                    replyInfo.get(2).get(0),
//                                    Integer.parseInt(replyInfo.get(3).get(0)),
//                                    Integer.parseInt(replyInfo.get(4).get(0)),
//                                    replyInfo.get(5).get(0));
//                            break;
//                        }
//                        case "reply-to-reply", "4": // TODO: check for case break
//                        {
//                            System.out.println(
//                                "Please input the content of your reply. End with a new line.");
//                            String content = utils.getLine(scanner);
//
//                            //是否匿名的检测
//                            System.out.println(
//                                "Do you want to post this post anonymously? (Enter 'y' to post anonymously, otherwise enter 'n' )");
//                            String Ano = utils.getWord(scanner);
//                            boolean anonymous = Ano.equals("y") ? true : false;
//
//                            be.replyPost(reply.getId(), content, post.getPostID(), usr.getName(),
//                                anonymous);
//                            System.out.println("You just replied to a reply. Thank you!");
//                            break;
//                        }
//                        case "star-the-current-reply", "6": {
//                            be.starReply(reply.getId());
//                            System.out.println("You just stared a reply. Thank you!");
//                        }
//
//                    }
//                    break;
//                }
                default:{
                    msg4Client = new String[]{String.valueOf(stage), String.valueOf(subStage), stageNoMatch, "e"}; // e for error
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            clear();
        }
        return msg4Client;
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
