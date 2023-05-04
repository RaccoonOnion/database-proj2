import java.sql.Timestamp;

public class Author {
    private String authorID;
    private String name;
    private Timestamp registration_time;
    private String phone;

    public Author(String authorID, String name, Timestamp registration_time, String phone) {
        this.authorID = authorID;
        this.name = name;
        this.registration_time = registration_time;
        this.phone = phone;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getName() {
        return name;
    }

    public Timestamp getRegistration_time() {
        return registration_time;
    }

    public String getPhone() {
        return phone;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistration_time(Timestamp registration_time) {
        this.registration_time = registration_time;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorID=" + authorID +
                ", name='" + name + '\'' +
                ", registration_time='" + registration_time + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
