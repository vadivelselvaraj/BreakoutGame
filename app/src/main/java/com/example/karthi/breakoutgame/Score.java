package com.example.karthi.breakoutgame;

/*
* Author : Aishwarya & Vel
* Date : 24/Nov/2015
* Purpose : Entering and Updating the Score
*/
public class Score {


    private int rank;
    private String userName;
    private int score;
    private int time; // in seconds


    Score(int rank, String userName, int score, int time) {
        this.rank = rank;
        this.userName = userName;
        this.score = score;
        this.time = time;
    }
    /*
    * Author : Aishwarya & Vel
    * Date : 24/Nov/2015
    * Purpose : Getters and Setters of Score
    */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
