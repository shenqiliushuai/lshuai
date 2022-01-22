package com.les.ls.controller;

import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.BaseWebResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @PostMapping("/refund")
    public BaseWebResultVO refund(@RequestBody(required = false) Map<String, Object> map, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerkey = headerNames.nextElement();
            headers.put(headerkey, request.getHeader(headerkey));
        }
        result.put("请求头为：", headers);
        result.put("请求体为：", map);
        return new BaseWebResultVO(WebResultEnum.SUCCESS, result);
    }
}
