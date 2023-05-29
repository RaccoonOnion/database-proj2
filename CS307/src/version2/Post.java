package version2;

public class Post {
    private int postID;
    private String title;
    private String content;
    private String datetime;
    private String city;
    private String post_account_name;

    public Post(int postID, String title, String content, String datetime, String city, String post_account_name) {
        this.postID = postID;
        this.title = title;
        this.content = content;
        this.datetime = datetime;
        this.city = city;
        this.post_account_name = post_account_name;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPost_account_name() {
        return post_account_name;
    }

    public void setPost_account_name(String post_account_name) {
        this.post_account_name = post_account_name;
    }
}

