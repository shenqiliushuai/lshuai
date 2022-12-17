package com.les.ls.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController extends BaseController{
    @PostMapping("/login")
    public String login(String username, String password) {
        return username + ":" + password + "success";
    }
}
