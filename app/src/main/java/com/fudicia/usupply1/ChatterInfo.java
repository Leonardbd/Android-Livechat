package com.fudicia.usupply1;



public class ChatterInfo {

private String username;
private String email;

    public ChatterInfo() {
    }

    public ChatterInfo(String Username, String Email){

        username = Username;
        email = Email;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
