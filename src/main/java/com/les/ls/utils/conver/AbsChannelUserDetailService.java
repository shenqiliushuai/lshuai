package com.les.ls.utils.conver;


import cn.hutool.core.convert.ConverterRegistry;

import java.util.Map;

/**
 * @author ruoxiao.li
 * @date 2022/10/17
 * @describe
 */
public abstract class AbsChannelUserDetailService {

    public abstract <T> T getUserDetail(AbsUserDetail userDetailInfo);

    public abstract AbsUserDetail fillChannelUserDetail(AbsUserDetail userDetail, Map<String, Object> channelDataMap);

    protected abstract ConverterRegistry getConverterRegistry();

    protected <T> T fillUserDetail(AbsUserDetail userDetailInfo, Class clazz) {
        return this.getConverterRegistry().convert(clazz, userDetailInfo, null, true);
    }

    protected <T> T fillUserDetail(Map map, Class clazz) {
        return this.getConverterRegistry().convert(clazz, map, null, true);
    }
}
