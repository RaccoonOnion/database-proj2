public class Follows {
    private String FollowedName;
    private String FollowerName;

    public Follows(String followedName, String followerName) {
        FollowedName = followedName;
        FollowerName = followerName;
    }

    public String getFollowedName() {
        return FollowedName;
    }

    public void setFollowedName(String followedName) {
        FollowedName = followedName;
    }

    public String getFollowerName() {
        return FollowerName;
    }

    public void setFollowerName(String followerName) {
        FollowerName = followerName;
    }

    @Override
    public String toString() {
        return "follewingRelationship{" +
                "FollowedName='" + FollowedName + '\'' +
                ", FollowerName='" + FollowerName + '\'' +
                '}';
    }
}
