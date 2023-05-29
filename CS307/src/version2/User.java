package version2;

public class User {
    // User information variables
    private String name;
    private String account_id;
    private String regTime;
    private String phone;
    private String pw;
    // User state variables
    private boolean login = false;

    public User(String name, String account_id, String regTime, String phone, String pw, boolean login) {
        this.name = name;
        this.account_id = account_id;
        this.regTime = regTime;
        this.phone = phone;
        this.pw = pw;
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
