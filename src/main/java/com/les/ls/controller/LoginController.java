package com.les.ls.controller;

import com.les.ls.pojo.vo.BaseWebResultVO;
import com.les.ls.pojo.vo.LoginVO;
import com.les.ls.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public BaseWebResultVO login(@Validated LoginVO loginVO) {
        return userService.login(loginVO);
    }
}
