public class SecondaryReply {
    private int replyID;
    private String content;
    private int stars;
    private String authorName;

    public SecondaryReply(int replyID, String content, int stars, String authorName) {
        this.replyID = replyID;
        this.content = content;
        this.stars = stars;
        this.authorName = authorName;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "SecondaryReply{" +
                "replyID=" + replyID +
                ", content='" + content + '\'' +
                ", stars=" + stars +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}
