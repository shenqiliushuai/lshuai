package com.les.ls.controller;

import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.BaseWebResultVO;
import com.les.ls.service.UserService;
import com.les.ls.utils.TerminalUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 用户控制器
 *
 * @author lshuai
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @GetMapping
    public BaseWebResultVO getUser() {
        userService.login();
        return new BaseWebResultVO(WebResultEnum.SUCCESS, null);
    }

    @PostMapping
    public void file(@RequestParam("file") MultipartFile file) throws Exception {
        file.transferTo(new File(TerminalUtils.getWorkDir()));
    }


    @GetMapping("/test")
    public ModelAndView test(HttpServletResponse response) throws Exception {
        response.sendRedirect("https://www.baidu.com");
        return new ModelAndView(new RedirectView("https://www.baidu.com"));
    }
}
