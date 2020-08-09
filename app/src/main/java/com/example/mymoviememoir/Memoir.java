package com.example.mymoviememoir;

public class Memoir {
    private String watchtime;

    private String moviename;

    private String releasedate;

    private String comment;

    private String rating;

    private Cinema cinemaid;

    private Person userid;

    public Memoir(Cinema cinemaid, String comment, String moviename, String rating, String releasedate, Person userid, String watchtime) {

        this.watchtime = watchtime;
        this.moviename = moviename;
        this.releasedate = releasedate;
        this.comment = comment;
        this.rating = rating;
        this.cinemaid = cinemaid;
        this.userid = userid;
    }



    public String getWatchtime() {
        return watchtime;
    }

    public void setWatchtime(String watchtime) {
        this.watchtime = watchtime;
    }

    public String getMoviename() {
        return moviename;
    }

    public void setMoviename(String moviename) {
        this.moviename = moviename;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Cinema getCinemaid() {
        return cinemaid;
    }

    public void setCinemaid(Cinema cinemaid) {
        this.cinemaid = cinemaid;
    }

    public Person getUserid() {
        return userid;
    }

    public void setUserid(Person userid) {
        this.userid = userid;
    }
}
