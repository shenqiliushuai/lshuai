package com.les.ls.controller;

import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.BaseWebResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * @author lshuai
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @GetMapping
    public BaseWebResultVO getUser() {
        return new BaseWebResultVO(WebResultEnum.SUCCESS, null);
    }
}
