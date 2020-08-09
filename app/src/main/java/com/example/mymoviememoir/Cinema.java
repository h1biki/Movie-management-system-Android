package com.example.mymoviememoir;

public class Cinema {
    private String cinemaid;

    private String cinemaname;

    private String surburb;


    public Cinema(String cinemaid, String cinemaname, String surburb) {
        this.cinemaid = cinemaid;
        this.cinemaname = cinemaname;
        this.surburb = surburb;
    }

    public String getCinemaid() {
        return cinemaid;
    }

    public void setCinemaid(String cinemaid) {
        this.cinemaid = cinemaid;
    }

    public String getCinemaname() {
        return cinemaname;
    }

    public void setCinemaname(String cinemaname) {
        this.cinemaname = cinemaname;
    }

    public String getSurburb() {
        return surburb;
    }

    public void setSurburb(String surburb) {
        this.surburb = surburb;
    }
}
