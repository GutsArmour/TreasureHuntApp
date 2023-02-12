package com.example.treasurehunt;

public class UserScores {

    public UserScores() {
    }

    public Long getPoints() {
        return points;
    }
    public void setPoints(Long points) {
        this.points = points;
    }
    private Long points;

    public String getPfp() {return  pfp; }
    public void setPfp(String pfp) {
        this.pfp = pfp;
    }
    private String pfp;
}
