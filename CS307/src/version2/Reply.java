package version2;

public class Reply {
    private int id;
    private int replyID;
    private String content;
    private int stars;
    private int postID;
    private String author_account_name;

    public Reply(int id, int replyID, String content, int stars, int postID, String author_account_name) {
        this.id = id;
        this.replyID = replyID;
        this.content = content;
        this.stars = stars;
        this.postID = postID;
        this.author_account_name = author_account_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReplyID() {
        return replyID;
    }

    public void setReplyID(int replyID) {
        this.replyID = replyID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getAuthor_account_name() {
        return author_account_name;
    }

    public void setAuthor_account_name(String author_account_name) {
        this.author_account_name = author_account_name;
    }
}
