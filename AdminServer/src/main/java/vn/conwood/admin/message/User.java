package vn.conwood.admin.message;

public class User {
    private int uid;
    private String followerId;
    private String name;


    public User() {
    }

    public User(int uid, String followerId, String name) {
        this.uid = uid;
        this.followerId = followerId;
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
