import java.sql.Timestamp;

public class Posts {
    private int postID;
    private String title;
    private String content;
    private Timestamp posting_time;
    private String posting_city;
    private String AuthorName;

    public Posts(int postID, String title, String content, Timestamp posting_time, String posting_city, String authorName) {
        this.postID = postID;
        this.title = title;
        this.content = content;
        this.posting_time = posting_time;
        this.posting_city = posting_city;
        AuthorName = authorName;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getPosting_time() {
        return posting_time;
    }

    public void setPosting_time(Timestamp posting_time) {
        this.posting_time = posting_time;
    }

    public String getPosting_city() {
        return posting_city;
    }

    public void setPosting_city(String posting_city) {
        this.posting_city = posting_city;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "postID=" + postID +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", posting_time='" + posting_time + '\'' +
                ", posting_city='" + posting_city + '\'' +
                ", AuthorName='" + AuthorName + '\'' +
                '}';
    }
}
