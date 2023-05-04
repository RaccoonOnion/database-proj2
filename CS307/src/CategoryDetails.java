public class CategoryDetails {
    private int postID;
    private int categoryID;

    public CategoryDetails(int postID, int categoryID) {
        this.postID = postID;
        this.categoryID = categoryID;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return "CategoryDetails{" +
                "postID=" + postID +
                ", categoryID=" + categoryID +
                '}';
    }
}
