package com.example.pockettrip;

public class UserDTO {

    public String id;
    public String pw;
    public String name;

    public UserDTO()
    {

    }

    public UserDTO(String pw, String name) {
        this.pw = pw;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
