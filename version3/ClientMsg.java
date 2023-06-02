package version2;

import java.io.Serializable;

public class ClientMsg implements Serializable {

    private int stage = 0;
    private int subStage = 0;
    private boolean login = false;
    private User usr = null;
    private Post post = null;
    private Reply reply = null;

    private String[] clientInput = null;

    public ClientMsg(int stage, int subStage, boolean login, User usr, Post post, Reply reply,
        String[] clientInput) {
        this.stage = stage;
        this.subStage = subStage;
        this.login = login;
        this.usr = usr;
        this.post = post;
        this.reply = reply;
        this.clientInput = clientInput;
    }


    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getSubStage() {
        return subStage;
    }

    public void setSubStage(int subStage) {
        this.subStage = subStage;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public User getUsr() {
        return usr;
    }

    public void setUsr(User usr) {
        this.usr = usr;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public String[] getClientInput() {
        return clientInput;
    }

    public void setClientInput(String[] clientInput) {
        this.clientInput = clientInput;
    }


}
