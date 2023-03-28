package com.les.ls.utils.conver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.ConverterRegistry;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
@AbsChannelUserDetail(channelId = 10000, userClass = AbsQQGameUserDetail.class)
public class AbsQQGameUserDetailImpl extends AbsChannelUserDetailService {
    private static final int CHANNEL_ID = 10000;
    private final Gson gson = new Gson();

    @Override
    @SuppressWarnings(value = "unchecked")
    public AbsQQGameUserDetail getUserDetail(AbsUserDetail userDetailInfo) {
        try {
            if (userDetailInfo == null) {
                return null;
            }
            return super.fillUserDetail(userDetailInfo, AbsQQGameUserDetail.class);
        } catch (Exception e) {
            log.error("[{} UserDetailImpl] 异常错误, param={}, msg={}", CHANNEL_ID, gson.toJson(userDetailInfo), e.getMessage());
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public AbsUserDetail fillChannelUserDetail(AbsUserDetail userDetail, Map<String, Object> channelDataMap) {
        try {
            Map<String, Object> params = BeanUtil.beanToMap(userDetail);
            channelDataMap.putAll(params);
            return super.fillUserDetail(channelDataMap, Map.class);
        } catch (Exception e) {
            log.error("[{} UserDetailImpl] 异常错误, param={}, dataMap={}, msg={}", CHANNEL_ID, gson.toJson(userDetail), gson.toJson(channelDataMap), e.getMessage());
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ConverterRegistry getConverterRegistry() {
        ConverterRegistry converterRegistry = new ConverterRegistry();
        converterRegistry.putCustom(AbsQQGameUserDetail.class, AbsQQGameUserDetailConverter.class);
        converterRegistry.putCustom(Map.class, AbsQQGameUserDetailReverseConverter.class);
        return converterRegistry;
    }
}
