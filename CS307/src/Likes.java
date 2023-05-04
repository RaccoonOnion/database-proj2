public class Likes {
    private int postID;
    private String authorName;

    public Likes(int postID, String authorName) {
        this.postID = postID;
        this.authorName = authorName;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "Likes{" +
                "postID=" + postID +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}
