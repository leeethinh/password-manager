package com.thinhle.password_manager;

import java.io.Serializable;


public class LoginInfo implements Serializable {
    private String companyTitle;
    private String username;
    private String password;

    public LoginInfo(String companyTitle, String username, String password) {
        this.companyTitle = companyTitle;
        this.username = username;
        this.password = password;

    }
    public String getCompanyTitle() {
        return companyTitle;
    }

    public String getUsername() {
        return username;
    }

    public String  getPassword() {
        return password;
    }




}



