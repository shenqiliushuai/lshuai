package com.les.ls.utils.conver;

import cn.hutool.core.convert.Converter;
import com.google.common.base.Strings;

public class AbsQQGameUserDetailConverter implements Converter<AbsQQGameUserDetail> {
    @Override
    public AbsQQGameUserDetail convert(Object o, AbsQQGameUserDetail qqGameUserDetail) throws IllegalArgumentException {
        AbsUserDetail userDetail = (AbsUserDetail) o;
        qqGameUserDetail = new AbsQQGameUserDetail();
        qqGameUserDetail.setAppId(userDetail.getAppId());
        qqGameUserDetail.setChannelId(userDetail.getChannelId());
        qqGameUserDetail.setThirdUserId(userDetail.getThirdUserId());
        qqGameUserDetail.setLoginMethod(userDetail.getLoginMethod());
        qqGameUserDetail.setChannelUserId(userDetail.getChannelUserId());
        qqGameUserDetail.setCreateTime(userDetail.getCreateTime());
        qqGameUserDetail.setLastLoginTime(userDetail.getLastLoginTime());

        qqGameUserDetail.setOpenId(Strings.isNullOrEmpty(userDetail.getColumn0()) ? "" : userDetail.getColumn0());
        qqGameUserDetail.setOpenKey(Strings.isNullOrEmpty(userDetail.getColumn1()) ? "" : userDetail.getColumn1());
        qqGameUserDetail.setPf(Strings.isNullOrEmpty(userDetail.getColumn2()) ? "" : userDetail.getColumn2());
        qqGameUserDetail.setPfKey(Strings.isNullOrEmpty(userDetail.getColumn3()) ? "" : userDetail.getColumn3());
        qqGameUserDetail.setPackageId(Strings.isNullOrEmpty(userDetail.getColumn4()) ? "" : userDetail.getColumn4());

        return qqGameUserDetail;
    }
}
