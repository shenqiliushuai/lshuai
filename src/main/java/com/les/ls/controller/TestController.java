package com.les.ls.controller;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.AbsSendMessageEntity;
import com.les.ls.pojo.vo.BaseWebResultVO;
import com.les.ls.utils.MD5Util;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin
public class TestController {
    @PostMapping("/detail")
    public BaseWebResultVO refund(@RequestBody(required = false) Map<String, Object> map, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerKey = headerNames.nextElement();
            headers.put(headerKey, request.getHeader(headerKey));
        }
        result.put("请求头为：", headers);
        result.put("请求体为：", map);
        return new BaseWebResultVO(WebResultEnum.SUCCESS, result);
    }

    @PostMapping("/sendMessage")
    public String sendMessage(AbsSendMessageEntity entity) throws Exception {
        String signStr = entity.getCheckSign() + "66ad33c4e31b2434c2c089ef0d04af53";
        String url = "https://cn-test-sdk.wan.com/extend-service/component/informationpush/push?"
                + "appId=" + entity.getAppId() + "&packageId=" + entity.getPackageId() +
                "&accountList=" + entity.getAccountList() + "&title=" + entity.getTitle() +
                "&message=" + entity.getMessage() + "&sign=" + MD5Util.createMD5(signStr);
        HttpConfig config = HttpConfig.custom();
        config.url(url);
        return HttpClientUtil.post(config);
    }
}
