package com.les.ls.pojo.vo;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author shengqiang.wu
 * @date 2018/10/29/029 17:53
 * @desc
 */
@Data
public class AbsSendMessageEntity {

    private Integer appId;
    private Integer packageId;
    private String accountList;
    private String title;
    private String message;
    private Integer channelId;
    private String exInfo;
    private String sign;

    public String getCheckSign() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        params.put("packageId", packageId);
        params.put("accountList", accountList);
        params.put("title", title);
        params.put("message", message);
        return sortKeyNoNullValue(params);
    }

    public static String sortKeyNoNullValue(Map<String, Object> params) {
        List<String> keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < keys.size(); ++i) {
            String key = (String)keys.get(i);
            Object value = params.get(key);
            if (value != null) {
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
