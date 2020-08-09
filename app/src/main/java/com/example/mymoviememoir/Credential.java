package com.example.mymoviememoir;

public class Credential {
    private String username;
    private String password;
    private String signupdate;
    private Person userid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordhash() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignupdate() {
        return signupdate;
    }

    public void setSignupdate(String signupdate) {
        this.signupdate = signupdate;
    }

    public Person getUserid() {
        return userid;
    }

    public void setUserid(Person userid) {
        this.userid = userid;
    }
}
