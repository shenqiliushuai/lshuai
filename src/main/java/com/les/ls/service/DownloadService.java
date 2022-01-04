package com.les.ls.service;

import com.alibaba.fastjson.JSONObject;
import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;

public interface DownloadService {
    DownloadResult download(JfSetting setting, Placeholder placeholder);

    Boolean testPing();

    Boolean testTelnet();

    Boolean testConnect();

    void saveConfig();
}
