public class Favorites {
    private int postID;
    private String authorName;

    public Favorites(int postID, String authorName) {
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
        return "Favorites{" +
                "postID=" + postID +
                ", authorName='" + authorName + '\'' +
                '}';
    }
}
