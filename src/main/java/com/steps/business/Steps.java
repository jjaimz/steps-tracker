package com.steps.business;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Steps {
    @JsonProperty("id")
    private int id;
    @JsonProperty("users_id")
    private int users_id;
    @JsonProperty("date")
    private String date;
    @JsonProperty("steps")
    private int steps;
    @JsonProperty("image")
    private String image;

    @Override
    public String toString() {
        return "Steps{" +
                "id=" + id +
                ", users_id=" + users_id +
                ", date='" + date + '\'' +
                ", steps=" + steps +
                ", image='" + image + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
