package com.steps.business;

public class Steps {
    private int id;
    private int users_id;
    private String date;
    private int steps;
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
