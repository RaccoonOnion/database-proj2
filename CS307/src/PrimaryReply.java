public class PrimaryReply {
    private int postID;
    private String content;
    private int stars;
    private String authorName;

    public PrimaryReply(int postID, String content, int stars, String authorName) {
        this.postID = postID;
        this.content = content;
        this.stars = stars;
        this.authorName = authorName;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
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
        return "primaryReply{" +
                "postID=" + postID +
                ", content='" + content + '\'' +
                ", stars=" + stars +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}
