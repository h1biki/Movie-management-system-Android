package com.example.mymoviememoir;


public class Person {
    private String userid;
    private String firstname;
    private String surname;
    private String dob;
    private String gender;
    private String address;
    private String postcode;
    private String state;

    public Person() {
    }

    public Person(String userid, String firstname, String surname, String dob, String gender, String address, String postcode, String state) {
        this.userid = userid;
        this.firstname = firstname;
        this.surname = surname;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.state = state;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
