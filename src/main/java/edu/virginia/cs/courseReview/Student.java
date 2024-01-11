package edu.virginia.cs.courseReview;
public class Student {
    private String login;
    private String password;

    public Student()
    {
        login = "";
        password = "";
    }

    public Student(String login, String password){
        this.login= login;
        this.password=password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
